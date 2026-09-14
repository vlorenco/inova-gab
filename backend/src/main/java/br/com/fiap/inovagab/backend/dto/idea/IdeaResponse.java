package br.com.fiap.inovagab.backend.dto.idea;

import br.com.fiap.inovagab.backend.model.Idea;

import java.time.Instant;

public record IdeaResponse(
        String id,
        String title,
        String problem,
        String solution,
        String area,
        String benefit,
        String status,
        String priority,
        String operatorId,
        String operatorName,
        String strategyId,
        String strategyTitle,
        Instant createdAt,
        Instant updatedAt,
        Instant approvedAt,
        boolean convertedToProject,
        AiAnalysisResponse aiAnalysis
) {
    public static IdeaResponse from(Idea idea, String strategyTitle) {
        return new IdeaResponse(
                idea.getId(),
                idea.getTitle(),
                idea.getProblem(),
                idea.getSolution(),
                idea.getArea(),
                idea.getBenefit(),
                idea.getStatus().name(),
                idea.getPriority().name(),
                idea.getOperatorId(),
                idea.getOperatorName(),
                idea.getStrategyId(),
                strategyTitle,
                idea.getCreatedAt(),
                idea.getUpdatedAt(),
                idea.getApprovedAt(),
                idea.isConvertedToProject(),
                AiAnalysisResponse.from(idea.getAiAnalysis())
        );
    }
}
