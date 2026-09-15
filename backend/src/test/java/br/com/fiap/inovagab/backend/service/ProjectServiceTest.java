package br.com.fiap.inovagab.backend.service;

import br.com.fiap.inovagab.backend.dto.project.ProjectRequest;
import br.com.fiap.inovagab.backend.dto.project.ProjectResponse;
import br.com.fiap.inovagab.backend.exception.BadRequestException;
import br.com.fiap.inovagab.backend.exception.ConflictException;
import br.com.fiap.inovagab.backend.model.Idea;
import br.com.fiap.inovagab.backend.model.IdeaStatus;
import br.com.fiap.inovagab.backend.model.Project;
import br.com.fiap.inovagab.backend.model.ProjectStatus;
import br.com.fiap.inovagab.backend.repository.ProjectRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.within;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ProjectServiceTest {

    @Mock
    private ProjectRepository projectRepository;
    @Mock
    private IdeaService ideaService;
    @Mock
    private StrategyService strategyService;

    private ProjectService projectService;

    @BeforeEach
    void setUp() {
        projectService = new ProjectService(projectRepository, ideaService, strategyService);
        when(projectRepository.save(any(Project.class))).thenAnswer(call -> call.getArgument(0));
    }

    private ProjectRequest request(String ideaId) {
        return new ProjectRequest(
                "Senha digital de patio", "Fila unica digital.", "Operacoes",
                ProjectStatus.EM_ANDAMENTO, "Piloto",
                20000.0, 50000.0, 8000.0, 25.0, "30/09/2026",
                ideaId, null
        );
    }

    private Idea approvedIdea() {
        Idea idea = new Idea();
        idea.setId("idea-1");
        idea.setStatus(IdeaStatus.APROVADA);
        idea.setOperatorId("op-1");
        idea.setStrategyId("strat-1");
        return idea;
    }

    // ── ROI ─────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("ROI = ((retorno - investimento) / investimento) * 100")
    void calculatesRoi() {
        assertThat(ProjectService.calculateRoi(20000, 50000)).isCloseTo(150.0, within(0.0001));
        assertThat(ProjectService.calculateRoi(10000, 25000)).isCloseTo(150.0, within(0.0001));
        assertThat(ProjectService.calculateRoi(18000, 46000)).isCloseTo(155.5555, within(0.001));
    }

    @Test
    @DisplayName("ROI negativo quando o retorno e menor que o investimento")
    void calculatesNegativeRoi() {
        assertThat(ProjectService.calculateRoi(10000, 4000)).isCloseTo(-60.0, within(0.0001));
    }

    @Test
    @DisplayName("investimento zero ou negativo nao divide por zero: ROI vale 0")
    void handlesZeroInvestment() {
        assertThat(ProjectService.calculateRoi(0, 50000)).isZero();
        assertThat(ProjectService.calculateRoi(-1, 50000)).isZero();
        assertThat(ProjectService.calculateRoi(0, 0)).isZero();
    }

    // ── Criacao ─────────────────────────────────────────────────────────────

    @Test
    @DisplayName("gestor cria projeto avulso com o ROI ja calculado na resposta")
    void createsStandaloneProject() {
        ProjectResponse response = projectService.create(request(null));

        assertThat(response.name()).isEqualTo("Senha digital de patio");
        assertThat(response.roi()).isCloseTo(150.0, within(0.0001));
        verify(ideaService, never()).markConvertedToProject(any());
    }

    @Test
    @DisplayName("projeto criado a partir de ideia aprovada herda a estrategia e marca a ideia")
    void createsProjectFromApprovedIdea() {
        Idea idea = approvedIdea();
        when(ideaService.findOrThrow("idea-1")).thenReturn(idea);
        when(projectRepository.findByIdeaId("idea-1")).thenReturn(Optional.empty());

        ProjectResponse response = projectService.create(request("idea-1"));

        assertThat(response.ideaId()).isEqualTo("idea-1");
        assertThat(response.strategyId()).isEqualTo("strat-1");
        verify(ideaService).markConvertedToProject(idea);
        // O vinculo validado e o herdado da ideia, nao o corpo vazio da requisicao.
        verify(strategyService).validateLink("strat-1", null);
    }

    @Test
    @DisplayName("ideia ainda nao aprovada nao pode virar projeto")
    void refusesIdeaNotApproved() {
        Idea idea = approvedIdea();
        idea.setStatus(IdeaStatus.EM_ANALISE);
        when(ideaService.findOrThrow("idea-1")).thenReturn(idea);

        assertThatThrownBy(() -> projectService.create(request("idea-1")))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("ideia aprovada");
    }

    @Test
    @DisplayName("a mesma ideia nao pode virar dois projetos")
    void refusesDuplicateConversion() {
        Project existing = new Project();
        existing.setId("proj-existente");
        existing.setName("Senha digital de patio");

        when(ideaService.findOrThrow("idea-1")).thenReturn(approvedIdea());
        when(projectRepository.findByIdeaId("idea-1")).thenReturn(Optional.of(existing));

        assertThatThrownBy(() -> projectService.create(request("idea-1")))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("ja foi convertida");
    }

    @Test
    @DisplayName("atualizar resultados mantem o projeto e recalcula o ROI")
    void updatesProjectResults() {
        Project existing = new Project();
        existing.setId("proj-1");
        existing.setName("Antigo");
        when(projectRepository.findById("proj-1")).thenReturn(Optional.of(existing));

        ProjectRequest updated = new ProjectRequest(
                "Senha digital de patio", null, null,
                ProjectStatus.CONCLUIDO, "Concluido",
                10000.0, 40000.0, 3000.0, 12.0, "01/12/2026",
                null, null
        );

        ProjectResponse response = projectService.update("proj-1", updated);

        assertThat(response.status()).isEqualTo("CONCLUIDO");
        assertThat(response.roi()).isCloseTo(300.0, within(0.0001));
    }
}
