package br.com.fiap.inovagab.backend.ai;

import br.com.fiap.inovagab.backend.exception.AiServiceException;
import br.com.fiap.inovagab.backend.model.AiAnalysis;
import br.com.fiap.inovagab.backend.model.Idea;
import br.com.fiap.inovagab.backend.model.Strategy;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

/**
 * A regra aqui e "nao confiar cegamente na IA": estes testes cobrem justamente
 * as respostas malformadas, fora de faixa e fora do dominio.
 */
@ExtendWith(MockitoExtension.class)
class AiAnalysisServiceTest {

    @Mock
    private GeminiClient geminiClient;

    private AiAnalysisService service;

    @BeforeEach
    void setUp() {
        service = new AiAnalysisService(geminiClient, new ObjectMapper());
    }

    private Idea idea() {
        Idea idea = new Idea();
        idea.setTitle("Fila unica de carregamento");
        idea.setProblem("Filas paralelas sem ordem definida.");
        idea.setSolution("Senha digital com ordem de chegada.");
        idea.setArea("Patio");
        idea.setBenefit("Reducao do tempo de espera.");
        return idea;
    }

    @Test
    @DisplayName("resposta JSON valida vira uma analise persistivel")
    void parsesValidResponse() {
        when(geminiClient.generateJson(anyString())).thenReturn("""
                {"score":87,"impactScore":90,"feasibilityScore":82,"innovationScore":85,
                 "strategicAlignmentScore":91,"recommendation":"ALTA_PRIORIDADE",
                 "summary":"Alto impacto operacional com implantacao viavel."}
                """);
        when(geminiClient.getModel()).thenReturn("gemini-3.5-flash");

        AiAnalysis analysis = service.analyze(idea(), null, "ge-1");

        assertThat(analysis.getScore()).isEqualTo(87);
        assertThat(analysis.getImpactScore()).isEqualTo(90);
        assertThat(analysis.getStrategicAlignmentScore()).isEqualTo(91);
        assertThat(analysis.getRecommendation()).isEqualTo("ALTA_PRIORIDADE");
        assertThat(analysis.getAnalyzedAt()).isNotNull();
        assertThat(analysis.getAnalyzedBy()).isEqualTo("ge-1");
        assertThat(analysis.getModel()).isEqualTo("gemini-3.5-flash");
    }

    @Test
    @DisplayName("JSON envolvido em cerca markdown ainda e aceito")
    void stripsMarkdownCodeFence() {
        AiScoreResult result = service.parseAndValidate(
                "```json\n{\"score\":70,\"impactScore\":70,\"feasibilityScore\":70,"
                        + "\"innovationScore\":70,\"strategicAlignmentScore\":70,"
                        + "\"recommendation\":\"MEDIA_PRIORIDADE\",\"summary\":\"ok\"}\n```");

        assertThat(result.score()).isEqualTo(70);
        assertThat(result.recommendation()).isEqualTo("MEDIA_PRIORIDADE");
    }

    @Test
    @DisplayName("notas fora de 0-100 sao limitadas em vez de aceitas")
    void clampsScoresOutOfRange() {
        AiScoreResult result = service.parseAndValidate("""
                {"score":180,"impactScore":-40,"feasibilityScore":50,"innovationScore":50,
                 "strategicAlignmentScore":50,"recommendation":"ALTA_PRIORIDADE","summary":"x"}
                """);

        assertThat(result.score()).isEqualTo(100);
        assertThat(result.impactScore()).isZero();
    }

    @Test
    @DisplayName("recommendation fora do dominio cai para o valor derivado do score")
    void fallsBackOnUnknownRecommendation() {
        AiScoreResult high = service.parseAndValidate("""
                {"score":90,"impactScore":90,"feasibilityScore":90,"innovationScore":90,
                 "strategicAlignmentScore":90,"recommendation":"URGENTISSIMO","summary":"x"}
                """);
        AiScoreResult low = service.parseAndValidate("""
                {"score":20,"impactScore":20,"feasibilityScore":20,"innovationScore":20,
                 "strategicAlignmentScore":20,"recommendation":"","summary":"x"}
                """);

        assertThat(high.recommendation()).isEqualTo("ALTA_PRIORIDADE");
        assertThat(low.recommendation()).isEqualTo("BAIXA_PRIORIDADE");
    }

    @Test
    @DisplayName("nota em formato nao numerico e recusada com erro de IA")
    void rejectsNonNumericScore() {
        assertThatThrownBy(() -> service.parseAndValidate("""
                {"score":"muito alto","impactScore":90,"feasibilityScore":90,"innovationScore":90,
                 "strategicAlignmentScore":90,"recommendation":"ALTA_PRIORIDADE","summary":"x"}
                """))
                .isInstanceOf(AiServiceException.class)
                .hasMessageContaining("score");
    }

    @Test
    @DisplayName("resposta que nao e JSON e recusada sem derrubar a aplicacao")
    void rejectsNonJsonResponse() {
        assertThatThrownBy(() -> service.parseAndValidate("Desculpe, nao consigo analisar."))
                .isInstanceOf(AiServiceException.class)
                .hasMessageContaining("nao pode ser interpretada");
    }

    @Test
    @DisplayName("summary ausente recebe um texto padrao em vez de vazio")
    void fillsMissingSummary() {
        AiScoreResult result = service.parseAndValidate("""
                {"score":60,"impactScore":60,"feasibilityScore":60,"innovationScore":60,
                 "strategicAlignmentScore":60,"recommendation":"MEDIA_PRIORIDADE"}
                """);

        assertThat(result.summary()).isNotBlank();
    }

    @Test
    @DisplayName("o prompt inclui os dados da estrategia vinculada")
    void promptMentionsLinkedStrategy() {
        Strategy strategy = new Strategy();
        strategy.setTitle("Automacao de patio");
        strategy.setDescription("Reduzir movimentacoes manuais.");
        strategy.setActive(true);

        when(geminiClient.generateJson(anyString())).thenAnswer(call -> {
            String prompt = call.getArgument(0);
            assertThat(prompt).contains("Automacao de patio");
            assertThat(prompt).contains("Fila unica de carregamento");
            return """
                   {"score":80,"impactScore":80,"feasibilityScore":80,"innovationScore":80,
                    "strategicAlignmentScore":80,"recommendation":"ALTA_PRIORIDADE","summary":"ok"}
                   """;
        });

        assertThat(service.analyze(idea(), strategy, "ge-1").getScore()).isEqualTo(80);
    }
}
