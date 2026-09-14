package br.com.fiap.inovagab.backend.dto.project;

import br.com.fiap.inovagab.backend.model.Project;

import java.time.Instant;

public record ProjectResponse(
        String id,
        String ideaId,
        String strategyId,
        String strategyTitle,
        String name,
        String description,
        String responsible,
        String status,
        String currentStage,
        double investment,
        double financialReturn,
        double costReduction,
        double productivityGain,
        double roi,
        String deadline,
        Instant createdAt,
        Instant updatedAt
) {
    public static ProjectResponse from(Project project, String strategyTitle, double roi) {
        return new ProjectResponse(
                project.getId(),
                project.getIdeaId(),
                project.getStrategyId(),
                strategyTitle,
                project.getName(),
                project.getDescription(),
                project.getResponsible(),
                project.getStatus().name(),
                project.getCurrentStage(),
                project.getInvestment(),
                project.getFinancialReturn(),
                project.getCostReduction(),
                project.getProductivityGain(),
                roi,
                project.getDeadline(),
                project.getCreatedAt(),
                project.getUpdatedAt()
        );
    }
}
