package br.com.fiap.inovagab.backend.dto.auth;

public record LoginResponse(
        String token,
        String tokenType,
        long expiresInMs,
        UserResponse user
) {
}
