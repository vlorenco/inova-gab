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
import org.springframework.web.client.RestClientResponseException;

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

    /** Tentativas totais quando o Gemini responde que esta sobrecarregado. */
    private static final int MAX_ATTEMPTS = 3;

    /** Espera base entre tentativas; cresce a cada rodada (1s, 2s). */
    private static final long RETRY_DELAY_MS = 1_000L;

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

        return extractText(postWithRetry(body));
    }

    /**
     * O Gemini devolve 503 UNAVAILABLE ("high demand") de forma intermitente, e
     * uma tentativa isolada perde a analise por um motivo que costuma passar em
     * segundos. Por isso 503 e 429 sao repetidos; erro de chave, de modelo ou de
     * cota nao adianta repetir e falha na hora.
     */
    private String postWithRetry(Map<String, Object> body) {
        for (int attempt = 1; attempt <= MAX_ATTEMPTS; attempt++) {
            try {
                return restClient.post()
                        .uri(uriBuilder -> uriBuilder
                                .path("/v1beta/models/{model}:generateContent")
                                .queryParam("key", properties.getApiKey())
                                .build(properties.getModel()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(body)
                        .retrieve()
                        .body(String.class);

            } catch (RestClientResponseException ex) {
                int status = ex.getStatusCode().value();
                String detail = errorMessageOf(ex.getResponseBodyAsString());

                if (!isTransient(status) || attempt == MAX_ATTEMPTS) {
                    // O Gemini respondeu, mas recusou. Engolir o motivo aqui deixaria
                    // o gestor sem saber se o problema e a chave, o modelo ou a cota.
                    log.warn("Gemini recusou a requisicao (modelo {}, HTTP {}): {}",
                            properties.getModel(), status, detail);
                    throw new AiServiceException(
                            "O servico de IA recusou a requisicao (HTTP " + status + "): " + detail);
                }

                log.info("Gemini indisponivel (HTTP {}), tentativa {}/{}: {}",
                        status, attempt, MAX_ATTEMPTS, detail);
                sleep(RETRY_DELAY_MS * attempt);

            } catch (RestClientException ex) {
                // Aqui e falha de transporte mesmo: DNS, timeout, conexao recusada.
                log.warn("Falha de rede ao chamar o Gemini (modelo {}): {}",
                        properties.getModel(), ex.getMessage());
                throw new AiServiceException(
                        "Nao foi possivel falar com o servico de IA agora. Tente novamente em instantes.");
            }
        }

        // Inalcancavel: a ultima tentativa do laco sempre retorna ou lanca.
        throw new AiServiceException("O servico de IA esta sobrecarregado. Tente novamente em instantes.");
    }

    private boolean isTransient(int status) {
        return status == 503 || status == 429 || status == 500;
    }

    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new AiServiceException("Analise por IA interrompida.");
        }
    }

    /** Extrai `error.message` do corpo de erro do Gemini, com fallback legivel. */
    private String errorMessageOf(String errorBody) {
        if (errorBody == null || errorBody.isBlank()) {
            return "sem detalhes na resposta";
        }
        try {
            String message = objectMapper.readTree(errorBody).path("error").path("message").asText("");
            if (!message.isBlank()) {
                return message;
            }
        } catch (Exception ignored) {
            // Corpo de erro fora do formato JSON esperado: cai no recorte bruto abaixo.
        }
        return errorBody.length() <= 300 ? errorBody : errorBody.substring(0, 300) + "...";
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
