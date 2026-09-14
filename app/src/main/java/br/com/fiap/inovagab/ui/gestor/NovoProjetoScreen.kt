package br.com.fiap.inovagab.ui.gestor

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.text.KeyboardOptions
import br.com.fiap.inovagab.data.model.Project
import br.com.fiap.inovagab.data.model.Strategy
import br.com.fiap.inovagab.data.repository.ProjectRepository
import br.com.fiap.inovagab.data.repository.StrategyRepository
import br.com.fiap.inovagab.ui.theme.*
import kotlinx.coroutines.launch

private val STATUS_OPTIONS = listOf("PLANEJADO", "EM_ANDAMENTO", "CONCLUIDO", "CANCELADO")

/**
 * Cadastro e edição de projeto.
 *
 * - `ideaId` preenchido: o projeto nasce de uma ideia aprovada.
 * - `projectId` preenchido: o gestor está atualizando resultados de um projeto existente.
 */
@OptIn(ExperimentalMaterial3Api::class)
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

    var estrategias by remember { mutableStateOf<List<Strategy>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var loadingExisting by remember { mutableStateOf(projectId != null) }
    var message by remember { mutableStateOf<String?>(null) }
    var isError by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()
    val projectRepository = remember { ProjectRepository() }
    val strategyRepository = remember { StrategyRepository() }

    val isEditing = projectId != null

    LaunchedEffect(projectId) {
        // Carrega as orientações antes do projeto para conseguir pré-selecionar a vinculada.
        val loadedStrategies = strategyRepository.getStrategies().getOrDefault(emptyList())
        estrategias = loadedStrategies

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
                }
                .onFailure { message = it.message; isError = true }
            loadingExisting = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (isEditing) "Editar Projeto" else "Novo Projeto",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkBlue)
            )
        },
        containerColor = LightBackground
    ) { innerPadding ->
        if (loadingExisting) {
            Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
                CircularProgressIndicator(
                    modifier = Modifier.align(androidx.compose.ui.Alignment.Center),
                    color = PrimaryBlue
                )
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            if (ideaId != null && !isEditing) {
                Surface(shape = RoundedCornerShape(12.dp), color = SuccessGreen.copy(alpha = 0.12f)) {
                    Text(
                        "Projeto originado de uma ideia aprovada.",
                        fontSize = 12.sp,
                        color = SuccessGreen,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                    )
                }
            }

            ProjectField("Nome do projeto", nome) { nome = it }
            ProjectField("Descrição", descricao, minLines = 2) { descricao = it }
            ProjectField("Responsável", responsavel) { responsavel = it }

            Column {
                Text("Status", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                Spacer(modifier = Modifier.height(6.dp))
                FlowStatusChips(selected = status, onSelect = { status = it })
            }

            ProjectStrategyPicker(
                strategies = estrategias,
                selected = estrategia,
                onSelect = { estrategia = it }
            )

            ProjectField("Etapa atual", etapa) { etapa = it }
            ProjectField("Investimento (R$)", investimento, numeric = true) { investimento = it }
            ProjectField("Retorno financeiro (R$)", retorno, numeric = true) { retorno = it }
            ProjectField("Redução de custos (R$)", reducaoCustos, numeric = true) { reducaoCustos = it }
            ProjectField("Ganho de produtividade (%)", produtividade, numeric = true) { produtividade = it }
            ProjectField("Prazo (dd/mm/aaaa)", prazo) { prazo = it }

            message?.let {
                Text(
                    it,
                    fontSize = 13.sp,
                    color = if (isError) DangerRed else SuccessGreen,
                    fontWeight = FontWeight.Medium
                )
            }

            Button(
                onClick = {
                    if (nome.isBlank()) {
                        message = "Informe o nome do projeto."; isError = true; return@Button
                    }
                    coroutineScope.launch {
                        isLoading = true
                        message = null
                        val project = Project(
                            id = projectId.orEmpty(),
                            ideaId = ideaId.orEmpty(),
                            strategyId = estrategia?.id.orEmpty(),
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
                },
                enabled = !isLoading,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(22.dp),
                        color = Color.White,
                        strokeWidth = 2.5.dp
                    )
                } else {
                    Text(
                        if (isEditing) "Salvar Alterações" else "Salvar Projeto",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun FlowStatusChips(selected: String, onSelect: (String) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        STATUS_OPTIONS.chunked(2).forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                row.forEach { option ->
                    FilterChip(
                        selected = selected == option,
                        onClick = { onSelect(option) },
                        label = { Text(option.replace("_", " "), fontSize = 11.sp) }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProjectStrategyPicker(
    strategies: List<Strategy>,
    selected: Strategy?,
    onSelect: (Strategy?) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column {
        Text(
            "Orientação estratégica",
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = TextPrimary
        )
        Spacer(modifier = Modifier.height(6.dp))
        ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
            OutlinedTextField(
                value = selected?.title ?: "Nenhuma",
                onValueChange = {},
                readOnly = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(MenuAnchorType.PrimaryNotEditable),
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimaryBlue,
                    unfocusedBorderColor = Color(0xFFE2E8F0)
                )
            )
            ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                DropdownMenuItem(
                    text = { Text("Nenhuma") },
                    onClick = { onSelect(null); expanded = false }
                )
                strategies.forEach { strategy ->
                    DropdownMenuItem(
                        text = { Text(strategy.title, fontSize = 13.sp) },
                        onClick = { onSelect(strategy); expanded = false }
                    )
                }
            }
        }
    }
}

@Composable
private fun ProjectField(
    label: String,
    value: String,
    minLines: Int = 1,
    numeric: Boolean = false,
    onValueChange: (String) -> Unit
) {
    Column {
        Text(label, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            minLines = minLines,
            keyboardOptions = if (numeric) {
                KeyboardOptions(keyboardType = KeyboardType.Decimal)
            } else {
                KeyboardOptions.Default
            },
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PrimaryBlue,
                unfocusedBorderColor = Color(0xFFE2E8F0)
            )
        )
    }
}
