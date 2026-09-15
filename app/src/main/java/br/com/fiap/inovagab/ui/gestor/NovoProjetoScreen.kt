package br.com.fiap.inovagab.ui.gestor

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import br.com.fiap.inovagab.data.model.Project
import br.com.fiap.inovagab.data.model.Strategy
import br.com.fiap.inovagab.data.repository.IdeaRepository
import br.com.fiap.inovagab.data.repository.ProjectRepository
import br.com.fiap.inovagab.data.repository.StrategyRepository
import br.com.fiap.inovagab.ui.components.InovaCard
import br.com.fiap.inovagab.ui.components.InovaDivider
import br.com.fiap.inovagab.ui.components.InovaFormField
import br.com.fiap.inovagab.ui.components.InovaInlineMessage
import br.com.fiap.inovagab.ui.components.InovaLoading
import br.com.fiap.inovagab.ui.components.InovaPickerField
import br.com.fiap.inovagab.ui.components.InovaPrimaryButton
import br.com.fiap.inovagab.ui.components.InovaScreen
import br.com.fiap.inovagab.ui.components.InovaSegmentedToggle
import br.com.fiap.inovagab.ui.components.InovaTopBar
import br.com.fiap.inovagab.ui.components.projectStatusLabel
import br.com.fiap.inovagab.ui.theme.InovaBlueLight
import br.com.fiap.inovagab.ui.theme.InovaDurationDefault
import br.com.fiap.inovagab.ui.theme.InovaTextPrimary
import br.com.fiap.inovagab.ui.theme.InovaType
import br.com.fiap.inovagab.ui.theme.inovaTween
import kotlinx.coroutines.launch

private val STATUS_OPTIONS = listOf("PLANEJADO", "EM_ANDAMENTO", "CONCLUIDO", "CANCELADO")

/**
 * Cadastro e edição de projeto.
 *
 * - `ideaId` preenchido: o projeto nasce de uma ideia aprovada.
 * - `projectId` preenchido: o gestor está atualizando resultados de um projeto existente.
 */
