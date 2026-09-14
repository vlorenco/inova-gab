package br.com.fiap.inovagab.backend.service;

import br.com.fiap.inovagab.backend.dto.project.ProjectRequest;
import br.com.fiap.inovagab.backend.dto.project.ProjectResponse;
import br.com.fiap.inovagab.backend.exception.BadRequestException;
import br.com.fiap.inovagab.backend.exception.ConflictException;
import br.com.fiap.inovagab.backend.exception.NotFoundException;
import br.com.fiap.inovagab.backend.model.Idea;
import br.com.fiap.inovagab.backend.model.IdeaStatus;
import br.com.fiap.inovagab.backend.model.Project;
import br.com.fiap.inovagab.backend.model.ProjectStatus;
import br.com.fiap.inovagab.backend.repository.ProjectRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final IdeaService ideaService;
    private final StrategyService strategyService;

    public ProjectService(ProjectRepository projectRepository,
                          IdeaService ideaService,
                          StrategyService strategyService) {
        this.projectRepository = projectRepository;
        this.ideaService = ideaService;
        this.strategyService = strategyService;
    }

    public List<ProjectResponse> list() {
        return projectRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(this::toResponse)
                .toList();
    }

    public ProjectResponse getById(String id) {
        return toResponse(findOrThrow(id));
    }

    public Project findOrThrow(String id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Projeto nao encontrado."));
    }

    public ProjectResponse create(ProjectRequest request) {
        strategyService.validateExists(request.strategyId());

        Project project = new Project();
        applyFields(project, request);
        project.setCreatedAt(Instant.now());
        project.setUpdatedAt(project.getCreatedAt());

        Idea sourceIdea = resolveSourceIdea(request.ideaId(), null);
        if (sourceIdea != null) {
            project.setIdeaId(sourceIdea.getId());
            // Se o gestor nao escolheu estrategia, herda a da ideia de origem.
            if (project.getStrategyId() == null) {
                project.setStrategyId(sourceIdea.getStrategyId());
            }
        }

        Project saved = projectRepository.save(project);

        if (sourceIdea != null) {
            ideaService.markConvertedToProject(sourceIdea);
        }

        return toResponse(saved);
    }

    public ProjectResponse update(String id, ProjectRequest request) {
        Project project = findOrThrow(id);
        strategyService.validateExists(request.strategyId());

        Idea sourceIdea = resolveSourceIdea(request.ideaId(), project.getId());

        applyFields(project, request);
        project.setUpdatedAt(Instant.now());

        if (sourceIdea != null) {
            project.setIdeaId(sourceIdea.getId());
        }

        Project saved = projectRepository.save(project);

        if (sourceIdea != null) {
            ideaService.markConvertedToProject(sourceIdea);
        }

        return toResponse(saved);
    }

    public void delete(String id) {
        Project project = findOrThrow(id);
        projectRepository.deleteById(project.getId());
    }

    /**
     * ROI = ((retorno - investimento) / investimento) * 100.
     * Investimento zero nao gera divisao por zero: sem capital aplicado nao ha
     * retorno percentual a medir, entao o indicador vale 0.
     */
    public static double calculateRoi(double investment, double financialReturn) {
        if (investment <= 0) {
            return 0.0;
        }
        return ((financialReturn - investment) / investment) * 100.0;
    }

    // ── Apoio ───────────────────────────────────────────────────────────────

    /**
     * Valida a ideia de origem: precisa existir, estar aprovada e ainda nao
     * pertencer a outro projeto.
     */
    private Idea resolveSourceIdea(String ideaId, String currentProjectId) {
        if (ideaId == null || ideaId.isBlank()) {
            return null;
        }

        Idea idea = ideaService.findOrThrow(ideaId);

        if (idea.getStatus() != IdeaStatus.APROVADA) {
            throw new BadRequestException("So e possivel criar projeto a partir de uma ideia aprovada.");
        }

        projectRepository.findByIdeaId(ideaId).ifPresent(existing -> {
            if (!existing.getId().equals(currentProjectId)) {
                throw new ConflictException("Esta ideia ja foi convertida no projeto: " + existing.getName());
            }
        });

        return idea;
    }

    private void applyFields(Project project, ProjectRequest request) {
        project.setName(request.name().trim());
        project.setDescription(trimOrNull(request.description()));
        project.setResponsible(trimOrNull(request.responsible()));
        project.setStatus(request.status() == null ? ProjectStatus.PLANEJADO : request.status());
        project.setCurrentStage(trimOrNull(request.currentStage()));
        project.setInvestment(orZero(request.investment()));
        project.setFinancialReturn(orZero(request.financialReturn()));
        project.setCostReduction(orZero(request.costReduction()));
        project.setProductivityGain(orZero(request.productivityGain()));
        project.setDeadline(trimOrNull(request.deadline()));
        project.setStrategyId(trimOrNull(request.strategyId()));
    }

    private ProjectResponse toResponse(Project project) {
        double roi = calculateRoi(project.getInvestment(), project.getFinancialReturn());
        return ProjectResponse.from(project, strategyService.titleOf(project.getStrategyId()), roi);
    }

    private double orZero(Double value) {
        return value == null ? 0.0 : value;
    }

    private String trimOrNull(String value) {
        return (value == null || value.isBlank()) ? null : value.trim();
    }
}
