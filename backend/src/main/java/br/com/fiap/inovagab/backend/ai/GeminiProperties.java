package br.com.fiap.inovagab.backend.ai;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "inovagab.gemini")
public class GeminiProperties {

    /** Chave da API. Sempre vem de GEMINI_API_KEY - nunca fica no codigo nem no app. */
    private String apiKey;

    /** Modelo configuravel para nao depender de um nome que pode ser depreciado. */
    private String model = "gemini-3.5-flash";

    private String baseUrl = "https://generativelanguage.googleapis.com";

    private int timeoutSeconds = 30;

    public boolean isConfigured() {
        return apiKey != null && !apiKey.isBlank();
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public int getTimeoutSeconds() {
        return timeoutSeconds;
    }

    public void setTimeoutSeconds(int timeoutSeconds) {
        this.timeoutSeconds = timeoutSeconds;
    }
}
