package br.com.fiap.inovagab.ui.gestor

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.fiap.inovagab.ui.gestor.model.Projeto
import br.com.fiap.inovagab.ui.operador.model.Ideia
import br.com.fiap.inovagab.ui.repositoryfirestore.FirestoreRepository
import kotlinx.coroutines.launch

class GestorViewModel : ViewModel() {

    private val repository = FirestoreRepository()

    val ideiasPendentes = mutableStateListOf<Ideia>()
    val projetos = mutableStateListOf<Projeto>()

    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)

    fun carregarIdeiasPendentes() {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                ideiasPendentes.clear()
                ideiasPendentes.addAll(repository.buscarIdeiasPendentes())
            } catch (e: Exception) {
                errorMessage = e.message
            } finally {
                isLoading = false
            }
        }
    }

    fun carregarProjetos() {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                projetos.clear()
                projetos.addAll(repository.buscarProjetos())
            } catch (e: Exception) {
                errorMessage = e.message
            } finally {
                isLoading = false
            }
        }
    }

    fun aprovarIdeia(ideiaId: String) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                repository.aprovarIdeia(ideiaId)
                carregarIdeiasPendentes()
            } catch (e: Exception) {
                errorMessage = e.message
            } finally {
                isLoading = false
            }
        }
    }

    // Aprova sem recarregar a lista — usado antes de abrir CadastroProjetoScreen
    fun aprovarIdeiaParaProjeto(ideiaId: String) {
        viewModelScope.launch {
            try {
                repository.aprovarIdeia(ideiaId)
            } catch (e: Exception) {
                errorMessage = e.message
            }
        }
    }

    fun reprovarIdeia(ideiaId: String) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                repository.reprovarIdeia(ideiaId)
                carregarIdeiasPendentes()
            } catch (e: Exception) {
                errorMessage = e.message
            } finally {
                isLoading = false
            }
        }
    }

    fun salvarProjeto(projeto: Projeto) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                repository.salvarProjeto(projeto)
                carregarProjetos()
            } catch (e: Exception) {
                errorMessage = e.message
            } finally {
                isLoading = false
            }
        }
    }

    fun atualizarProjeto(projeto: Projeto) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                repository.atualizarProjeto(projeto)
                carregarProjetos()
            } catch (e: Exception) {
                errorMessage = e.message
            } finally {
                isLoading = false
            }
        }
    }
}
