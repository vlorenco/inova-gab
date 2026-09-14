package br.com.fiap.inovagab.backend.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "inovagab.jwt")
public class JwtProperties {

    /** Segredo HMAC. Vem sempre de variavel de ambiente (JWT_SECRET). */
    private String secret;

    /** Validade do token em milissegundos. */
    private long expirationMs = 86_400_000L;

    private String issuer = "inovagab-backend";

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public long getExpirationMs() {
        return expirationMs;
    }

    public void setExpirationMs(long expirationMs) {
        this.expirationMs = expirationMs;
    }

    public String getIssuer() {
        return issuer;
    }

    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }
}
