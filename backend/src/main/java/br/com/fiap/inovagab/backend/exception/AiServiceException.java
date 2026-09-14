package br.com.fiap.inovagab.backend.exception;

import org.springframework.http.HttpStatus;

/**
 * Falha ao falar com o Gemini ou ao interpretar a resposta dele.
 * Vira 503 para o app: erro amigavel, sem derrubar o backend.
 */
public class AiServiceException extends ApiException {

    public AiServiceException(String message) {
        super(HttpStatus.SERVICE_UNAVAILABLE, message);
    }
}
