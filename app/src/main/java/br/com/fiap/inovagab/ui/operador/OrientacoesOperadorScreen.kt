package br.com.fiap.inovagab.ui.operador

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Flag
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
import br.com.fiap.inovagab.data.model.Strategy
import br.com.fiap.inovagab.data.repository.StrategyRepository
import br.com.fiap.inovagab.ui.components.IconTile
import br.com.fiap.inovagab.ui.components.InovaCard
import br.com.fiap.inovagab.ui.components.InovaEmptyState
import br.com.fiap.inovagab.ui.components.InovaErrorState
import br.com.fiap.inovagab.ui.components.InovaListScreen
import br.com.fiap.inovagab.ui.components.InovaLoading
import br.com.fiap.inovagab.ui.components.InovaTag
import br.com.fiap.inovagab.ui.components.InovaTopBar
import br.com.fiap.inovagab.ui.components.MonoCounter
import br.com.fiap.inovagab.ui.components.MonoLabel
import br.com.fiap.inovagab.ui.theme.InovaSpacing
import br.com.fiap.inovagab.ui.theme.InovaTextPrimary
import br.com.fiap.inovagab.ui.theme.InovaTextSecondary
import br.com.fiap.inovagab.ui.theme.InovaTextTertiary
import br.com.fiap.inovagab.ui.theme.InovaType

@Composable
fun OrientacoesOperadorScreen(onBack: () -> Unit) {
    val repository = remember { StrategyRepository() }
    var strategies by remember { mutableStateOf<List<Strategy>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    // O backend já devolve apenas as orientações vigentes com activeOnly=true.
    LaunchedEffect(Unit) {
        repository.getStrategies(activeOnly = true)
            .onSuccess { strategies = it; isLoading = false }
            .onFailure { errorMsg = it.message; isLoading = false }
    }

    InovaListScreen(
        header = {
            InovaTopBar(
                title = "Orientações Estratégicas",
                onBack = onBack,
                actions = {
                    if (!isLoading && errorMsg == null) {
                        MonoLabel(
                            text = "${strategies.size}",
                            color = InovaTextTertiary,
                            style = InovaType.mono
                        )
                    }
                }
            )
        }
    ) {
        when {
            isLoading -> InovaLoading()
            errorMsg != null -> InovaErrorState(errorMsg!!)
            strategies.isEmpty() -> InovaEmptyState("Nenhuma orientação estratégica disponível.")
            else -> LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(InovaSpacing.card),
                contentPadding = PaddingValues(
                    start = InovaSpacing.screenHorizontal,
                    end = InovaSpacing.screenHorizontal,
                    top = InovaSpacing.screenVertical,
                    bottom = 28.dp
                )
            ) {
                itemsIndexed(strategies) { index, strategy ->
                    StrategyCard(strategy = strategy, index = index + 1)
                }
            }
        }
    }
}

@Composable
private fun StrategyCard(strategy: Strategy, index: Int) {
    InovaCard(accent = true) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconTile(icon = Icons.Outlined.Flag, contentDescription = null)
            MonoCounter(index = index)
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(text = strategy.title, style = InovaType.cardLabel, color = InovaTextPrimary)

        if (strategy.description.isNotBlank()) {
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = strategy.description,
                style = InovaType.bodySmall,
                color = InovaTextSecondary
            )
        }

        if (strategy.category.isNotBlank() || strategy.campaign.isNotBlank()) {
            Spacer(modifier = Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                if (strategy.category.isNotBlank()) {
                    InovaTag(text = strategy.category)
                }
                if (strategy.campaign.isNotBlank()) {
                    InovaTag(text = strategy.campaign, color = InovaTextTertiary)
                }
            }
        }
    }
}
