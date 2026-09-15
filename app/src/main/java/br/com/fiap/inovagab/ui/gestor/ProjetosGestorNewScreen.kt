package br.com.fiap.inovagab.ui.gestor

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
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
import br.com.fiap.inovagab.data.model.Project
import br.com.fiap.inovagab.data.repository.ProjectRepository
import br.com.fiap.inovagab.ui.components.InfoRow
import br.com.fiap.inovagab.ui.components.InovaCard
import br.com.fiap.inovagab.ui.components.InovaDivider
import br.com.fiap.inovagab.ui.components.InovaEmptyState
import br.com.fiap.inovagab.ui.components.InovaErrorState
import br.com.fiap.inovagab.ui.components.InovaIconButton
import br.com.fiap.inovagab.ui.components.InovaListScreen
import br.com.fiap.inovagab.ui.components.InovaLoading
import br.com.fiap.inovagab.ui.components.InovaTopBar
import br.com.fiap.inovagab.ui.components.MonoCounter
import br.com.fiap.inovagab.ui.components.MonoLabel
import br.com.fiap.inovagab.ui.components.ProjectStatusBadge
import br.com.fiap.inovagab.ui.components.formatCurrencyBr
import br.com.fiap.inovagab.ui.components.formatPercentBr
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
fun ProjetosGestorNewScreen(
    onBack: () -> Unit,
    onNovoProjeto: () -> Unit,
    onEditarProjeto: (String) -> Unit
) {
    val repository = remember { ProjectRepository() }
    val scope = rememberCoroutineScope()

    var projects by remember { mutableStateOf<List<Project>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMsg by remember { mutableStateOf<String?>(null) }
    var deleting by remember { mutableStateOf<Project?>(null) }

    suspend fun reload() {
        repository.getProjects()
            .onSuccess { projects = it; errorMsg = null }
            .onFailure { errorMsg = it.message }
        isLoading = false
    }

    LaunchedEffect(Unit) { reload() }

    InovaListScreen(
        header = {
            InovaTopBar(
                title = "Projetos",
                onBack = onBack,
                actions = {
                    if (!isLoading && errorMsg == null) {
                        MonoLabel(
                            text = "${projects.size}",
                            color = InovaTextTertiary,
                            style = InovaType.mono
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                    }
                    InovaIconButton(
                        icon = Icons.Outlined.Add,
                        contentDescription = "Novo Projeto",
                        onClick = onNovoProjeto
                    )
                }
            )
        }
    ) {
        when {
            isLoading -> InovaLoading()
            errorMsg != null -> InovaErrorState(errorMsg!!)
            projects.isEmpty() -> InovaEmptyState("Nenhum projeto encontrado.")
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
                itemsIndexed(projects) { index, project ->
                    ProjectCardGestor(
                        project = project,
                        index = index + 1,
                        onEdit = { onEditarProjeto(project.id) },
                        onDelete = { deleting = project }
                    )
                }
            }
        }
    }

    deleting?.let { project ->
        AlertDialog(
            onDismissRequest = { deleting = null },
            containerColor = InovaSurface,
            titleContentColor = InovaTextPrimary,
            textContentColor = InovaTextSecondary,
            title = { Text("Excluir projeto", style = InovaType.sectionTitle) },
            text = {
                Text("Deseja realmente excluir \"${project.name}\"?", style = InovaType.body)
            },
            confirmButton = {
                TextButton(onClick = {
                    scope.launch {
                        repository.deleteProject(project.id).onFailure { errorMsg = it.message }
                        deleting = null
                        reload()
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
private fun ProjectCardGestor(
    project: Project,
    index: Int,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    InovaCard(accent = project.status == "EM_ANDAMENTO") {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            MonoCounter(index = index)
            ProjectStatusBadge(status = project.status)
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(text = project.name, style = InovaType.cardLabel, color = InovaTextPrimary)

        Spacer(modifier = Modifier.height(12.dp))
        InovaDivider()
        Spacer(modifier = Modifier.height(12.dp))

        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            InfoRow(label = "Etapa", value = project.currentStage)
            InfoRow(label = "Estratégia", value = project.strategyTitle)
            if (project.investment > 0) {
                InfoRow(label = "Investimento", value = formatCurrencyBr(project.investment))
            }
            if (project.financialReturn > 0) {
                InfoRow(label = "Retorno", value = formatCurrencyBr(project.financialReturn))
            }
        }

        if (project.investment > 0) {
            Spacer(modifier = Modifier.height(14.dp))
            Row(verticalAlignment = Alignment.Bottom) {
                MonoLabel(text = "ROI", color = InovaTextTertiary, style = InovaType.monoTiny)
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = formatPercentBr(project.roi),
                    style = InovaType.metricSmall,
                    color = if (project.roi >= 0) InovaStatusDone else InovaStatusError
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))
        InovaDivider()
        Spacer(modifier = Modifier.height(4.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            TextButton(onClick = onEdit, modifier = Modifier.height(44.dp)) {
                Icon(
                    imageVector = Icons.Outlined.Edit,
                    contentDescription = null,
                    tint = InovaBlueLight,
                    modifier = Modifier.height(15.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Editar / resultados",
                    style = InovaType.bodySmall,
                    color = InovaBlueLight
                )
            }
            TextButton(onClick = onDelete, modifier = Modifier.height(44.dp)) {
                Icon(
                    imageVector = Icons.Outlined.Delete,
                    contentDescription = null,
                    tint = InovaStatusError,
                    modifier = Modifier.height(15.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = "Excluir", style = InovaType.bodySmall, color = InovaStatusError)
            }
        }
    }
}
