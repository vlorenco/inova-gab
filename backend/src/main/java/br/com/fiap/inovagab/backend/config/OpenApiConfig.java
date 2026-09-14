package br.com.fiap.inovagab.backend.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI inovagabOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("InovaGAB API")
                        .version("1.0.0")
                        .description("""
                                API REST da plataforma de inovacao InovaGAB (FIAP / Grupo Aguia Branca).

                                Autenticacao: faca POST em /api/auth/login, copie o campo token e clique
                                em Authorize informando apenas o token (o prefixo Bearer e adicionado
                                automaticamente).

                                Usuarios de demonstracao (senha 123456):
                                operador@app.com (OPERADOR), gestor@app.com (GESTOR), lider@app.com (LIDERANCA).
                                """)
                        .contact(new Contact().name("Equipe InovaGAB - FIAP")))
                .components(new Components().addSecuritySchemes("bearerAuth",
                        new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("JWT obtido em POST /api/auth/login")));
    }
}
