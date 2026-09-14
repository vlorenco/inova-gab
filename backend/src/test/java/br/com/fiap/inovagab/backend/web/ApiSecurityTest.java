package br.com.fiap.inovagab.backend.web;

import br.com.fiap.inovagab.backend.controller.AuthController;
import br.com.fiap.inovagab.backend.controller.DashboardController;
import br.com.fiap.inovagab.backend.controller.IdeaController;
import br.com.fiap.inovagab.backend.controller.ProjectController;
import br.com.fiap.inovagab.backend.controller.StrategyController;
import br.com.fiap.inovagab.backend.dto.auth.LoginRequest;
import br.com.fiap.inovagab.backend.dto.auth.LoginResponse;
import br.com.fiap.inovagab.backend.dto.auth.UserResponse;
import br.com.fiap.inovagab.backend.dto.strategy.StrategyResponse;
import br.com.fiap.inovagab.backend.exception.GlobalExceptionHandler;
import br.com.fiap.inovagab.backend.model.Role;
import br.com.fiap.inovagab.backend.model.User;
import br.com.fiap.inovagab.backend.security.JwtAuthenticationFilter;
import br.com.fiap.inovagab.backend.security.JwtProperties;
import br.com.fiap.inovagab.backend.security.JwtService;
import br.com.fiap.inovagab.backend.security.RestAccessDeniedHandler;
import br.com.fiap.inovagab.backend.security.RestAuthenticationEntryPoint;
import br.com.fiap.inovagab.backend.security.SecurityConfig;
import br.com.fiap.inovagab.backend.service.AuthService;
import br.com.fiap.inovagab.backend.service.DashboardService;
import br.com.fiap.inovagab.backend.service.IdeaService;
import br.com.fiap.inovagab.backend.service.ProjectService;
import br.com.fiap.inovagab.backend.service.StrategyService;
import br.com.fiap.inovagab.backend.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Exercita as regras reais do SecurityConfig com tokens JWT reais.
 * Nada aqui e simulado do lado da seguranca: apenas os services sao mockados.
 */
@WebMvcTest(controllers = {
        AuthController.class,
        StrategyController.class,
        IdeaController.class,
        ProjectController.class,
        DashboardController.class
})
@Import({
        SecurityConfig.class,
        JwtAuthenticationFilter.class,
        JwtService.class,
        RestAuthenticationEntryPoint.class,
        RestAccessDeniedHandler.class,
        GlobalExceptionHandler.class,
        ApiSecurityTest.TestJwtConfig.class
})
class ApiSecurityTest {

    @TestConfiguration
    static class TestJwtConfig {
        @Bean
        JwtProperties jwtProperties() {
            JwtProperties properties = new JwtProperties();
            properties.setSecret("segredo-de-teste-inovagab-com-mais-de-32-caracteres");
            properties.setExpirationMs(3_600_000L);
            properties.setIssuer("inovagab-backend");
            return properties;
        }
    }

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private JwtService jwtService;

    @MockitoBean
    private AuthService authService;
    @MockitoBean
    private UserService userService;
    @MockitoBean
    private StrategyService strategyService;
    @MockitoBean
    private IdeaService ideaService;
    @MockitoBean
    private ProjectService projectService;
    @MockitoBean
    private DashboardService dashboardService;

    private String operatorToken;
    private String managerToken;
    private String leaderToken;

    private User user(String id, String name, String email, Role role) {
        User user = new User(name, email, "hash", role);
        user.setId(id);
        return user;
    }

    @BeforeEach
    void setUp() {
        operatorToken = jwtService.generateToken(user("op-1", "Operador Demo", "operador@app.com", Role.OPERADOR));
        managerToken = jwtService.generateToken(user("ge-1", "Gestor Demo", "gestor@app.com", Role.GESTOR));
        leaderToken = jwtService.generateToken(user("li-1", "Lideranca Demo", "lider@app.com", Role.LIDERANCA));
    }

    private String bearer(String token) {
        return "Bearer " + token;
    }

    // ── Login ───────────────────────────────────────────────────────────────

