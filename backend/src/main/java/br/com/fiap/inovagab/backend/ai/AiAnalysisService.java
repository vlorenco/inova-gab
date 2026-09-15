package br.com.fiap.inovagab.backend.ai;

import br.com.fiap.inovagab.backend.exception.AiServiceException;
import br.com.fiap.inovagab.backend.model.AiAnalysis;
import br.com.fiap.inovagab.backend.model.Idea;
import br.com.fiap.inovagab.backend.model.Strategy;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Set;

/**
 * Funcionalidade Plus: pontuacao e priorizacao automatica das ideias.
 *
 * Monta o prompt, chama o Gemini e - importante - NAO confia no texto devolvido:
 * o JSON e validado campo a campo antes de virar a analise persistida.
 */
@Service
public class AiAnalysisService {

    private static final Logger log = LoggerFactory.getLogger(AiAnalysisService.class);

    private static final Set<String> VALID_RECOMMENDATIONS =
            Set.of("ALTA_PRIORIDADE", "MEDIA_PRIORIDADE", "BAIXA_PRIORIDADE", "REVISAR");

    private static final int MAX_SUMMARY_LENGTH = 1200;

    private final GeminiClient geminiClient;
    private final ObjectMapper objectMapper;

    public AiAnalysisService(GeminiClient geminiClient, ObjectMapper objectMapper) {
        this.geminiClient = geminiClient;
        this.objectMapper = objectMapper;
    }

    public AiAnalysis analyze(Idea idea, Strategy strategy, String requestedByUserId) {
        String prompt = buildPrompt(idea, strategy);
        String rawJson = geminiClient.generateJson(prompt);
        AiScoreResult result = parseAndValidate(rawJson);

        AiAnalysis analysis = new AiAnalysis();
        analysis.setScore(result.score());
        analysis.setImpactScore(result.impactScore());
        analysis.setFeasibilityScore(result.feasibilityScore());
        analysis.setInnovationScore(result.innovationScore());
        analysis.setStrategicAlignmentScore(result.strategicAlignmentScore());
        analysis.setRecommendation(result.recommendation());
        analysis.setSummary(result.summary());
        analysis.setAnalyzedAt(Instant.now());
        analysis.setAnalyzedBy(requestedByUserId);
        analysis.setModel(geminiClient.getModel());
        return analysis;
    }

    private String buildPrompt(Idea idea, Strategy strategy) {
        String strategyBlock = strategy == null
                ? "Nenhuma orientacao estrategica foi vinculada a esta ideia."
                : """
                  Titulo: %s
                  Descricao: %s
                  Categoria: %s
                  Campanha: %s
                  Vigente: %s
                  """.formatted(
                        nullSafe(strategy.getTitle()),
                        nullSafe(strategy.getDescription()),
                        nullSafe(strategy.getCategory()),
                        nullSafe(strategy.getCampaign()),
                        strategy.isActive() ? "sim" : "nao");

        return """
               Voce e um analista de inovacao corporativa do Grupo Aguia Branca (logistica e transporte).
               Avalie a ideia abaixo e devolva EXCLUSIVAMENTE um objeto JSON valido, sem markdown e sem texto extra.

               IDEIA
               Titulo: %s
               Problema: %s
               Solucao proposta: %s
               Area impactada: %s
               Beneficio esperado: %s

               ORIENTACAO ESTRATEGICA VINCULADA
               %s

               CRITERIOS (cada nota de 0 a 100)
               - impactScore: tamanho do impacto operacional e financeiro.
               - feasibilityScore: viabilidade tecnica, de custo e de prazo.
               - innovationScore: grau de inovacao frente ao que ja e praticado.
               - strategicAlignmentScore: aderencia a orientacao estrategica acima
                 (use 50 quando nenhuma orientacao estiver vinculada).
               - score: nota geral de 0 a 100 consolidando os criterios acima.

               FORMATO EXATO DA RESPOSTA
               {
                 "score": 87,
                 "impactScore": 90,
                 "feasibilityScore": 82,
                 "innovationScore": 85,
                 "strategicAlignmentScore": 91,
                 "recommendation": "ALTA_PRIORIDADE",
                 "summary": "Justificativa objetiva em portugues, no maximo 3 frases."
               }

               O campo recommendation deve ser um destes valores:
               ALTA_PRIORIDADE, MEDIA_PRIORIDADE, BAIXA_PRIORIDADE, REVISAR.
               """.formatted(
                nullSafe(idea.getTitle()),
                nullSafe(idea.getProblem()),
                nullSafe(idea.getSolution()),
                nullSafe(idea.getArea()),
                nullSafe(idea.getBenefit()),
                strategyBlock);
    }

    /** Nunca confiar cegamente no retorno da IA: valida tipo, faixa e dominio. */
    AiScoreResult parseAndValidate(String rawJson) {
        String cleaned = stripCodeFence(rawJson);
        JsonNode node;
        try {
            node = objectMapper.readTree(cleaned);
        } catch (Exception ex) {
            log.warn("JSON invalido devolvido pela IA: {}", abbreviate(cleaned));
            throw new AiServiceException("A IA devolveu uma resposta que nao pode ser interpretada.");
        }

        if (node == null || !node.isObject()) {
            throw new AiServiceException("A IA devolveu uma resposta que nao pode ser interpretada.");
        }

        int score = readScore(node, "score");
        int impact = readScore(node, "impactScore");
        int feasibility = readScore(node, "feasibilityScore");
        int innovation = readScore(node, "innovationScore");
        int alignment = readScore(node, "strategicAlignmentScore");

        String recommendation = node.path("recommendation").asText("").trim().toUpperCase();
        if (!VALID_RECOMMENDATIONS.contains(recommendation)) {
            recommendation = fallbackRecommendation(score);
        }

        String summary = node.path("summary").asText("").trim();
        if (summary.isEmpty()) {
            summary = "A IA nao forneceu uma justificativa textual para esta analise.";
        } else if (summary.length() > MAX_SUMMARY_LENGTH) {
            summary = summary.substring(0, MAX_SUMMARY_LENGTH);
        }

        return new AiScoreResult(score, impact, feasibility, innovation, alignment, recommendation, summary);
    }

    private int readScore(JsonNode node, String field) {
        JsonNode value = node.path(field);
        if (!value.isNumber()) {
            throw new AiServiceException("A IA devolveu a nota '" + field + "' em formato invalido.");
        }
        return Math.clamp(value.asInt(), 0, 100);
    }

    private String fallbackRecommendation(int score) {
        if (score >= 75) {
            return "ALTA_PRIORIDADE";
        }
        if (score >= 50) {
            return "MEDIA_PRIORIDADE";
        }
        return "BAIXA_PRIORIDADE";
    }

    /** Alguns modelos insistem em envolver o JSON em uma cerca de codigo markdown. */
    private String stripCodeFence(String raw) {
        String fence = "```";
        String text = raw == null ? "" : raw.trim();
        if (text.startsWith(fence)) {
            int firstBreak = text.indexOf('\n');
            if (firstBreak > -1) {
                text = text.substring(firstBreak + 1);
            }
            int lastFence = text.lastIndexOf(fence);
            if (lastFence > -1) {
                text = text.substring(0, lastFence);
            }
        }
        return text.trim();
    }

    private String abbreviate(String value) {
        if (value == null) {
            return "";
        }
        return value.length() <= 200 ? value : value.substring(0, 200) + "...";
    }

    private String nullSafe(String value) {
        return (value == null || value.isBlank()) ? "nao informado" : value;
    }
}
