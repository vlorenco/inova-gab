package br.com.fiap.inovagab.data.remote.dto

data class ProjectDto(
    val id: String = "",
    val ideaId: String? = null,
    val strategyId: String? = null,
    val strategyTitle: String? = null,
    val name: String = "",
    val description: String? = null,
    val responsible: String? = null,
    val status: String = "PLANEJADO",
    val currentStage: String? = null,
    val investment: Double = 0.0,
    val financialReturn: Double = 0.0,
    val costReduction: Double = 0.0,
    val productivityGain: Double = 0.0,
    val roi: Double = 0.0,
    val deadline: String? = null
)

data class ProjectRequestDto(
    val name: String,
    val description: String? = null,
    val responsible: String? = null,
    val status: String = "PLANEJADO",
    val currentStage: String? = null,
    val investment: Double = 0.0,
    val financialReturn: Double = 0.0,
    val costReduction: Double = 0.0,
    val productivityGain: Double = 0.0,
    val deadline: String? = null,
    val ideaId: String? = null,
    val strategyId: String? = null
)
