package br.com.fiap.inovagab.backend.service;

import br.com.fiap.inovagab.backend.dto.strategy.StrategyHistoryResponse;
import br.com.fiap.inovagab.backend.dto.strategy.StrategyRequest;
import br.com.fiap.inovagab.backend.dto.strategy.StrategyResponse;
import br.com.fiap.inovagab.backend.exception.NotFoundException;
import br.com.fiap.inovagab.backend.model.Strategy;
import br.com.fiap.inovagab.backend.model.StrategyAction;
import br.com.fiap.inovagab.backend.model.StrategyHistory;
import br.com.fiap.inovagab.backend.repository.StrategyHistoryRepository;
import br.com.fiap.inovagab.backend.repository.StrategyRepository;
import br.com.fiap.inovagab.backend.security.AuthenticatedUser;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class StrategyService {

    private final StrategyRepository strategyRepository;
    private final StrategyHistoryRepository historyRepository;

    public StrategyService(StrategyRepository strategyRepository, StrategyHistoryRepository historyRepository) {
        this.strategyRepository = strategyRepository;
        this.historyRepository = historyRepository;
    }

    public List<StrategyResponse> list(Boolean activeOnly) {
        List<Strategy> strategies = Boolean.TRUE.equals(activeOnly)
                ? strategyRepository.findByActiveOrderByCreatedAtDesc(true)
                : strategyRepository.findAllByOrderByCreatedAtDesc();
        return strategies.stream().map(StrategyResponse::from).toList();
    }

    public StrategyResponse getById(String id) {
        return StrategyResponse.from(findOrThrow(id));
    }

    public Strategy findOrThrow(String id) {
        return strategyRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Orientacao estrategica nao encontrada."));
    }

    public StrategyResponse create(StrategyRequest request, AuthenticatedUser author) {
        Strategy strategy = new Strategy();
        apply(strategy, request);
        strategy.setCreatedAt(Instant.now());
        strategy.setUpdatedAt(strategy.getCreatedAt());
        strategy.setCreatedBy(author.getId());

        Strategy saved = strategyRepository.save(strategy);
        historyRepository.save(StrategyHistory.snapshot(saved, StrategyAction.CRIADA, author.getId()));
        return StrategyResponse.from(saved);
    }

    public StrategyResponse update(String id, StrategyRequest request, AuthenticatedUser author) {
        Strategy strategy = findOrThrow(id);
        boolean wasActive = strategy.isActive();

        apply(strategy, request);
        strategy.setUpdatedAt(Instant.now());
        Strategy saved = strategyRepository.save(strategy);

        StrategyAction action = (wasActive && !saved.isActive())
                ? StrategyAction.DESATIVADA
                : StrategyAction.ATUALIZADA;
        historyRepository.save(StrategyHistory.snapshot(saved, action, author.getId()));
        return StrategyResponse.from(saved);
    }

    public void delete(String id, AuthenticatedUser author) {
        Strategy strategy = findOrThrow(id);
        historyRepository.save(StrategyHistory.snapshot(strategy, StrategyAction.EXCLUIDA, author.getId()));
        strategyRepository.deleteById(id);
    }

    public List<StrategyHistoryResponse> history(String strategyId) {
        // Nao usa findOrThrow: o historico continua consultavel depois da exclusao.
        List<StrategyHistory> entries = historyRepository.findByStrategyIdOrderByChangedAtDesc(strategyId);
        if (entries.isEmpty() && !strategyRepository.existsById(strategyId)) {
            throw new NotFoundException("Orientacao estrategica nao encontrada.");
        }
        return entries.stream().map(StrategyHistoryResponse::from).toList();
    }

    /** Valida que o strategyId informado por ideia/projeto realmente existe. */
    public void validateExists(String strategyId) {
        if (strategyId != null && !strategyId.isBlank() && !strategyRepository.existsById(strategyId)) {
            throw new NotFoundException("Orientacao estrategica informada nao existe: " + strategyId);
        }
    }

    public String titleOf(String strategyId) {
        if (strategyId == null || strategyId.isBlank()) {
            return null;
        }
        return strategyRepository.findById(strategyId).map(Strategy::getTitle).orElse(null);
    }

    private void apply(Strategy strategy, StrategyRequest request) {
        strategy.setTitle(request.title().trim());
        strategy.setDescription(request.description().trim());
        strategy.setDate(trimOrNull(request.date()));
        strategy.setCategory(trimOrNull(request.category()));
        strategy.setCampaign(trimOrNull(request.campaign()));
        strategy.setActive(request.active() == null || request.active());
    }

    private String trimOrNull(String value) {
        return (value == null || value.isBlank()) ? null : value.trim();
    }
}
