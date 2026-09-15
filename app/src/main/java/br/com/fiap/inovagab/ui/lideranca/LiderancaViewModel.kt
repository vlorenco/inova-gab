package br.com.fiap.inovagab.ui.lideranca

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.fiap.inovagab.data.model.Strategy
import br.com.fiap.inovagab.data.model.StrategyHistoryEntry
import br.com.fiap.inovagab.data.repository.StrategyRepository
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * CRUD completo das orientações estratégicas, consumindo /api/strategies.
 * O histórico vem de /api/strategies/{id}/history.
 */
class LiderancaViewModel : ViewModel() {

    private val strategyRepository = StrategyRepository()

    val strategies = mutableStateListOf<Strategy>()

    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)
    var selectedTab by mutableStateOf("Todas")

    // Formulário (criação e edição usam o mesmo painel)
    var editingId by mutableStateOf<String?>(null)
    var formTitle by mutableStateOf("")
    var formDescription by mutableStateOf("")
    var formCategory by mutableStateOf("")
    var formCampaign by mutableStateOf("")
    var formActive by mutableStateOf(true)
    var showForm by mutableStateOf(false)

    // Histórico
    var historyOf by mutableStateOf<Strategy?>(null)
    val history = mutableStateListOf<StrategyHistoryEntry>()
    var historyLoading by mutableStateOf(false)

    fun loadStrategies() {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            strategyRepository.getStrategies()
                .onSuccess { list ->
                    strategies.clear()
                    strategies.addAll(list)
                }
                .onFailure { errorMessage = it.message }
            isLoading = false
        }
    }

    fun startCreate() {
        editingId = null
        formTitle = ""
        formDescription = ""
        formCategory = ""
        formCampaign = ""
        formActive = true
        showForm = true
    }

    fun startEdit(strategy: Strategy) {
        editingId = strategy.id
        formTitle = strategy.title
        formDescription = strategy.description
        formCategory = strategy.category
        formCampaign = strategy.campaign
        formActive = strategy.active
        showForm = true
    }

    fun cancelForm() {
        showForm = false
        editingId = null
    }

    fun saveStrategy() {
        if (formTitle.isBlank() || formDescription.isBlank()) {
            errorMessage = "Informe título e descrição da orientação."
            return
        }
        viewModelScope.launch {
            isLoading = true
            errorMessage = null

            val strategy = Strategy(
                id = editingId.orEmpty(),
                title = formTitle.trim(),
                description = formDescription.trim(),
                date = SimpleDateFormat("dd/MM/yyyy", Locale.forLanguageTag("pt-BR")).format(Date()),
                category = formCategory.trim(),
                campaign = formCampaign.trim(),
                active = formActive
            )

            val result = if (editingId == null) {
                strategyRepository.createStrategy(strategy)
            } else {
                strategyRepository.updateStrategy(strategy)
            }

            result
                .onSuccess {
                    cancelForm()
                    loadStrategies()
                }
                .onFailure { errorMessage = it.message }
            isLoading = false
        }
    }

    fun deleteStrategy(strategyId: String) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            strategyRepository.deleteStrategy(strategyId)
                .onSuccess { loadStrategies() }
                .onFailure { errorMessage = it.message }
            isLoading = false
        }
    }

    fun openHistory(strategy: Strategy) {
        historyOf = strategy
        history.clear()
        historyLoading = true
        viewModelScope.launch {
            strategyRepository.getHistory(strategy.id)
                .onSuccess { history.addAll(it) }
                .onFailure { errorMessage = it.message }
            historyLoading = false
        }
    }

    fun closeHistory() {
        historyOf = null
        history.clear()
    }

    fun getFilteredStrategies(): List<Strategy> = when (selectedTab) {
        "Ativas" -> strategies.filter { it.active }
        "Inativas" -> strategies.filter { !it.active }
        else -> strategies.toList()
    }
}
