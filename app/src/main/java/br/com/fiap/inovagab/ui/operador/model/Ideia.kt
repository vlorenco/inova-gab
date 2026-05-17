package br.com.fiap.inovagab.ui.operador.model

import com.google.firebase.Timestamp


data class Ideia(
    val id: String = "",
    val titulo: String = "",
    val problema: String = "",
    val solucao: String = "",
    val area: String = "",
    val beneficio: String = "",
    val categoria: String = "",
    val status: String = "Em análise",
    val dataCriacao: Timestamp? = null
)