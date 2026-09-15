package br.com.fiap.inovagab.data.remote.dto

/** Formato de erro padronizado devolvido pelo backend. */
data class ApiErrorDto(
    val timestamp: String? = null,
    val status: Int = 0,
    val error: String? = null,
    val message: String? = null,
    val path: String? = null,
    val details: List<String>? = null
)
