package br.com.fiap.inovagab.ui.lideranca

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.History
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import br.com.fiap.inovagab.data.model.Strategy
import br.com.fiap.inovagab.ui.components.InovaCard
import br.com.fiap.inovagab.ui.components.InovaDivider
import br.com.fiap.inovagab.ui.components.InovaEmptyState
import br.com.fiap.inovagab.ui.components.InovaErrorState
import br.com.fiap.inovagab.ui.components.InovaFilledButton
import br.com.fiap.inovagab.ui.components.InovaFormField
import br.com.fiap.inovagab.ui.components.InovaIconButton
import br.com.fiap.inovagab.ui.components.InovaListScreen
import br.com.fiap.inovagab.ui.components.InovaLoading
import br.com.fiap.inovagab.ui.components.InovaOutlineButton
import br.com.fiap.inovagab.ui.components.InovaSegmentedToggle
import br.com.fiap.inovagab.ui.components.InovaSwitch
import br.com.fiap.inovagab.ui.components.InovaTag
import br.com.fiap.inovagab.ui.components.InovaTopBar
import br.com.fiap.inovagab.ui.components.MonoCounter
import br.com.fiap.inovagab.ui.components.MonoLabel
import br.com.fiap.inovagab.ui.components.StatusBadge
import br.com.fiap.inovagab.ui.theme.InovaBlueLight
import br.com.fiap.inovagab.ui.theme.InovaDurationDefault
import br.com.fiap.inovagab.ui.theme.InovaSpacing
import br.com.fiap.inovagab.ui.theme.InovaStatusDone
import br.com.fiap.inovagab.ui.theme.InovaStatusError
import br.com.fiap.inovagab.ui.theme.InovaSurface
import br.com.fiap.inovagab.ui.theme.InovaTextDisabled
import br.com.fiap.inovagab.ui.theme.InovaTextPrimary
import br.com.fiap.inovagab.ui.theme.InovaTextSecondary
import br.com.fiap.inovagab.ui.theme.InovaTextTertiary
import br.com.fiap.inovagab.ui.theme.InovaType
import br.com.fiap.inovagab.ui.theme.inovaTween

@Composable
fun StrategiesScreen(
    onBack: () -> Unit,
    viewModel: LiderancaViewModel = viewModel()
) {
    var showDeleteDialog by remember { mutableStateOf<Strategy?>(null) }

    LaunchedEffect(Unit) { viewModel.loadStrategies() }

    val filtered = viewModel.getFilteredStrategies()

    InovaListScreen(
        header = {
            InovaTopBar(
                title = "Orientações Estratégicas",
                onBack = onBack,
                actions = {
                    InovaIconButton(
                        icon = Icons.Outlined.Add,
                        contentDescription = "Adicionar",
                        onClick = { viewModel.startCreate() }
                    )
                }
            )
        }
    ) {
        InovaSegmentedToggle(
            options = listOf("Todas", "Ativas", "Inativas"),
            selected = viewModel.selectedTab,
            onSelect = { viewModel.selectedTab = it },
            modifier = Modifier.padding(
                start = InovaSpacing.screenHorizontal,
                end = InovaSpacing.screenHorizontal,
                top = InovaSpacing.screenVertical
            )
        )

        AnimatedVisibility(
            visible = viewModel.showForm,
            enter = fadeIn(inovaTween(InovaDurationDefault)) +
                expandVertically(inovaTween(InovaDurationDefault)),
            exit = fadeOut(inovaTween(InovaDurationDefault)) +
                shrinkVertically(inovaTween(InovaDurationDefault))
        ) {
            StrategyForm(
                viewModel = viewModel,
                modifier = Modifier.padding(
                    start = InovaSpacing.screenHorizontal,
                    end = InovaSpacing.screenHorizontal,
                    top = InovaSpacing.block
                )
            )
        }

        when {
            viewModel.isLoading && viewModel.strategies.isEmpty() -> InovaLoading()

            viewModel.errorMessage != null && viewModel.strategies.isEmpty() ->
                InovaErrorState(viewModel.errorMessage!!)

            filtered.isEmpty() ->
                InovaEmptyState("Nenhuma orientação estratégica encontrada.")

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
                viewModel.errorMessage?.let { error ->
                    item {
                        Text(text = error, style = InovaType.bodySmall, color = InovaStatusError)
                    }
                }
                itemsIndexed(filtered) { index, strategy ->
                    StrategyCard(
                        strategy = strategy,
                        index = index + 1,
                        onEdit = { viewModel.startEdit(strategy) },
                        onHistory = { viewModel.openHistory(strategy) },
                        onDelete = { showDeleteDialog = strategy }
                    )
                }
            }
        }
    }

    showDeleteDialog?.let { strategy ->
        AlertDialog(
            onDismissRequest = { showDeleteDialog = null },
            containerColor = InovaSurface,
            titleContentColor = InovaTextPrimary,
            textContentColor = InovaTextSecondary,
            title = { Text("Excluir orientação", style = InovaType.sectionTitle) },
            text = {
                Text(
                    "Deseja realmente excluir \"${strategy.title}\"? O histórico será preservado.",
                    style = InovaType.body
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteStrategy(strategy.id)
                    showDeleteDialog = null
                }) {
                    Text("Excluir", style = InovaType.cardLabel, color = InovaStatusError)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = null }) {
                    Text("Cancelar", style = InovaType.cardLabel, color = InovaTextSecondary)
                }
            }
        )
    }

    viewModel.historyOf?.let { strategy ->
        HistoryDialog(viewModel = viewModel, strategy = strategy)
    }
}

