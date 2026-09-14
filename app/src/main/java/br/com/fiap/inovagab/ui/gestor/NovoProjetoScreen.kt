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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.fiap.inovagab.data.model.Project
import br.com.fiap.inovagab.data.repository.IdeaRepository
import br.com.fiap.inovagab.data.repository.ProjectRepository
import br.com.fiap.inovagab.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NovoProjetoScreen(
    ideaId: String? = null,
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

    var isLoading by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf<String?>(null) }
    var isError by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()
    val projectRepository = remember { ProjectRepository() }
    val ideaRepository = remember { IdeaRepository() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Novo Projeto", color = Color.White, fontWeight = FontWeight.Bold) },
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            ProjectField("Nome do projeto", nome) { nome = it }
            ProjectField("Descrição", descricao, minLines = 2) { descricao = it }
            ProjectField("Responsável", responsavel) { responsavel = it }

            // Status dropdown
            Column {
                Text("Status", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                Spacer(modifier = Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("PLANEJADO", "EM_ANDAMENTO", "CONCLUIDO").forEach { s ->
                        FilterChip(
                            selected = status == s,
                            onClick = { status = s },
                            label = { Text(s.replace("_", " "), fontSize = 11.sp) }
                        )
                    }
                }
            }

            ProjectField("Etapa atual", etapa) { etapa = it }
            ProjectField("Investimento (R$)", investimento) { investimento = it }
            ProjectField("Retorno financeiro (R$)", retorno) { retorno = it }
            ProjectField("Redução de custos (R$)", reducaoCustos) { reducaoCustos = it }
            ProjectField("Ganho de produtividade (%)", produtividade) { produtividade = it }
            ProjectField("Prazo (dd/mm/aaaa)", prazo) { prazo = it }

            if (message != null) {
                Text(message!!, fontSize = 13.sp, color = if (isError) DangerRed else SuccessGreen, fontWeight = FontWeight.Medium)
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
                            ideaId = ideaId ?: "",
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
                        projectRepository.createProject(project)
                            .onSuccess {
                                if (!ideaId.isNullOrBlank()) {
                                    ideaRepository.markConvertedToProject(ideaId)
                                }
                                message = "Projeto criado com sucesso!"
                                isError = false
                                nome = ""; descricao = ""; responsavel = ""; etapa = ""
                                investimento = ""; retorno = ""; reducaoCustos = ""; produtividade = ""; prazo = ""
                            }
                            .onFailure { message = "Erro: ${it.message}"; isError = true }
                        isLoading = false
                    }
                },
                enabled = !isLoading,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(22.dp), color = Color.White, strokeWidth = 2.5.dp)
                } else {
                    Text("Salvar Projeto", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun ProjectField(label: String, value: String, minLines: Int = 1, onValueChange: (String) -> Unit) {
    Column {
        Text(label, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            minLines = minLines,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PrimaryBlue,
                unfocusedBorderColor = Color(0xFFE2E8F0)
            )
        )
    }
}
