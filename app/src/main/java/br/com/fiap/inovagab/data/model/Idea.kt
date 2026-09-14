package br.com.fiap.inovagab.data.model

data class Idea(
    val id: String = "",
    val title: String = "",
    val problem: String = "",
    val solution: String = "",
    val area: String = "",
    val benefit: String = "",
    val status: String = "EM_ANALISE",
    val priority: String = "NORMAL",
    val operatorId: String = "",
    val operatorName: String = "",
    val strategyId: String = "",
    val strategyTitle: String = "",
    val convertedToProject: Boolean = false,
    val aiAnalysis: AiAnalysis? = null
)

/** Pontuacao automatica gerada pelo Gemini atraves do backend. */
data class AiAnalysis(
    val score: Int = 0,
    val impactScore: Int = 0,
    val feasibilityScore: Int = 0,
    val innovationScore: Int = 0,
    val strategicAlignmentScore: Int = 0,
    val recommendation: String = "",
    val summary: String = ""
)
