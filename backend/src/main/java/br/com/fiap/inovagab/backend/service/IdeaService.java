package br.com.fiap.inovagab.backend.service;

import br.com.fiap.inovagab.backend.ai.AiAnalysisService;
import br.com.fiap.inovagab.backend.dto.idea.AiAnalysisResponse;
import br.com.fiap.inovagab.backend.dto.idea.IdeaRequest;
import br.com.fiap.inovagab.backend.dto.idea.IdeaResponse;
import br.com.fiap.inovagab.backend.exception.BadRequestException;
import br.com.fiap.inovagab.backend.exception.ForbiddenException;
import br.com.fiap.inovagab.backend.exception.NotFoundException;
import br.com.fiap.inovagab.backend.model.AiAnalysis;
import br.com.fiap.inovagab.backend.model.Idea;
import br.com.fiap.inovagab.backend.model.IdeaPriority;
import br.com.fiap.inovagab.backend.model.IdeaStatus;
import br.com.fiap.inovagab.backend.model.Role;
import br.com.fiap.inovagab.backend.model.Strategy;
import br.com.fiap.inovagab.backend.repository.IdeaRepository;
import br.com.fiap.inovagab.backend.security.AuthenticatedUser;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class IdeaService {

    private final IdeaRepository ideaRepository;
    private final StrategyService strategyService;
    private final RankingService rankingService;
    private final AiAnalysisService aiAnalysisService;

    public IdeaService(IdeaRepository ideaRepository,
                       StrategyService strategyService,
                       RankingService rankingService,
                       AiAnalysisService aiAnalysisService) {
        this.ideaRepository = ideaRepository;
        this.strategyService = strategyService;
        this.rankingService = rankingService;
        this.aiAnalysisService = aiAnalysisService;
    }

    // ── Operador ────────────────────────────────────────────────────────────

    public IdeaResponse create(IdeaRequest request, AuthenticatedUser author) {
        // Toda ideia nasce vinculada a uma orientacao estrategica vigente.
        strategyService.validateLink(request.strategyId(), null);

        Idea idea = new Idea();
        applyEditableFields(idea, request);
        idea.setStatus(IdeaStatus.EM_ANALISE);
        idea.setPriority(IdeaPriority.NORMAL);
        // O dono vem do token: nunca do corpo da requisicao.
        idea.setOperatorId(author.getId());
        idea.setOperatorName(author.getName());
        idea.setCreatedAt(Instant.now());
        idea.setUpdatedAt(idea.getCreatedAt());

        Idea saved = ideaRepository.save(idea);

        if (!saved.isCreationPointsAwarded()) {
            rankingService.awardPoints(saved.getOperatorId(), RankingService.POINTS_IDEA_CREATED);
            saved.setCreationPointsAwarded(true);
            saved = ideaRepository.save(saved);
        }

        return toResponse(saved);
    }

    public List<IdeaResponse> listMyIdeas(AuthenticatedUser operator) {
        return ideaRepository.findByOperatorIdOrderByCreatedAtDesc(operator.getId()).stream()
                .map(this::toResponse)
                .toList();
    }

    public IdeaResponse update(String ideaId, IdeaRequest request, AuthenticatedUser operator) {
        Idea idea = findOrThrow(ideaId);
        requireOwnership(idea, operator);

        if (idea.getStatus() == IdeaStatus.APROVADA || idea.getStatus() == IdeaStatus.REJEITADA) {
            throw new BadRequestException(
                    "Esta ideia ja foi avaliada pelo gestor e nao pode mais ser editada.");
        }

        strategyService.validateLink(request.strategyId(), idea.getStrategyId());
        applyEditableFields(idea, request);
        idea.setUpdatedAt(Instant.now());
        return toResponse(ideaRepository.save(idea));
    }

    public void delete(String ideaId, AuthenticatedUser operator) {
        Idea idea = findOrThrow(ideaId);
        requireOwnership(idea, operator);

        if (idea.isConvertedToProject()) {
            throw new BadRequestException("Esta ideia ja virou projeto e nao pode ser excluida.");
        }
        if (idea.getStatus() == IdeaStatus.APROVADA) {
            throw new BadRequestException("Ideias aprovadas nao podem ser excluidas pelo operador.");
        }

        ideaRepository.deleteById(ideaId);
    }

    // ── Gestor ──────────────────────────────────────────────────────────────

    public List<IdeaResponse> listAll(IdeaStatus status) {
        List<Idea> ideas = status == null
                ? ideaRepository.findAllByOrderByCreatedAtDesc()
                : ideaRepository.findByStatusOrderByCreatedAtDesc(status);
        return ideas.stream().map(this::toResponse).toList();
    }

    public IdeaResponse updatePriority(String ideaId, IdeaPriority priority) {
        Idea idea = findOrThrow(ideaId);
        idea.setPriority(priority);
        // Priorizar uma ideia ainda em analise move o status junto, como na Sprint 1.
        if (priority == IdeaPriority.ALTA && idea.getStatus() == IdeaStatus.EM_ANALISE) {
            idea.setStatus(IdeaStatus.PRIORIZADA);
        }
        idea.setUpdatedAt(Instant.now());
        return toResponse(ideaRepository.save(idea));
    }

    public IdeaResponse updateStatus(String ideaId, IdeaStatus status) {
        Idea idea = findOrThrow(ideaId);

        if (idea.isConvertedToProject() && status != IdeaStatus.APROVADA) {
            throw new BadRequestException(
                    "Esta ideia ja foi convertida em projeto: o status nao pode mais ser alterado.");
        }

        idea.setStatus(status);
        idea.setUpdatedAt(Instant.now());

        if (status == IdeaStatus.APROVADA) {
            if (idea.getApprovedAt() == null) {
                idea.setApprovedAt(Instant.now());
            }
            // Idempotente: reaprovar a mesma ideia nao credita pontos de novo.
            if (!idea.isApprovalPointsAwarded()) {
                rankingService.awardPoints(idea.getOperatorId(), RankingService.POINTS_IDEA_APPROVED);
                idea.setApprovalPointsAwarded(true);
            }
        }

        return toResponse(ideaRepository.save(idea));
    }

    /** Funcionalidade Plus: pontuacao automatica da ideia pelo Gemini. */
    public AiAnalysisResponse runAiAnalysis(String ideaId, AuthenticatedUser manager) {
        Idea idea = findOrThrow(ideaId);

        Strategy strategy = null;
        if (idea.getStrategyId() != null && !idea.getStrategyId().isBlank()) {
            strategy = strategyService.findOrThrow(idea.getStrategyId());
        }

        AiAnalysis analysis = aiAnalysisService.analyze(idea, strategy, manager.getId());
        idea.setAiAnalysis(analysis);
        idea.setUpdatedAt(Instant.now());
        ideaRepository.save(idea);

        return AiAnalysisResponse.from(analysis);
    }

    // ── Consulta compartilhada ──────────────────────────────────────────────

    /**
     * Detalhe da ideia. O gestor ve qualquer ideia; o operador so ve a propria,
     * mesmo que troque o id na URL.
     */
    public IdeaResponse getById(String ideaId, AuthenticatedUser requester) {
        Idea idea = findOrThrow(ideaId);
        if (requester.getRole() == Role.OPERADOR) {
            requireOwnership(idea, requester);
        }
        return toResponse(idea);
    }

    public Idea findOrThrow(String ideaId) {
        return ideaRepository.findById(ideaId)
                .orElseThrow(() -> new NotFoundException("Ideia nao encontrada."));
    }

    /** Chamado pelo ProjectService quando um projeto nasce de uma ideia aprovada. */
    public void markConvertedToProject(Idea idea) {
        idea.setConvertedToProject(true);
        idea.setUpdatedAt(Instant.now());
        if (!idea.isConversionPointsAwarded()) {
            rankingService.awardPoints(idea.getOperatorId(), RankingService.POINTS_IDEA_CONVERTED);
            idea.setConversionPointsAwarded(true);
        }
        ideaRepository.save(idea);
    }

    // ── Apoio ───────────────────────────────────────────────────────────────

    private void requireOwnership(Idea idea, AuthenticatedUser operator) {
        if (!operator.getId().equals(idea.getOperatorId())) {
            throw new ForbiddenException("Voce so pode acessar as suas proprias ideias.");
        }
    }

    private void applyEditableFields(Idea idea, IdeaRequest request) {
        idea.setTitle(request.title().trim());
        idea.setProblem(request.problem().trim());
        idea.setSolution(request.solution().trim());
        idea.setArea(trimOrNull(request.area()));
        idea.setBenefit(trimOrNull(request.benefit()));
        idea.setStrategyId(trimOrNull(request.strategyId()));
    }

    private IdeaResponse toResponse(Idea idea) {
        return IdeaResponse.from(idea, strategyService.titleOf(idea.getStrategyId()));
    }

    private String trimOrNull(String value) {
        return (value == null || value.isBlank()) ? null : value.trim();
    }
}
