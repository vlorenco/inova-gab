package br.com.fiap.inovagab.data.remote.dto

data class IdeaDto(
    val id: String = "",
    val title: String = "",
    val problem: String = "",
    val solution: String = "",
    val area: String? = null,
    val benefit: String? = null,
    val status: String = "EM_ANALISE",
    val priority: String = "NORMAL",
    val operatorId: String = "",
    val operatorName: String = "",
    val strategyId: String? = null,
    val strategyTitle: String? = null,
    val convertedToProject: Boolean = false,
    val aiAnalysis: AiAnalysisDto? = null
)

data class IdeaRequestDto(
    val title: String,
    val problem: String,
    val solution: String,
    val area: String? = null,
    val benefit: String? = null,
    val strategyId: String? = null
)

data class IdeaPriorityRequestDto(val priority: String)

data class IdeaStatusRequestDto(val status: String)

data class AiAnalysisDto(
    val score: Int = 0,
    val impactScore: Int = 0,
    val feasibilityScore: Int = 0,
    val innovationScore: Int = 0,
    val strategicAlignmentScore: Int = 0,
    val recommendation: String = "",
    val summary: String = "",
    val analyzedAt: String? = null,
    val model: String? = null
)
