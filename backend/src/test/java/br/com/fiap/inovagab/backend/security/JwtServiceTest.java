package br.com.fiap.inovagab.backend.security;

import br.com.fiap.inovagab.backend.model.Role;
import br.com.fiap.inovagab.backend.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JwtServiceTest {

    private static final String SECRET = "segredo-de-teste-inovagab-com-mais-de-32-caracteres";

    private JwtService serviceWithSecret(String secret) {
        JwtProperties properties = new JwtProperties();
        properties.setSecret(secret);
        properties.setExpirationMs(3_600_000L);
        properties.setIssuer("inovagab-backend");
        return new JwtService(properties);
    }

    private User operator() {
        User user = new User("Operador Demo", "operador@app.com", "hash", Role.OPERADOR);
        user.setId("op-1");
        return user;
    }

    @Test
    @DisplayName("token gerado carrega id, nome, e-mail e role do usuario")
    void generatesAndParsesToken() {
        JwtService service = serviceWithSecret(SECRET);

        String token = service.generateToken(operator());
        Optional<AuthenticatedUser> parsed = service.parseToken(token);

        assertThat(parsed).isPresent();
        assertThat(parsed.get().getId()).isEqualTo("op-1");
        assertThat(parsed.get().getName()).isEqualTo("Operador Demo");
        assertThat(parsed.get().getEmail()).isEqualTo("operador@app.com");
        assertThat(parsed.get().getRole()).isEqualTo(Role.OPERADOR);
    }

    @Test
    @DisplayName("token assinado com outro segredo e rejeitado")
    void rejectsTokenSignedWithAnotherSecret() {
        String token = serviceWithSecret(SECRET).generateToken(operator());

        Optional<AuthenticatedUser> parsed =
                serviceWithSecret("outro-segredo-completamente-diferente-com-32-chars").parseToken(token);

        assertThat(parsed).isEmpty();
    }

    @Test
    @DisplayName("token corrompido e rejeitado sem lancar excecao")
    void rejectsMalformedToken() {
        assertThat(serviceWithSecret(SECRET).parseToken("nao-e-um-jwt")).isEmpty();
    }

    @Test
    @DisplayName("segredo curto derruba a inicializacao em vez de aceitar configuracao insegura")
    void refusesWeakSecret() {
        assertThatThrownBy(() -> serviceWithSecret("curto"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("JWT_SECRET");
    }
}
