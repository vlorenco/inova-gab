package br.com.fiap.inovagab.ui.lideranca

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.fiap.inovagab.data.model.Strategy
import br.com.fiap.inovagab.data.repository.StrategyRepository
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class LiderancaViewModel : ViewModel() {

    private val strategyRepository = StrategyRepository()

    val strategies = mutableStateListOf<Strategy>()

    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)
    var selectedTab by mutableStateOf("Todas")

    // Formulário
    var newStrategyTitle by mutableStateOf("")
    var newStrategyDescription by mutableStateOf("")
    var showForm by mutableStateOf(false)

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

    fun createStrategy() {
        if (newStrategyTitle.isBlank()) return
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale("pt", "BR"))
            val strategy = Strategy(
                title = newStrategyTitle.trim(),
                description = newStrategyDescription.trim(),
                date = dateFormat.format(Date()),
                isActive = true
            )
            strategyRepository.createStrategy(strategy)
                .onSuccess {
                    newStrategyTitle = ""
                    newStrategyDescription = ""
                    showForm = false
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

    fun getFilteredStrategies(): List<Strategy> {
        return when (selectedTab) {
            "Ativas" -> strategies.filter { it.isActive }
            "Inativas" -> strategies.filter { !it.isActive }
            else -> strategies.toList()
        }
    }
}
