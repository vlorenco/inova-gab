package br.com.fiap.inovagab.ui.gestor

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import br.com.fiap.inovagab.data.model.Idea
import br.com.fiap.inovagab.data.repository.IdeaRepository
import br.com.fiap.inovagab.ui.components.IdeaStatusBadge
import br.com.fiap.inovagab.ui.components.InfoRow
import br.com.fiap.inovagab.ui.components.InovaCard
import br.com.fiap.inovagab.ui.components.InovaDivider
import br.com.fiap.inovagab.ui.components.InovaEmptyState
import br.com.fiap.inovagab.ui.components.InovaErrorState
import br.com.fiap.inovagab.ui.components.InovaListScreen
import br.com.fiap.inovagab.ui.components.InovaLoading
import br.com.fiap.inovagab.ui.components.InovaSegmentedToggle
import br.com.fiap.inovagab.ui.components.InovaTag
import br.com.fiap.inovagab.ui.components.InovaTopBar
import br.com.fiap.inovagab.ui.components.MonoCounter
import br.com.fiap.inovagab.ui.components.MonoLabel
import br.com.fiap.inovagab.ui.components.StatusBadge
import br.com.fiap.inovagab.ui.components.scoreColor
import br.com.fiap.inovagab.ui.theme.InovaSpacing
import br.com.fiap.inovagab.ui.theme.InovaStatusError
import br.com.fiap.inovagab.ui.theme.InovaTextPrimary
import br.com.fiap.inovagab.ui.theme.InovaTextSecondary
import br.com.fiap.inovagab.ui.theme.InovaTextTertiary
import br.com.fiap.inovagab.ui.theme.InovaType

/** Filtro visual -> valor de status enviado ao backend. */
private val FILTERS = listOf(
    "Todas" to null,
    "Em análise" to "EM_ANALISE",
    "Priorizadas" to "PRIORIZADA",
    "Aprovadas" to "APROVADA",
    "Rejeitadas" to "REJEITADA"
)

@Composable
fun IdeiasGestorNewScreen(
    onBack: () -> Unit,
    onIdeiaClick: (String) -> Unit
) {
    val repository = remember { IdeaRepository() }
    var ideas by remember { mutableStateOf<List<Idea>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMsg by remember { mutableStateOf<String?>(null) }
    var selectedFilter by remember { mutableStateOf("Todas") }

    // A filtragem acontece no backend (GET /api/ideas?status=...).
    LaunchedEffect(selectedFilter) {
        isLoading = true
        val status = FILTERS.firstOrNull { it.first == selectedFilter }?.second
        repository.getAllIdeas(status)
            .onSuccess { ideas = it; errorMsg = null }
            .onFailure { errorMsg = it.message }
        isLoading = false
    }

    InovaListScreen(
        header = {
            InovaTopBar(
                title = "Avaliar Ideias",
                onBack = onBack,
                actions = {
                    if (!isLoading && errorMsg == null) {
                        MonoLabel(
                            text = "${ideas.size}",
                            color = InovaTextTertiary,
                            style = InovaType.mono
                        )
                    }
                }
            )
        }
    ) {
        InovaSegmentedToggle(
            options = FILTERS.map { it.first },
            selected = selectedFilter,
            onSelect = { selectedFilter = it },
            modifier = Modifier.padding(
                start = InovaSpacing.screenHorizontal,
                end = InovaSpacing.screenHorizontal,
                top = InovaSpacing.screenVertical
            )
        )

        when {
            isLoading -> InovaLoading()
            errorMsg != null -> InovaErrorState(errorMsg!!)
            ideas.isEmpty() -> InovaEmptyState("Nenhuma ideia encontrada.")
            else -> LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(InovaSpacing.card),
                contentPadding = PaddingValues(
                    start = InovaSpacing.screenHorizontal,
                    end = InovaSpacing.screenHorizontal,
                    top = InovaSpacing.block,
                    bottom = 28.dp
                )
            ) {
                itemsIndexed(ideas) { index, idea ->
                    GestorIdeaCard(
                        idea = idea,
                        index = index + 1,
                        onClick = { onIdeiaClick(idea.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun GestorIdeaCard(idea: Idea, index: Int, onClick: () -> Unit) {
    InovaCard(onClick = onClick, accent = idea.priority == "ALTA") {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            MonoCounter(index = index)
            IdeaStatusBadge(status = idea.status)
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(text = idea.title, style = InovaType.cardLabel, color = InovaTextPrimary)

        Spacer(modifier = Modifier.height(12.dp))
        InovaDivider()
        Spacer(modifier = Modifier.height(12.dp))

        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            InfoRow(label = "Operador", value = idea.operatorName)
            InfoRow(label = "Área", value = idea.area)
            InfoRow(label = "Estratégia", value = idea.strategyTitle)
        }

        val hasPriority = idea.priority != "NORMAL"
        val analysis = idea.aiAnalysis
        if (hasPriority || analysis != null) {
            Spacer(modifier = Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                if (hasPriority) {
                    InovaTag(
                        text = "Prioridade: ${idea.priority}",
                        color = if (idea.priority == "ALTA") InovaStatusError else InovaTextSecondary
                    )
                }
                if (analysis != null) {
                    StatusBadge(
                        text = "IA ${analysis.score}/100",
                        color = scoreColor(analysis.score)
                    )
                }
            }
        }
    }
}
