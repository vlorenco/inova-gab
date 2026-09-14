package br.com.fiap.inovagab.backend.dto.strategy;

import br.com.fiap.inovagab.backend.model.Strategy;

import java.time.Instant;

public record StrategyResponse(
        String id,
        String title,
        String description,
        String date,
        String category,
        String campaign,
        boolean active,
        Instant createdAt,
        Instant updatedAt,
        String createdBy
) {
    public static StrategyResponse from(Strategy strategy) {
        return new StrategyResponse(
                strategy.getId(),
                strategy.getTitle(),
                strategy.getDescription(),
                strategy.getDate(),
                strategy.getCategory(),
                strategy.getCampaign(),
                strategy.isActive(),
                strategy.getCreatedAt(),
                strategy.getUpdatedAt(),
                strategy.getCreatedBy()
        );
    }
}
