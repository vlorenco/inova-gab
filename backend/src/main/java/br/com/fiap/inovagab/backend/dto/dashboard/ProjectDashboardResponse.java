package br.com.fiap.inovagab.backend.dto.dashboard;

/** Indicadores de um projeto especifico. */
public record ProjectDashboardResponse(
        String projectId,
        String projectName,
        String status,
        String currentStage,
        String strategyId,
        String strategyTitle,
        String ideaId,
        String ideaTitle,
        double investment,
        double financialReturn,
        double profit,
        double roi,
        double costReduction,
        double productivityGain,
        String deadline
) {
}
