package br.com.fiap.inovagab.backend.dto.strategy;

import br.com.fiap.inovagab.backend.model.StrategyHistory;

import java.time.Instant;

public record StrategyHistoryResponse(
        String id,
        String strategyId,
        String title,
        String description,
        String date,
        String category,
        String campaign,
        boolean active,
        String action,
        Instant changedAt,
        String changedBy
) {
    public static StrategyHistoryResponse from(StrategyHistory history) {
        return new StrategyHistoryResponse(
                history.getId(),
                history.getStrategyId(),
                history.getTitle(),
                history.getDescription(),
                history.getDate(),
                history.getCategory(),
                history.getCampaign(),
                history.isActive(),
                history.getAction() == null ? null : history.getAction().name(),
                history.getChangedAt(),
                history.getChangedBy()
        );
    }
}