@Composable
private fun StrategyForm(viewModel: LiderancaViewModel, modifier: Modifier = Modifier) {
    InovaCard(modifier = modifier, contentPadding = PaddingValues(18.dp)) {
        Text(
            text = if (viewModel.editingId == null) "Nova orientação" else "Editar orientação",
            style = InovaType.sectionTitle,
            color = InovaTextPrimary
        )

        Spacer(modifier = Modifier.height(20.dp))

        InovaFormField(
            label = "Título",
            value = viewModel.formTitle,
            onValueChange = { viewModel.formTitle = it }
        )
        Spacer(modifier = Modifier.height(20.dp))
        InovaFormField(
            label = "Descrição",
            value = viewModel.formDescription,
            onValueChange = { viewModel.formDescription = it },
            singleLine = false,
            minLines = 2
        )
        Spacer(modifier = Modifier.height(20.dp))
        InovaFormField(
            label = "Categoria",
            value = viewModel.formCategory,
            onValueChange = { viewModel.formCategory = it }
        )
        Spacer(modifier = Modifier.height(20.dp))
        InovaFormField(
            label = "Campanha",
            value = viewModel.formCampaign,
            onValueChange = { viewModel.formCampaign = it }
        )

        Spacer(modifier = Modifier.height(20.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            InovaSwitch(
                checked = viewModel.formActive,
                onCheckedChange = { viewModel.formActive = it }
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = if (viewModel.formActive) "Orientação vigente" else "Orientação inativa",
                style = InovaType.bodySmall,
                color = InovaTextSecondary
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            InovaOutlineButton(
                text = "Cancelar",
                onClick = { viewModel.cancelForm() },
                modifier = Modifier.weight(1f)
            )
            InovaFilledButton(
                text = "Salvar",
                onClick = { viewModel.saveStrategy() },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun StrategyCard(
    strategy: Strategy,
    index: Int,
    onEdit: () -> Unit,
    onHistory: () -> Unit,
    onDelete: () -> Unit
) {
    InovaCard(accent = strategy.active) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            MonoCounter(index = index)
            StatusBadge(
                text = if (strategy.active) "Ativa" else "Inativa",
                color = if (strategy.active) InovaStatusDone else InovaTextDisabled
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

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
                if (strategy.category.isNotBlank()) InovaTag(text = strategy.category)
                if (strategy.campaign.isNotBlank()) {
                    InovaTag(text = strategy.campaign, color = InovaTextTertiary)
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))
        InovaDivider()
        Spacer(modifier = Modifier.height(6.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            if (strategy.date.isNotBlank()) {
                MonoLabel(
                    text = strategy.date,
                    color = InovaTextTertiary,
                    style = InovaType.monoTiny
                )
            } else {
                Spacer(modifier = Modifier.width(1.dp))
            }

            Row {
                InovaIconButton(
                    icon = Icons.Outlined.History,
                    contentDescription = "Histórico",
                    onClick = onHistory,
                    tint = InovaBlueLight
                )
                InovaIconButton(
                    icon = Icons.Outlined.Edit,
                    contentDescription = "Editar",
                    onClick = onEdit,
                    tint = InovaTextSecondary
                )
                InovaIconButton(
                    icon = Icons.Outlined.Delete,
                    contentDescription = "Excluir",
                    onClick = onDelete,
                    tint = InovaStatusError
                )
            }
        }
    }
}

@Composable
private fun HistoryDialog(viewModel: LiderancaViewModel, strategy: Strategy) {
    AlertDialog(
        onDismissRequest = { viewModel.closeHistory() },
        containerColor = InovaSurface,
        titleContentColor = InovaTextPrimary,
        textContentColor = InovaTextSecondary,
        title = {
            Text("Histórico: ${strategy.title}", style = InovaType.sectionTitle)
        },
        text = {
            when {
                viewModel.historyLoading -> androidx.compose.foundation.layout.Box(
                    modifier = Modifier.fillMaxWidth().height(120.dp)
                ) { InovaLoading() }

                viewModel.history.isEmpty() -> Text(
                    "Nenhum registro de histórico.",
                    style = InovaType.bodySmall
                )

                else -> Column(
                    modifier = Modifier.verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    viewModel.history.forEach { entry ->
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                StatusBadge(text = entry.action, color = InovaBlueLight)
                                MonoLabel(
                                    text = entry.changedAt.take(19).replace("T", " "),
                                    color = InovaTextTertiary,
                                    style = InovaType.monoTiny
                                )
                            }
                            Spacer(modifier = Modifier.height(7.dp))
                            Text(
                                text = entry.title,
                                style = InovaType.cardLabel,
                                color = InovaTextPrimary
                            )
                            if (entry.description.isNotBlank()) {
                                Spacer(modifier = Modifier.height(3.dp))
                                Text(
                                    text = entry.description,
                                    style = InovaType.bodyTiny,
                                    color = InovaTextSecondary
                                )
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            InovaDivider()
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { viewModel.closeHistory() }) {
                Text("Fechar", style = InovaType.cardLabel, color = InovaBlueLight)
            }
        }
    )
}
