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
    val createdAt: Long = System.currentTimeMillis(),
    val approvedAt: Long? = null,
    val convertedToProject: Boolean = false
)
