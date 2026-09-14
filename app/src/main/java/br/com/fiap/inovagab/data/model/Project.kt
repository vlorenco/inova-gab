package br.com.fiap.inovagab.data.model

data class Project(
    val id: String = "",
    val ideaId: String = "",
    val strategyId: String = "",
    val strategyTitle: String = "",
    val name: String = "",
    val description: String = "",
    val responsible: String = "",
    val status: String = "PLANEJADO",
    val currentStage: String = "",
    val investment: Double = 0.0,
    val financialReturn: Double = 0.0,
    val costReduction: Double = 0.0,
    val productivityGain: Double = 0.0,
    val roi: Double = 0.0,
    val deadline: String = ""
)
