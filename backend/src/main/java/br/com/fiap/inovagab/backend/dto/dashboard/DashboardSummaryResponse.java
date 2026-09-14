package br.com.fiap.inovagab.backend.dto.dashboard;

/**
 * Indicadores consolidados de todos os projetos.
 * Substitui o calculo que a Sprint 1 fazia dentro do app Android.
 */
public record DashboardSummaryResponse(
        long totalProjects,
        long activeProjects,
        long completedProjects,
        long plannedProjects,
        long cancelledProjects,
        double totalInvestment,
        double totalFinancialReturn,
        double profit,
        double roi,
        double totalCostReduction,
        double averageProductivityGain,
        long totalIdeas,
        long approvedIdeas,
        long ideasUnderAnalysis,
        long totalStrategies,
        long activeStrategies
) {
}
