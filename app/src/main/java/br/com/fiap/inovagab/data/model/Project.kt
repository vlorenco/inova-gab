package br.com.fiap.inovagab.data.model

data class Project(
    val id: String = "",
    val ideaId: String = "",
    val name: String = "",
    val description: String = "",
    val responsible: String = "",
    val status: String = "",
    val currentStage: String = "",
    val investment: Double = 0.0,
    val financialReturn: Double = 0.0,
    val costReduction: Double = 0.0,
    val productivityGain: Double = 0.0,
    val deadline: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
