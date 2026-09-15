package br.com.fiap.inovagab.ui.lideranca

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
import br.com.fiap.inovagab.data.model.Project
import br.com.fiap.inovagab.data.repository.ProjectRepository
import br.com.fiap.inovagab.ui.components.InfoRow
import br.com.fiap.inovagab.ui.components.InovaCard
import br.com.fiap.inovagab.ui.components.InovaDivider
import br.com.fiap.inovagab.ui.components.InovaEmptyState
import br.com.fiap.inovagab.ui.components.InovaErrorState
import br.com.fiap.inovagab.ui.components.InovaListScreen
import br.com.fiap.inovagab.ui.components.InovaLoading
import br.com.fiap.inovagab.ui.components.InovaTopBar
import br.com.fiap.inovagab.ui.components.MonoCounter
import br.com.fiap.inovagab.ui.components.formatCurrencyBr
import br.com.fiap.inovagab.ui.components.formatPercentBr
import br.com.fiap.inovagab.ui.components.MonoLabel
import br.com.fiap.inovagab.ui.components.ProjectStatusBadge
import br.com.fiap.inovagab.ui.theme.InovaSpacing
import br.com.fiap.inovagab.ui.theme.InovaStatusDone
import br.com.fiap.inovagab.ui.theme.InovaStatusError
import br.com.fiap.inovagab.ui.theme.InovaTextPrimary
import br.com.fiap.inovagab.ui.theme.InovaTextSecondary
import br.com.fiap.inovagab.ui.theme.InovaTextTertiary
import br.com.fiap.inovagab.ui.theme.InovaType

/** A liderança apenas acompanha o andamento: o CRUD é do gestor. */
@Composable
fun LeaderProjectsScreen(
    onBackClick: () -> Unit
) {
    val repository = remember { ProjectRepository() }
    var projects by remember { mutableStateOf<List<Project>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        repository.getProjects()
            .onSuccess { projects = it; isLoading = false }
            .onFailure { errorMsg = it.message; isLoading = false }
    }

    InovaListScreen(
        header = {
            InovaTopBar(
                title = "Projetos",
                onBack = onBackClick,
                actions = {
                    if (!isLoading && errorMsg == null) {
                        MonoLabel(
                            text = "${projects.size}",
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
                    ProjectCard(project = project, index = index + 1)
                }
            }
        }
    }
}

@Composable
private fun ProjectCard(project: Project, index: Int) {
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

        if (project.description.isNotBlank()) {
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = project.description,
                style = InovaType.bodySmall,
                color = InovaTextSecondary
            )
        }

        Spacer(modifier = Modifier.height(14.dp))
        InovaDivider()
        Spacer(modifier = Modifier.height(14.dp))

        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            InfoRow(label = "Orientação estratégica", value = project.strategyTitle)
            InfoRow(label = "Responsável", value = project.responsible)
            InfoRow(label = "Etapa atual", value = project.currentStage)
            // Valor zerado vira string vazia e o InfoRow se esconde sozinho.
            InfoRow(label = "Investimento", value = currencyOrBlank(project.investment))
            InfoRow(label = "Retorno financeiro", value = currencyOrBlank(project.financialReturn))
            InfoRow(label = "Redução de custos", value = currencyOrBlank(project.costReduction))
            InfoRow(
                label = "Ganho de produtividade",
                value = percentOrBlank(project.productivityGain)
            )
            InfoRow(label = "Prazo", value = project.deadline)
        }

        if (project.investment > 0) {
            Spacer(modifier = Modifier.height(14.dp))
            InovaDivider()
            Spacer(modifier = Modifier.height(14.dp))
            Row(verticalAlignment = Alignment.Bottom) {
                MonoLabel(
                    text = "ROI (calculado pela API)",
                    color = InovaTextTertiary,
                    style = InovaType.monoTiny
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = formatPercentBr(project.roi),
                    style = InovaType.metricSmall,
                    color = if (project.roi >= 0) InovaStatusDone else InovaStatusError
                )
            }
        }
    }
}

private fun currencyOrBlank(value: Double): String =
    if (value == 0.0) "" else formatCurrencyBr(value)

private fun percentOrBlank(value: Double): String =
    if (value == 0.0) "" else formatPercentBr(value)
