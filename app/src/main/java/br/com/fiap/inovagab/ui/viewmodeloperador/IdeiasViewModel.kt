package br.com.fiap.inovagab.ui.viewmodeloperador

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.fiap.inovagab.ui.operador.model.Ideia
import br.com.fiap.inovagab.ui.repositoryfirestore.FirestoreRepository
import kotlinx.coroutines.launch

open class IdeiasViewModel : ViewModel() {

    private val repository = FirestoreRepository()

    val ideias = mutableStateListOf<Ideia>()

    fun carregarIdeias() {
        viewModelScope.launch {
            ideias.clear()
            ideias.addAll(repository.buscarIdeias())
        }
    }

    fun salvarIdeia(ideia: Ideia) {
        viewModelScope.launch {
            repository.salvarIdeia(ideia)
            carregarIdeias()
        }
    }
}