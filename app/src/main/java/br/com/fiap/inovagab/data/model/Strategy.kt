package br.com.fiap.inovagab.data.model

data class Strategy(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val date: String = "",
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)
