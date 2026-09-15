package br.com.fiap.inovagab.ui.operador

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import br.com.fiap.inovagab.ui.components.InovaFormField
import br.com.fiap.inovagab.ui.components.InovaIconButton
import br.com.fiap.inovagab.ui.components.InovaListScreen
import br.com.fiap.inovagab.ui.components.InovaLoading
import br.com.fiap.inovagab.ui.components.InovaTag
import br.com.fiap.inovagab.ui.components.InovaTopBar
import br.com.fiap.inovagab.ui.components.MonoCounter
import br.com.fiap.inovagab.ui.components.MonoLabel
import br.com.fiap.inovagab.ui.theme.InovaBlueLight
import br.com.fiap.inovagab.ui.theme.InovaSpacing
import br.com.fiap.inovagab.ui.theme.InovaStatusDone
import br.com.fiap.inovagab.ui.theme.InovaStatusError
import br.com.fiap.inovagab.ui.theme.InovaSurface
import br.com.fiap.inovagab.ui.theme.InovaTextPrimary
import br.com.fiap.inovagab.ui.theme.InovaTextSecondary
import br.com.fiap.inovagab.ui.theme.InovaTextTertiary
import br.com.fiap.inovagab.ui.theme.InovaType
import kotlinx.coroutines.launch

@Composable
fun MinhasIdeiasNewScreen(
    onBack: () -> Unit,
    onNovaIdeia: () -> Unit = {}
) {
    val repository = remember { IdeaRepository() }
    val scope = rememberCoroutineScope()

    var ideas by remember { mutableStateOf<List<Idea>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMsg by remember { mutableStateOf<String?>(null) }
    var feedback by remember { mutableStateOf<String?>(null) }

    var editing by remember { mutableStateOf<Idea?>(null) }
    var deleting by remember { mutableStateOf<Idea?>(null) }

    suspend fun reload() {
        repository.getMyIdeas()
            .onSuccess { ideas = it; errorMsg = null }
            .onFailure { errorMsg = it.message }
        isLoading = false
    }

    LaunchedEffect(Unit) { reload() }

    InovaListScreen(
        header = {
            InovaTopBar(
                title = "Minhas Ideias",
                onBack = onBack,
                actions = {
                    if (!isLoading) {
                        MonoLabel(
                            text = "${ideas.size}",
                            color = InovaTextTertiary,
                            style = InovaType.mono
                        )
                    }
                    // Cadastrar sem precisar voltar para a home: é a ação que
                    // mais se quer logo depois de olhar a própria lista.
                    InovaIconButton(
                        icon = Icons.Outlined.Add,
                        contentDescription = "Cadastrar nova ideia",
                        onClick = onNovaIdeia,
                        tint = InovaBlueLight
                    )
                }
            )
        }
    ) {
        when {
            isLoading -> InovaLoading()
            errorMsg != null -> InovaErrorState(errorMsg!!)
            ideas.isEmpty() -> InovaEmptyState("Você ainda não cadastrou nenhuma ideia.")
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
                feedback?.let { text ->
                    item {
                        Text(text = text, style = InovaType.bodySmall, color = InovaStatusDone)
                    }
                }
                itemsIndexed(ideas) { index, idea ->
                    IdeaCard(
                        idea = idea,
                        index = index + 1,
                        onEdit = { editing = idea },
                        onDelete = { deleting = idea }
                    )
                }
            }
        }
    }

    editing?.let { idea ->
        EditIdeaDialog(
            idea = idea,
            onDismiss = { editing = null },
            onConfirm = { title, problem, solution, area, benefit ->
                scope.launch {
                    repository.updateIdea(idea.id, title, problem, solution, area, benefit, idea.strategyId)
                        .onSuccess { feedback = "Ideia atualizada."; reload() }
                        .onFailure { errorMsg = it.message }
                    editing = null
                }
            }
        )
    }

    deleting?.let { idea ->
        AlertDialog(
            onDismissRequest = { deleting = null },
            containerColor = InovaSurface,
            titleContentColor = InovaTextPrimary,
            textContentColor = InovaTextSecondary,
            title = { Text("Excluir ideia", style = InovaType.sectionTitle) },
            text = {
                Text("Deseja realmente excluir \"${idea.title}\"?", style = InovaType.body)
            },
            confirmButton = {
                TextButton(onClick = {
                    scope.launch {
                        repository.deleteIdea(idea.id)
                            .onSuccess { feedback = "Ideia excluída."; reload() }
                            .onFailure { errorMsg = it.message }
                        deleting = null
                    }
                }) {
                    Text("Excluir", style = InovaType.cardLabel, color = InovaStatusError)
                }
            },
            dismissButton = {
                TextButton(onClick = { deleting = null }) {
                    Text("Cancelar", style = InovaType.cardLabel, color = InovaTextSecondary)
                }
            }
        )
    }
}

