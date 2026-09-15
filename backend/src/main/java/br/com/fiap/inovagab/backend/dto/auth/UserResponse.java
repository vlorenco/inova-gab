package br.com.fiap.inovagab.backend.dto.auth;

import br.com.fiap.inovagab.backend.model.User;

/** Representacao publica do usuario: nunca carrega a senha. */
public record UserResponse(
        String id,
        String name,
        String email,
        String role,
        int points
) {
    public static UserResponse from(User user) {
        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole().name(),
                user.getPoints()
        );
    }
}