@Composable
fun NovoProjetoScreen(
    ideaId: String? = null,
    projectId: String? = null,
    onBack: () -> Unit
) {
    var nome by remember { mutableStateOf("") }
    var descricao by remember { mutableStateOf("") }
    var responsavel by remember { mutableStateOf("") }
    var status by remember { mutableStateOf("PLANEJADO") }
    var etapa by remember { mutableStateOf("") }
    var investimento by remember { mutableStateOf("") }
    var retorno by remember { mutableStateOf("") }
    var reducaoCustos by remember { mutableStateOf("") }
    var produtividade by remember { mutableStateOf("") }
    var prazo by remember { mutableStateOf("") }
    var estrategia by remember { mutableStateOf<Strategy?>(null) }
    var pickerOpen by remember { mutableStateOf(false) }

    var estrategias by remember { mutableStateOf<List<Strategy>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var loadingExisting by remember { mutableStateOf(projectId != null) }
    var message by remember { mutableStateOf<String?>(null) }
    var isError by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()
    val projectRepository = remember { ProjectRepository() }
    val strategyRepository = remember { StrategyRepository() }
    val ideaRepository = remember { IdeaRepository() }

    val isEditing = projectId != null

    LaunchedEffect(projectId) {
        // Carrega as orientações antes do projeto para conseguir pré-selecionar a vinculada.
        val loadedStrategies = strategyRepository.getStrategies().getOrDefault(emptyList())
        // Um projeto novo só pode nascer ligado a uma orientação vigente.
        estrategias = loadedStrategies.filter { it.active }

        if (projectId != null) {
            projectRepository.getProject(projectId)
                .onSuccess { project ->
                    nome = project.name
                    descricao = project.description
                    responsavel = project.responsible
                    status = project.status
                    etapa = project.currentStage
                    investimento = project.investment.takeIf { it > 0 }?.toString().orEmpty()
                    retorno = project.financialReturn.takeIf { it > 0 }?.toString().orEmpty()
                    reducaoCustos = project.costReduction.takeIf { it > 0 }?.toString().orEmpty()
                    produtividade = project.productivityGain.takeIf { it > 0 }?.toString().orEmpty()
                    prazo = project.deadline
                    estrategia = loadedStrategies.firstOrNull { it.id == project.strategyId }
                    // Na edição a orientação já vinculada continua selecionável mesmo
                    // que a liderança a tenha desativado depois.
                    estrategias = loadedStrategies.filter {
                        it.active || it.id == project.strategyId
                    }
                }
                .onFailure { message = it.message; isError = true }
            loadingExisting = false
        } else if (ideaId != null) {
            // Conversão de ideia aprovada: o projeto já chega herdando a
            // orientação estratégica que a ideia tinha.
            ideaRepository.getIdea(ideaId)
                .onSuccess { idea ->
                    // Se a orientação da ideia já foi desativada, o gestor precisa
                    // escolher uma vigente — por isso o filtro por `active`.
                    estrategia = loadedStrategies.firstOrNull {
                        it.id == idea.strategyId && it.active
                    }
                }
        }
    }

    InovaScreen(
        header = {
            InovaTopBar(
                title = if (isEditing) "Editar Projeto" else "Novo Projeto",
                onBack = onBack
            )
        }
    ) {
        if (loadingExisting) {
            Box(modifier = Modifier.fillMaxWidth().height(260.dp)) { InovaLoading() }
            return@InovaScreen
        }

        if (ideaId != null && !isEditing) {
            InovaInlineMessage(
                message = "Projeto originado de uma ideia aprovada.",
                isError = false
            )
        }

        InovaCard(contentPadding = PaddingValues(18.dp)) {
            InovaFormField(
                label = "Nome do projeto",
                value = nome,
                onValueChange = { nome = it }
            )
            Spacer(modifier = Modifier.height(20.dp))
            InovaFormField(
                label = "Descrição",
                value = descricao,
                onValueChange = { descricao = it },
                singleLine = false,
                minLines = 2
            )
            Spacer(modifier = Modifier.height(20.dp))
            InovaFormField(
                label = "Responsável",
                value = responsavel,
                onValueChange = { responsavel = it }
            )
            Spacer(modifier = Modifier.height(20.dp))
            InovaFormField(
                label = "Etapa atual",
                value = etapa,
                onValueChange = { etapa = it }
            )
        }

        // ── Status ───────────────────────────────────────────────────────────
        InovaCard(contentPadding = PaddingValues(18.dp)) {
            Text(text = "Status", style = InovaType.cardLabel, color = InovaTextPrimary)
            Spacer(modifier = Modifier.height(12.dp))
            InovaSegmentedToggle(
                options = STATUS_OPTIONS.map { projectStatusLabel(it) },
                selected = projectStatusLabel(status),
                onSelect = { label ->
                    status = STATUS_OPTIONS.first { projectStatusLabel(it) == label }
                },
                scrollable = true
            )
        }

        // ── Orientação estratégica ───────────────────────────────────────────
        InovaCard(contentPadding = PaddingValues(18.dp)) {
            InovaPickerField(
                label = "Orientação estratégica",
                value = estrategia?.title ?: "Selecione uma orientação",
                expanded = pickerOpen,
                onClick = { pickerOpen = !pickerOpen }
            )

            AnimatedVisibility(
                visible = pickerOpen,
                enter = fadeIn(inovaTween(InovaDurationDefault)) +
                    expandVertically(inovaTween(InovaDurationDefault)),
                exit = fadeOut(inovaTween(InovaDurationDefault)) +
                    shrinkVertically(inovaTween(InovaDurationDefault))
            ) {
                Column(modifier = Modifier.padding(top = 14.dp)) {
                    estrategias.forEachIndexed { index, option ->
                        if (index > 0) InovaDivider()
                        StrategyOption(
                            title = option.title,
                            selected = estrategia?.id == option.id,
                            onClick = { estrategia = option; pickerOpen = false }
                        )
                    }
                }
            }
        }

        // ── Resultados ───────────────────────────────────────────────────────
        InovaCard(contentPadding = PaddingValues(18.dp)) {
            InovaFormField(
                label = "Investimento (R$)",
                value = investimento,
                onValueChange = { investimento = it },
                keyboardType = KeyboardType.Decimal
            )
            Spacer(modifier = Modifier.height(20.dp))
            InovaFormField(
                label = "Retorno financeiro (R$)",
                value = retorno,
                onValueChange = { retorno = it },
                keyboardType = KeyboardType.Decimal
            )
            Spacer(modifier = Modifier.height(20.dp))
            InovaFormField(
                label = "Redução de custos (R$)",
                value = reducaoCustos,
                onValueChange = { reducaoCustos = it },
                keyboardType = KeyboardType.Decimal
            )
            Spacer(modifier = Modifier.height(20.dp))
            InovaFormField(
                label = "Ganho de produtividade (%)",
                value = produtividade,
                onValueChange = { produtividade = it },
                keyboardType = KeyboardType.Decimal
            )
            Spacer(modifier = Modifier.height(20.dp))
            InovaFormField(
                label = "Prazo (dd/mm/aaaa)",
                value = prazo,
                onValueChange = { prazo = it }
            )
        }

        message?.let { InovaInlineMessage(message = it, isError = isError) }

        InovaPrimaryButton(
            text = if (isEditing) "Salvar Alterações" else "Salvar Projeto",
            enabled = !isLoading,
            isLoading = isLoading,
            onClick = {
                if (nome.isBlank()) {
                    message = "Informe o nome do projeto."; isError = true
                    return@InovaPrimaryButton
                }
                // Vínculo obrigatório com a orientação estratégica, como na ideia.
                val estrategiaSelecionada = estrategia
                if (estrategiaSelecionada == null) {
                    message = "Selecione a orientação estratégica do projeto."; isError = true
                    return@InovaPrimaryButton
                }
                coroutineScope.launch {
                    isLoading = true
                    message = null
                    val project = Project(
                        id = projectId.orEmpty(),
                        ideaId = ideaId.orEmpty(),
                        strategyId = estrategiaSelecionada.id,
                        name = nome.trim(),
                        description = descricao.trim(),
                        responsible = responsavel.trim(),
                        status = status,
                        currentStage = etapa.trim(),
                        investment = investimento.toDoubleOrNull() ?: 0.0,
                        financialReturn = retorno.toDoubleOrNull() ?: 0.0,
                        costReduction = reducaoCustos.toDoubleOrNull() ?: 0.0,
                        productivityGain = produtividade.toDoubleOrNull() ?: 0.0,
                        deadline = prazo.trim()
                    )

                    val result = if (isEditing) {
                        projectRepository.updateProject(project)
                    } else {
                        projectRepository.createProject(project)
                    }

                    result
                        .onSuccess {
                            isError = false
                            if (isEditing) {
                                message = "Projeto atualizado com sucesso!"
                            } else {
                                message = "Projeto criado com sucesso!"
                                nome = ""; descricao = ""; responsavel = ""; etapa = ""
                                investimento = ""; retorno = ""; reducaoCustos = ""
                                produtividade = ""; prazo = ""; estrategia = null
                            }
                        }
                        .onFailure { message = it.message; isError = true }
                    isLoading = false
                }
            }
        )

        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
private fun StrategyOption(
    title: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .heightIn(min = 44.dp)
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = title,
            style = InovaType.body,
            color = InovaTextPrimary,
            modifier = Modifier.weight(1f)
        )
        if (selected) {
            Icon(
                imageVector = Icons.Outlined.Check,
                contentDescription = null,
                tint = InovaBlueLight,
                modifier = Modifier.height(16.dp)
            )
        }
    }
}