@Composable
private fun IdeaCard(
    idea: Idea,
    index: Int,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    // Ideias já avaliadas pelo gestor ficam somente leitura, como no backend.
    val editable = idea.status == "EM_ANALISE" || idea.status == "PRIORIZADA"
    val highlighted = idea.priority == "ALTA"

    InovaCard(accent = highlighted) {
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

        if (idea.area.isNotBlank() || idea.benefit.isNotBlank() || idea.strategyTitle.isNotBlank()) {
            Spacer(modifier = Modifier.height(12.dp))
            InovaDivider()
            Spacer(modifier = Modifier.height(12.dp))

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                InfoRow(label = "Área", value = idea.area)
                InfoRow(label = "Benefício", value = idea.benefit)
                InfoRow(label = "Estratégia", value = idea.strategyTitle)
            }
        }

        if (idea.priority != "NORMAL" || idea.convertedToProject) {
            Spacer(modifier = Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                if (idea.priority != "NORMAL") {
                    InovaTag(
                        text = "Prioridade: ${idea.priority}",
                        color = if (idea.priority == "ALTA") InovaStatusError else InovaTextSecondary
                    )
                }
                if (idea.convertedToProject) {
                    InovaTag(text = "Convertida em projeto", color = InovaStatusDone)
                }
            }
        }

        if (editable) {
            Spacer(modifier = Modifier.height(12.dp))
            InovaDivider()
            Spacer(modifier = Modifier.height(4.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                CardAction(
                    text = "Editar",
                    icon = Icons.Outlined.Edit,
                    color = InovaBlueLight,
                    onClick = onEdit
                )
                CardAction(
                    text = "Excluir",
                    icon = Icons.Outlined.Delete,
                    color = InovaStatusError,
                    onClick = onDelete
                )
            }
        }
    }
}

@Composable
private fun CardAction(
    text: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: androidx.compose.ui.graphics.Color,
    onClick: () -> Unit
) {
    TextButton(onClick = onClick, modifier = Modifier.height(44.dp)) {
        androidx.compose.material3.Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.height(15.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(text = text, style = InovaType.bodySmall, color = color)
    }
}

@Composable
private fun EditIdeaDialog(
    idea: Idea,
    onDismiss: () -> Unit,
    onConfirm: (String, String, String, String, String) -> Unit
) {
    var title by remember { mutableStateOf(idea.title) }
    var problem by remember { mutableStateOf(idea.problem) }
    var solution by remember { mutableStateOf(idea.solution) }
    var area by remember { mutableStateOf(idea.area) }
    var benefit by remember { mutableStateOf(idea.benefit) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = InovaSurface,
        titleContentColor = InovaTextPrimary,
        textContentColor = InovaTextSecondary,
        title = { Text("Editar ideia", style = InovaType.sectionTitle) },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                InovaFormField(label = "Título", value = title, onValueChange = { title = it })
                InovaFormField(
                    label = "Problema",
                    value = problem,
                    onValueChange = { problem = it },
                    singleLine = false,
                    minLines = 2
                )
                InovaFormField(
                    label = "Solução",
                    value = solution,
                    onValueChange = { solution = it },
                    singleLine = false,
                    minLines = 2
                )
                InovaFormField(label = "Área", value = area, onValueChange = { area = it })
                InovaFormField(label = "Benefício", value = benefit, onValueChange = { benefit = it })
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirm(title, problem, solution, area, benefit) },
                enabled = title.isNotBlank() && problem.isNotBlank() && solution.isNotBlank()
            ) {
                Text("Salvar", style = InovaType.cardLabel, color = InovaBlueLight)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar", style = InovaType.cardLabel, color = InovaTextSecondary)
            }
        }
    )
}
