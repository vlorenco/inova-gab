package br.com.fiap.inovagab.backend.security;

import br.com.fiap.inovagab.backend.model.Role;
import br.com.fiap.inovagab.backend.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Optional;

/** Geracao e leitura dos tokens JWT (HS256). */
@Service
public class JwtService {

    private final JwtProperties properties;
    private final SecretKey key;

    public JwtService(JwtProperties properties) {
        this.properties = properties;
        String secret = properties.getSecret();
        if (secret == null || secret.getBytes(StandardCharsets.UTF_8).length < 32) {
            throw new IllegalStateException(
                    "JWT_SECRET ausente ou muito curto: defina ao menos 32 caracteres na variavel de ambiente JWT_SECRET.");
        }
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(User user) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + properties.getExpirationMs());
        return Jwts.builder()
                .subject(user.getId())
                .issuer(properties.getIssuer())
                .claim("email", user.getEmail())
                .claim("name", user.getName())
                .claim("role", user.getRole().name())
                .issuedAt(now)
                .expiration(expiration)
                .signWith(key)
                .compact();
    }

    /** Devolve o principal do token, ou vazio se o token for invalido/expirado. */
    public Optional<AuthenticatedUser> parseToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(key)
                    .requireIssuer(properties.getIssuer())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            Role role = Role.valueOf(claims.get("role", String.class));
            return Optional.of(new AuthenticatedUser(
                    claims.getSubject(),
                    claims.get("name", String.class),
                    claims.get("email", String.class),
                    role
            ));
        } catch (JwtException | IllegalArgumentException ex) {
            return Optional.empty();
        }
    }

    public long getExpirationMs() {
        return properties.getExpirationMs();
    }
}
