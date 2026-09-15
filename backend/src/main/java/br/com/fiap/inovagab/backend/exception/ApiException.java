package br.com.fiap.inovagab.backend.exception;

import org.springframework.http.HttpStatus;

/**
 * Excecao de negocio com status HTTP explicito. Todas as regras do dominio
 * lancam subclasses disto para que o handler global traduza no formato padrao.
 */
public class ApiException extends RuntimeException {

    private final HttpStatus status;

    public ApiException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
