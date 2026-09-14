package br.com.fiap.inovagab.backend.service;

import br.com.fiap.inovagab.backend.dto.dashboard.DashboardSummaryResponse;
import br.com.fiap.inovagab.backend.dto.dashboard.StrategyDashboardResponse;
import br.com.fiap.inovagab.backend.model.Idea;
import br.com.fiap.inovagab.backend.model.IdeaStatus;
import br.com.fiap.inovagab.backend.model.Project;
import br.com.fiap.inovagab.backend.model.ProjectStatus;
import br.com.fiap.inovagab.backend.model.Strategy;
import br.com.fiap.inovagab.backend.repository.IdeaRepository;
import br.com.fiap.inovagab.backend.repository.ProjectRepository;
import br.com.fiap.inovagab.backend.repository.StrategyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class DashboardServiceTest {

    @Mock
    private ProjectRepository projectRepository;
    @Mock
    private IdeaRepository ideaRepository;
    @Mock
    private StrategyRepository strategyRepository;
    @Mock
    private StrategyService strategyService;

    private DashboardService dashboardService;

    @BeforeEach
    void setUp() {
        dashboardService = new DashboardService(
                projectRepository, ideaRepository, strategyRepository, strategyService);
    }

    private Project project(ProjectStatus status, double investment, double financialReturn,
                            double costReduction, double productivity) {
        Project project = new Project();
        project.setStatus(status);
        project.setInvestment(investment);
        project.setFinancialReturn(financialReturn);
        project.setCostReduction(costReduction);
        project.setProductivityGain(productivity);
        return project;
    }

    @Test
    @DisplayName("summary consolida contagens, totais, lucro e ROI do portfolio")
    void buildsSummary() {
        when(projectRepository.findAll()).thenReturn(List.of(
                project(ProjectStatus.EM_ANDAMENTO, 10000, 25000, 5000, 18),
                project(ProjectStatus.CONCLUIDO, 8000, 21000, 6000, 15),
                project(ProjectStatus.PLANEJADO, 0, 0, 0, 0)
        ));
        when(ideaRepository.count()).thenReturn(5L);
        when(ideaRepository.countByStatus(IdeaStatus.APROVADA)).thenReturn(2L);
        when(ideaRepository.countByStatus(IdeaStatus.EM_ANALISE)).thenReturn(1L);
        when(strategyRepository.count()).thenReturn(4L);
        when(strategyRepository.findByActiveOrderByCreatedAtDesc(true))
                .thenReturn(List.of(new Strategy(), new Strategy(), new Strategy()));

        DashboardSummaryResponse summary = dashboardService.summary();

        assertThat(summary.totalProjects()).isEqualTo(3);
        assertThat(summary.activeProjects()).isEqualTo(1);
        assertThat(summary.completedProjects()).isEqualTo(1);
        assertThat(summary.plannedProjects()).isEqualTo(1);
        assertThat(summary.cancelledProjects()).isZero();

        assertThat(summary.totalInvestment()).isEqualTo(18000.0);
        assertThat(summary.totalFinancialReturn()).isEqualTo(46000.0);
        assertThat(summary.profit()).isEqualTo(28000.0);
        // ((46000 - 18000) / 18000) * 100
        assertThat(summary.roi()).isCloseTo(155.5555, within(0.001));
        assertThat(summary.totalCostReduction()).isEqualTo(11000.0);
        // Media inclui o projeto planejado com 0%: (18 + 15 + 0) / 3
        assertThat(summary.averageProductivityGain()).isCloseTo(11.0, within(0.001));

        assertThat(summary.totalIdeas()).isEqualTo(5);
        assertThat(summary.approvedIdeas()).isEqualTo(2);
        assertThat(summary.activeStrategies()).isEqualTo(3);
    }

    @Test
    @DisplayName("summary sem projetos nao quebra e devolve ROI zero")
    void handlesEmptyPortfolio() {
        when(projectRepository.findAll()).thenReturn(List.of());
        when(strategyRepository.findByActiveOrderByCreatedAtDesc(true)).thenReturn(List.of());

        DashboardSummaryResponse summary = dashboardService.summary();

        assertThat(summary.totalProjects()).isZero();
        assertThat(summary.roi()).isZero();
        assertThat(summary.profit()).isZero();
        assertThat(summary.averageProductivityGain()).isZero();
    }

    @Test
    @DisplayName("indicadores por estrategia somam apenas os projetos e ideias vinculados")
    void buildsStrategyDashboard() {
        Strategy strategy = new Strategy();
        strategy.setId("strat-1");
        strategy.setTitle("Automacao de patio");
        strategy.setActive(true);
        when(strategyService.findOrThrow("strat-1")).thenReturn(strategy);

        when(projectRepository.findByStrategyId("strat-1")).thenReturn(List.of(
                project(ProjectStatus.CONCLUIDO, 20000, 50000, 8000, 25)
        ));

        Idea approved = new Idea();
        approved.setStatus(IdeaStatus.APROVADA);
        Idea underAnalysis = new Idea();
        underAnalysis.setStatus(IdeaStatus.EM_ANALISE);
        when(ideaRepository.findByStrategyId("strat-1")).thenReturn(List.of(approved, underAnalysis));

        StrategyDashboardResponse response = dashboardService.byStrategy("strat-1");

        assertThat(response.strategyTitle()).isEqualTo("Automacao de patio");
        assertThat(response.totalIdeas()).isEqualTo(2);
        assertThat(response.approvedIdeas()).isEqualTo(1);
        assertThat(response.totalProjects()).isEqualTo(1);
        assertThat(response.completedProjects()).isEqualTo(1);
        assertThat(response.roi()).isCloseTo(150.0, within(0.001));
        assertThat(response.profit()).isEqualTo(30000.0);
    }
}
