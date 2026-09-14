package br.com.fiap.inovagab.data.remote.dto

data class DashboardSummaryDto(
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

data class StrategyDashboardDto(
    val strategyId: String = "",
    val strategyTitle: String = "",
    val active: Boolean = true,
    val totalIdeas: Int = 0,
    val approvedIdeas: Int = 0,
    val totalProjects: Int = 0,
    val completedProjects: Int = 0,
    val totalInvestment: Double = 0.0,
    val totalFinancialReturn: Double = 0.0,
    val profit: Double = 0.0,
    val roi: Double = 0.0,
    val totalCostReduction: Double = 0.0,
    val averageProductivityGain: Double = 0.0
)

data class ProjectDashboardDto(
    val projectId: String = "",
    val projectName: String = "",
    val status: String = "",
    val currentStage: String? = null,
    val strategyId: String? = null,
    val strategyTitle: String? = null,
    val ideaId: String? = null,
    val ideaTitle: String? = null,
    val investment: Double = 0.0,
    val financialReturn: Double = 0.0,
    val profit: Double = 0.0,
    val roi: Double = 0.0,
    val costReduction: Double = 0.0,
    val productivityGain: Double = 0.0,
    val deadline: String? = null
)
