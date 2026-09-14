package br.com.fiap.inovagab.ui.gestor.model

import com.google.firebase.Timestamp

data class Projeto(
    val id: String = "",
    val ideiaId: String = "",
    val titulo: String = "",
    val descricao: String = "",
    val responsavel: String = "",
    val status: String = "Criado",
    val roiEstimado: Double = 0.0,
    val reducaoCustoEstimada: Double = 0.0,
    val dataCriacao: Timestamp? = null,
    val dataAtualizacao: Timestamp? = null
)
