package br.com.fiap.inovagab.data.model

/** Indicadores calculados pelo backend (GET /api/dashboard/summary). */
data class DashboardSummary(
    val totalProjects: Int = 0,
    val activeProjects: Int = 0,
    val completedProjects: Int = 0,
    val plannedProjects: Int = 0,
    val cancelledProjects: Int = 0,
    val totalInvestment: Double = 0.0,
    val totalFinancialReturn: Double = 0.0,
    val profit: Double = 0.0,
    val roi: Double = 0.0,
    val totalCostReduction: Double = 0.0,
    val averageProductivityGain: Double = 0.0,
    val totalIdeas: Int = 0,
    val approvedIdeas: Int = 0,
    val ideasUnderAnalysis: Int = 0,
    val totalStrategies: Int = 0,
    val activeStrategies: Int = 0
)
