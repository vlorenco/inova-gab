package br.com.fiap.inovagab.backend.dto.dashboard;

/** Indicadores de uma orientacao estrategica especifica. */
public record StrategyDashboardResponse(
        String strategyId,
        String strategyTitle,
        boolean active,
        long totalIdeas,
        long approvedIdeas,
        long totalProjects,
        long completedProjects,
        double totalInvestment,
        double totalFinancialReturn,
        double profit,
        double roi,
        double totalCostReduction,
        double averageProductivityGain
) {
}