    @Test
    @DisplayName("login valido devolve 200 com token e usuario sem senha")
    void loginSucceeds() throws Exception {
        when(authService.login(any(LoginRequest.class))).thenReturn(new LoginResponse(
                "token-fake", "Bearer", 86_400_000L,
                new UserResponse("op-1", "Operador Demo", "operador@app.com", "OPERADOR", 0)
        ));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"operador@app.com","password":"123456"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("token-fake"))
                .andExpect(jsonPath("$.user.role").value("OPERADOR"))
                .andExpect(jsonPath("$.user.password").doesNotExist());
    }

    @Test
    @DisplayName("login com senha errada devolve 401 no formato padrao de erro")
    void loginFailsWithWrongPassword() throws Exception {
        when(authService.login(any(LoginRequest.class)))
                .thenThrow(new BadCredentialsException("Credenciais invalidas."));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"operador@app.com","password":"errada"}
                                """))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.message").value("E-mail ou senha incorretos."))
                .andExpect(jsonPath("$.path").value("/api/auth/login"));
    }

    @Test
    @DisplayName("login com e-mail invalido devolve 400 com o detalhe da validacao")
    void loginFailsValidation() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"nao-e-email","password":""}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.details").isArray());
    }

    // ── Sem token ───────────────────────────────────────────────────────────

    @Test
    @DisplayName("endpoint protegido sem token devolve 401")
    void protectedEndpointWithoutTokenIsUnauthorized() throws Exception {
        mockMvc.perform(get("/api/strategies"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401));

        mockMvc.perform(get("/api/ideas"))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(get("/api/dashboard/summary"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("token invalido tambem devolve 401")
    void invalidTokenIsUnauthorized() throws Exception {
        mockMvc.perform(get("/api/strategies").header("Authorization", "Bearer token-falsificado"))
                .andExpect(status().isUnauthorized());
    }

    // ── Roles ───────────────────────────────────────────────────────────────

    @Test
    @DisplayName("operador consulta estrategias, mas nao pode criar")
    void operatorReadsStrategiesButCannotWrite() throws Exception {
        when(strategyService.list(null)).thenReturn(List.<StrategyResponse>of());

        mockMvc.perform(get("/api/strategies").header("Authorization", bearer(operatorToken)))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/strategies")
                        .header("Authorization", bearer(operatorToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title":"Tentativa","description":"Nao deveria passar"}
                                """))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403));
    }

    @Test
    @DisplayName("operador nao acessa o dashboard da lideranca")
    void operatorCannotReachLeadershipDashboard() throws Exception {
        mockMvc.perform(get("/api/dashboard/summary").header("Authorization", bearer(operatorToken)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("operador nao consulta o historico de estrategias")
    void operatorCannotReadStrategyHistory() throws Exception {
        mockMvc.perform(get("/api/strategies/strat-1/history").header("Authorization", bearer(operatorToken)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("operador nao lista todas as ideias nem acessa projetos")
    void operatorIsLimitedToOwnIdeas() throws Exception {
        mockMvc.perform(get("/api/ideas").header("Authorization", bearer(operatorToken)))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/api/projects").header("Authorization", bearer(operatorToken)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("gestor lista todas as ideias e faz CRUD de projetos")
    void managerHandlesIdeasAndProjects() throws Exception {
        when(ideaService.listAll(null)).thenReturn(List.of());
        when(projectService.list()).thenReturn(List.of());

        mockMvc.perform(get("/api/ideas").header("Authorization", bearer(managerToken)))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/projects").header("Authorization", bearer(managerToken)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("gestor nao cria estrategia nem abre o dashboard da lideranca")
    void managerCannotActAsLeadership() throws Exception {
        mockMvc.perform(post("/api/strategies")
                        .header("Authorization", bearer(managerToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title":"Tentativa","description":"Nao deveria passar"}
                                """))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/api/dashboard/summary").header("Authorization", bearer(managerToken)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("lideranca consulta projetos e dashboard, mas nao cria projeto")
    void leadershipOnlyReadsProjects() throws Exception {
        when(projectService.list()).thenReturn(List.of());
        when(dashboardService.summary()).thenReturn(
                new br.com.fiap.inovagab.backend.dto.dashboard.DashboardSummaryResponse(
                        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));

        mockMvc.perform(get("/api/projects").header("Authorization", bearer(leaderToken)))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/dashboard/summary").header("Authorization", bearer(leaderToken)))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/projects")
                        .header("Authorization", bearer(leaderToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Nao deveria passar"}
                                """))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("lideranca faz CRUD completo de estrategias")
    void leadershipManagesStrategies() throws Exception {
        when(strategyService.create(any(), any())).thenReturn(
                new StrategyResponse("strat-1", "Automacao de patio", "Descricao",
                        "01/02/2026", "Operacoes", "Inova 2026", true, null, null, "li-1"));

        mockMvc.perform(post("/api/strategies")
                        .header("Authorization", bearer(leaderToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title":"Automacao de patio","description":"Descricao","active":true}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("strat-1"))
                .andExpect(jsonPath("$.active").value(true));
    }

    @Test
    @DisplayName("apenas o gestor dispara a analise por IA")
    void onlyManagerTriggersAiAnalysis() throws Exception {
        mockMvc.perform(post("/api/ideas/idea-1/ai-analysis").header("Authorization", bearer(operatorToken)))
                .andExpect(status().isForbidden());

        mockMvc.perform(post("/api/ideas/idea-1/ai-analysis").header("Authorization", bearer(leaderToken)))
                .andExpect(status().isForbidden());
    }
}
