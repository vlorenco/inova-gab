package br.com.fiap.inovagab.backend.ai;

import br.com.fiap.inovagab.backend.exception.AiServiceException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.time.Duration;
import java.util.Map;

/**
 * Cliente HTTP do Google Gemini.
 *
 * A chave nunca sai do backend: o app Android fala com a nossa API, e so a nossa
 * API fala com o Gemini.
 */
@Component
public class GeminiClient {

    private static final Logger log = LoggerFactory.getLogger(GeminiClient.class);

    private final GeminiProperties properties;
    private final ObjectMapper objectMapper;
    private final RestClient restClient;

    public GeminiClient(GeminiProperties properties, ObjectMapper objectMapper) {
        this.properties = properties;
        this.objectMapper = objectMapper;

        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(Duration.ofSeconds(properties.getTimeoutSeconds()));
        factory.setReadTimeout(Duration.ofSeconds(properties.getTimeoutSeconds()));

        this.restClient = RestClient.builder()
                .baseUrl(properties.getBaseUrl())
                .requestFactory(factory)
                .build();
    }

    public boolean isConfigured() {
        return properties.isConfigured();
    }

    public String getModel() {
        return properties.getModel();
    }

    /**
     * Envia o prompt pedindo resposta em JSON e devolve o texto bruto retornado
     * pelo modelo. A validacao do conteudo fica no AiAnalysisService.
     */
    public String generateJson(String prompt) {
        if (!properties.isConfigured()) {
            throw new AiServiceException(
                    "Analise por IA indisponivel: configure a variavel de ambiente GEMINI_API_KEY no backend.");
        }

        Map<String, Object> body = Map.of(
                "contents", java.util.List.of(Map.of(
                        "role", "user",
                        "parts", java.util.List.of(Map.of("text", prompt))
                )),
                "generationConfig", Map.of(
                        "temperature", 0.2,
                        "responseMimeType", "application/json"
                )
        );

        String raw;
        try {
            raw = restClient.post()
                    .uri(uriBuilder -> uriBuilder
                            .path("/v1beta/models/{model}:generateContent")
                            .queryParam("key", properties.getApiKey())
                            .build(properties.getModel()))
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(body)
                    .retrieve()
                    .body(String.class);
        } catch (RestClientException ex) {
            log.warn("Falha ao chamar o Gemini (modelo {}): {}", properties.getModel(), ex.getMessage());
            throw new AiServiceException(
                    "Nao foi possivel falar com o servico de IA agora. Tente novamente em instantes.");
        }

        return extractText(raw);
    }

    /** Percorre candidates[0].content.parts[*].text da resposta do Gemini. */
    private String extractText(String raw) {
        if (raw == null || raw.isBlank()) {
            throw new AiServiceException("O servico de IA devolveu uma resposta vazia.");
        }
        try {
            JsonNode root = objectMapper.readTree(raw);

            JsonNode error = root.path("error");
            if (!error.isMissingNode()) {
                log.warn("Gemini retornou erro: {}", error.path("message").asText());
                throw new AiServiceException(
                        "O servico de IA recusou a requisicao: " + error.path("message").asText("erro desconhecido"));
            }

            JsonNode parts = root.path("candidates").path(0).path("content").path("parts");
            StringBuilder text = new StringBuilder();
            for (JsonNode part : parts) {
                text.append(part.path("text").asText(""));
            }

            if (text.isEmpty()) {
                throw new AiServiceException("O servico de IA nao retornou conteudo analisavel.");
            }
            return text.toString();
        } catch (AiServiceException ex) {
            throw ex;
        } catch (Exception ex) {
            log.warn("Resposta do Gemini fora do formato esperado", ex);
            throw new AiServiceException("Resposta do servico de IA em formato inesperado.");
        }
    }
}
