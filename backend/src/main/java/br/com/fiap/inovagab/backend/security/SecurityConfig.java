package br.com.fiap.inovagab.backend.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * Autorizacao por rota + role. Esta e a unica fonte de verdade das permissoes:
 * esconder um botao no Android nao e seguranca.
 */
@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private static final String OPERADOR = "OPERADOR";
    private static final String GESTOR = "GESTOR";
    private static final String LIDERANCA = "LIDERANCA";

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final RestAuthenticationEntryPoint authenticationEntryPoint;
    private final RestAccessDeniedHandler accessDeniedHandler;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter,
                          RestAuthenticationEntryPoint authenticationEntryPoint,
                          RestAccessDeniedHandler accessDeniedHandler) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.authenticationEntryPoint = authenticationEntryPoint;
        this.accessDeniedHandler = accessDeniedHandler;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(handling -> handling
                        .authenticationEntryPoint(authenticationEntryPoint)
                        .accessDeniedHandler(accessDeniedHandler))
                .authorizeHttpRequests(auth -> auth
                        // Publico
                        .requestMatchers("/api/auth/login").permitAll()
                        .requestMatchers("/actuator/health", "/api/health").permitAll()
                        .requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**").permitAll()

                        // Estrategias: todos consultam, so a lideranca escreve
                        .requestMatchers(HttpMethod.GET, "/api/strategies/*/history").hasRole(LIDERANCA)
                        .requestMatchers(HttpMethod.GET, "/api/strategies", "/api/strategies/**")
                            .hasAnyRole(OPERADOR, GESTOR, LIDERANCA)
                        .requestMatchers(HttpMethod.POST, "/api/strategies").hasRole(LIDERANCA)
                        .requestMatchers(HttpMethod.PUT, "/api/strategies/**").hasRole(LIDERANCA)
                        .requestMatchers(HttpMethod.DELETE, "/api/strategies/**").hasRole(LIDERANCA)

                        // Ideias
                        .requestMatchers(HttpMethod.GET, "/api/ideas/my").hasRole(OPERADOR)
                        .requestMatchers(HttpMethod.POST, "/api/ideas").hasRole(OPERADOR)
                        .requestMatchers(HttpMethod.GET, "/api/ideas").hasRole(GESTOR)
                        .requestMatchers(HttpMethod.POST, "/api/ideas/*/ai-analysis").hasRole(GESTOR)
                        .requestMatchers(HttpMethod.PATCH, "/api/ideas/*/priority").hasRole(GESTOR)
                        .requestMatchers(HttpMethod.PATCH, "/api/ideas/*/status").hasRole(GESTOR)
                        .requestMatchers(HttpMethod.GET, "/api/ideas/**").hasAnyRole(OPERADOR, GESTOR)
                        .requestMatchers(HttpMethod.PUT, "/api/ideas/**").hasRole(OPERADOR)
                        .requestMatchers(HttpMethod.DELETE, "/api/ideas/**").hasRole(OPERADOR)

                        // Projetos: gestor faz CRUD, lideranca consulta
                        .requestMatchers(HttpMethod.GET, "/api/projects", "/api/projects/**")
                            .hasAnyRole(GESTOR, LIDERANCA)
                        .requestMatchers(HttpMethod.POST, "/api/projects").hasRole(GESTOR)
                        .requestMatchers(HttpMethod.PUT, "/api/projects/**").hasRole(GESTOR)
                        .requestMatchers(HttpMethod.DELETE, "/api/projects/**").hasRole(GESTOR)

                        // Dashboard: exclusivo da lideranca, com uma excecao.
                        // A curadoria e o painel de trabalho do gestor e nao expoe
                        // numero financeiro nenhum - por isso sai da regra geral.
                        // Precisa vir antes do /** ou o matcher amplo vence.
                        .requestMatchers(HttpMethod.GET, "/api/dashboard/curation")
                            .hasAnyRole(GESTOR, LIDERANCA)
                        // Desempenho proprio: so devolve dados do dono do token.
                        .requestMatchers(HttpMethod.GET, "/api/dashboard/my-performance")
                            .hasRole(OPERADOR)
                        .requestMatchers("/api/dashboard/**").hasRole(LIDERANCA)

                        // Ranking: operador e gestor consultam
                        .requestMatchers("/api/ranking/**").hasAnyRole(OPERADOR, GESTOR, LIDERANCA)

                        .anyRequest().authenticated())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /** Liberado em dev para facilitar testes via Swagger/emulador. */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(List.of("*"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
