package br.com.fiap.inovagab.data.model

data class User(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val role: String = "",
    val points: Int = 0
)
