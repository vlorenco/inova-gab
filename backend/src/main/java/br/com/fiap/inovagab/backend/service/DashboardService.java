package br.com.fiap.inovagab.backend.service;

import br.com.fiap.inovagab.backend.dto.dashboard.DashboardSummaryResponse;
import br.com.fiap.inovagab.backend.dto.dashboard.ProjectDashboardResponse;
import br.com.fiap.inovagab.backend.dto.dashboard.StrategyDashboardResponse;
import br.com.fiap.inovagab.backend.model.Idea;
import br.com.fiap.inovagab.backend.model.IdeaStatus;
import br.com.fiap.inovagab.backend.model.Project;
import br.com.fiap.inovagab.backend.model.ProjectStatus;
import br.com.fiap.inovagab.backend.model.Strategy;
import br.com.fiap.inovagab.backend.repository.IdeaRepository;
import br.com.fiap.inovagab.backend.repository.ProjectRepository;
import br.com.fiap.inovagab.backend.repository.StrategyRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Relatorios da lideranca. Na Sprint 1 estas contas eram feitas dentro do app;
 * agora saem prontas do backend.
 */
@Service
public class DashboardService {

    private final ProjectRepository projectRepository;
    private final IdeaRepository ideaRepository;
    private final StrategyRepository strategyRepository;
    private final StrategyService strategyService;

    public DashboardService(ProjectRepository projectRepository,
                            IdeaRepository ideaRepository,
                            StrategyRepository strategyRepository,
                            StrategyService strategyService) {
        this.projectRepository = projectRepository;
        this.ideaRepository = ideaRepository;
        this.strategyRepository = strategyRepository;
        this.strategyService = strategyService;
    }

    public DashboardSummaryResponse summary() {
        List<Project> projects = projectRepository.findAll();
        Totals totals = Totals.of(projects);

        return new DashboardSummaryResponse(
                projects.size(),
                countByStatus(projects, ProjectStatus.EM_ANDAMENTO),
                countByStatus(projects, ProjectStatus.CONCLUIDO),
                countByStatus(projects, ProjectStatus.PLANEJADO),
                countByStatus(projects, ProjectStatus.CANCELADO),
                totals.investment(),
                totals.financialReturn(),
                totals.profit(),
                totals.roi(),
                totals.costReduction(),
                totals.averageProductivityGain(),
                ideaRepository.count(),
                ideaRepository.countByStatus(IdeaStatus.APROVADA),
                ideaRepository.countByStatus(IdeaStatus.EM_ANALISE),
                strategyRepository.count(),
                strategyRepository.findByActiveOrderByCreatedAtDesc(true).size()
        );
    }

    public StrategyDashboardResponse byStrategy(String strategyId) {
        Strategy strategy = strategyService.findOrThrow(strategyId);

        List<Project> projects = projectRepository.findByStrategyId(strategyId);
        List<Idea> ideas = ideaRepository.findByStrategyId(strategyId);
        Totals totals = Totals.of(projects);

        return new StrategyDashboardResponse(
                strategy.getId(),
                strategy.getTitle(),
                strategy.isActive(),
                ideas.size(),
                ideas.stream().filter(idea -> idea.getStatus() == IdeaStatus.APROVADA).count(),
                projects.size(),
                countByStatus(projects, ProjectStatus.CONCLUIDO),
                totals.investment(),
                totals.financialReturn(),
                totals.profit(),
                totals.roi(),
                totals.costReduction(),
                totals.averageProductivityGain()
        );
    }

    public ProjectDashboardResponse byProject(String projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new br.com.fiap.inovagab.backend.exception.NotFoundException("Projeto nao encontrado."));

        double profit = project.getFinancialReturn() - project.getInvestment();
        double roi = ProjectService.calculateRoi(project.getInvestment(), project.getFinancialReturn());

        String ideaTitle = null;
        if (project.getIdeaId() != null && !project.getIdeaId().isBlank()) {
            ideaTitle = ideaRepository.findById(project.getIdeaId()).map(Idea::getTitle).orElse(null);
        }

        return new ProjectDashboardResponse(
                project.getId(),
                project.getName(),
                project.getStatus().name(),
                project.getCurrentStage(),
                project.getStrategyId(),
                strategyService.titleOf(project.getStrategyId()),
                project.getIdeaId(),
                ideaTitle,
                project.getInvestment(),
                project.getFinancialReturn(),
                profit,
                roi,
                project.getCostReduction(),
                project.getProductivityGain(),
                project.getDeadline()
        );
    }

    private long countByStatus(List<Project> projects, ProjectStatus status) {
        return projects.stream().filter(project -> project.getStatus() == status).count();
    }

    /** Agregacao dos indicadores financeiros de um conjunto de projetos. */
    private record Totals(double investment, double financialReturn, double costReduction,
                          double averageProductivityGain) {

        static Totals of(List<Project> projects) {
            double investment = projects.stream().mapToDouble(Project::getInvestment).sum();
            double financialReturn = projects.stream().mapToDouble(Project::getFinancialReturn).sum();
            double costReduction = projects.stream().mapToDouble(Project::getCostReduction).sum();
            double productivity = projects.isEmpty()
                    ? 0.0
                    : projects.stream().mapToDouble(Project::getProductivityGain).average().orElse(0.0);
            return new Totals(investment, financialReturn, costReduction, productivity);
        }

        double profit() {
            return financialReturn - investment;
        }

        double roi() {
            return ProjectService.calculateRoi(investment, financialReturn);
        }
    }
}
