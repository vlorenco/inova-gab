package br.com.fiap.inovagab.backend.security;

import br.com.fiap.inovagab.backend.exception.ApiException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/** Atalho para ler o usuario autenticado a partir do SecurityContext. */
public final class CurrentUser {

    private CurrentUser() {
    }

    public static AuthenticatedUser require() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof AuthenticatedUser user)) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "Usuario nao autenticado.");
        }
        return user;
    }
}
