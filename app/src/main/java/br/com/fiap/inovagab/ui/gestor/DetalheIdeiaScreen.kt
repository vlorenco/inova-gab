package br.com.fiap.inovagab.ui.gestor

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.fiap.inovagab.data.model.AiAnalysis
import br.com.fiap.inovagab.data.model.Idea
import br.com.fiap.inovagab.data.repository.IdeaRepository
import br.com.fiap.inovagab.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalheIdeiaScreen(
    ideaId: String,
    onBack: () -> Unit,
    onCriarProjeto: (String) -> Unit
) {
    val repository = remember { IdeaRepository() }
    var idea by remember { mutableStateOf<Idea?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var actionLoading by remember { mutableStateOf(false) }
    var aiLoading by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf<String?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(ideaId) {
        repository.getIdea(ideaId)
            .onSuccess { idea = it }
            .onFailure { errorMessage = it.message }
        isLoading = false
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalhe da Ideia", color = Color.White, fontWeight = FontWeight.Bold) },
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
        Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            when {
                isLoading -> CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = PrimaryBlue
                )
                idea == null -> Text(
                    errorMessage ?: "Ideia não encontrada.",
                    color = DangerRed,
                    modifier = Modifier.align(Alignment.Center).padding(32.dp)
                )
                else -> {
                    val currentIdea = idea!!
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        val statusColor = ideaStatusColor(currentIdea.status)
                        val statusLabel = ideaStatusLabel(currentIdea.status)

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = CardWhite),
                            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                        ) {
                            Column(modifier = Modifier.padding(20.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        currentIdea.title,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary,
                                        modifier = Modifier.weight(1f)
                                    )
                                    Surface(
                                        shape = RoundedCornerShape(20.dp),
                                        color = statusColor.copy(alpha = 0.15f)
                                    ) {
                                        Text(
                                            statusLabel,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = statusColor,
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(16.dp))
                                HorizontalDivider(color = Color(0xFFF1F5F9))
                                Spacer(modifier = Modifier.height(16.dp))

                                InfoRow("Operador", currentIdea.operatorName)
                                InfoRow("Estratégia vinculada", currentIdea.strategyTitle)
                                InfoRow("Área", currentIdea.area)
                                InfoRow("Prioridade", currentIdea.priority)
                                InfoRow("Problema", currentIdea.problem)
                                InfoRow("Solução", currentIdea.solution)
                                InfoRow("Benefício", currentIdea.benefit)
                            }
                        }

                        // ── Funcionalidade Plus: pontuação automática pela IA ──
                        AiAnalysisCard(
                            analysis = currentIdea.aiAnalysis,
                            isLoading = aiLoading,
                            onAnalyze = {
                                coroutineScope.launch {
                                    aiLoading = true
                                    errorMessage = null
                                    message = null
                                    repository.requestAiAnalysis(ideaId)
                                        .onSuccess {
                                            idea = currentIdea.copy(aiAnalysis = it)
                                            message = "Análise concluída pela IA."
                                        }
                                        .onFailure { errorMessage = it.message }
                                    aiLoading = false
                                }
                            }
                        )

                        message?.let {
                            Text(it, fontSize = 13.sp, color = SuccessGreen, fontWeight = FontWeight.Medium)
                        }
                        errorMessage?.let {
                            Text(it, fontSize = 13.sp, color = DangerRed, fontWeight = FontWeight.Medium)
                        }

                        // ── Ações de curadoria ──
                        fun applyStatus(status: String, ok: String) {
                            coroutineScope.launch {
                                actionLoading = true
                                errorMessage = null
                                repository.updateIdeaStatus(ideaId, status)
                                    .onSuccess { idea = it; message = ok }
                                    .onFailure { errorMessage = it.message }
                                actionLoading = false
                            }
                        }

                        if (currentIdea.status != "REJEITADA") {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                OutlinedButton(
                                    onClick = { applyStatus("REJEITADA", "Ideia rejeitada.") },
                                    enabled = !actionLoading,
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = DangerRed),
                                    border = androidx.compose.foundation.BorderStroke(1.5.dp, DangerRed)
                                ) {
                                    Text("Reprovar", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                                }

                                if (currentIdea.status == "EM_ANALISE") {
                                    Button(
                                        onClick = {
                                            coroutineScope.launch {
                                                actionLoading = true
                                                errorMessage = null
                                                repository.updateIdeaPriority(ideaId, "ALTA")
                                                    .onSuccess { idea = it; message = "Ideia priorizada." }
                                                    .onFailure { errorMessage = it.message }
                                                actionLoading = false
                                            }
                                        },
                                        enabled = !actionLoading,
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(12.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = AccentBlue)
                                    ) {
                                        Text("Priorizar", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                                    }
                                }
                            }

                            if (currentIdea.status != "APROVADA") {
                                Button(
                                    onClick = { applyStatus("APROVADA", "Ideia aprovada!") },
                                    enabled = !actionLoading,
                                    modifier = Modifier.fillMaxWidth().height(48.dp),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen)
                                ) {
                                    Text("Aprovar", fontWeight = FontWeight.SemiBold, color = Color.White)
                                }
                            }

                            if (currentIdea.status == "APROVADA" && !currentIdea.convertedToProject) {
                                Button(
                                    onClick = { onCriarProjeto(ideaId) },
                                    modifier = Modifier.fillMaxWidth().height(48.dp),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                                ) {
                                    Text("Criar Projeto", fontWeight = FontWeight.SemiBold)
                                }
                            }

                            if (currentIdea.convertedToProject) {
                                Text(
                                    "Esta ideia já foi convertida em projeto.",
                                    fontSize = 13.sp,
                                    color = SuccessGreen,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun AiAnalysisCard(
    analysis: AiAnalysis?,
    isLoading: Boolean,
    onAnalyze: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.AutoAwesome,
                    contentDescription = null,
                    tint = AccentBlue,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "Análise inteligente (Gemini)",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (analysis == null) {
                Text(
                    "Peça à IA uma pontuação de impacto, viabilidade, inovação e alinhamento estratégico desta ideia.",
                    fontSize = 13.sp,
                    color = TextSecondary
                )
            } else {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        "${analysis.score}",
                        fontSize = 34.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = scoreColor(analysis.score)
                    )
                    Text("/100", fontSize = 14.sp, color = TextSecondary)
                    Spacer(modifier = Modifier.width(12.dp))
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = scoreColor(analysis.score).copy(alpha = 0.15f)
                    ) {
                        Text(
                            analysis.recommendation.replace("_", " "),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = scoreColor(analysis.score),
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                ScoreBar("Impacto", analysis.impactScore)
                ScoreBar("Viabilidade", analysis.feasibilityScore)
                ScoreBar("Inovação", analysis.innovationScore)
                ScoreBar("Alinhamento estratégico", analysis.strategicAlignmentScore)

                if (analysis.summary.isNotBlank()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(analysis.summary, fontSize = 13.sp, color = TextSecondary)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Button(
                onClick = onAnalyze,
                enabled = !isLoading,
                modifier = Modifier.fillMaxWidth().height(46.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AccentBlue)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        if (analysis == null) "Analisar com IA" else "Reanalisar com IA",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun ScoreBar(label: String, value: Int) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(label, fontSize = 12.sp, color = TextSecondary)
            Text("$value", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
        }
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { value / 100f },
            modifier = Modifier.fillMaxWidth().height(6.dp),
            color = scoreColor(value),
            trackColor = Color(0xFFF1F5F9)
        )
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    if (value.isBlank()) return
    Column(modifier = Modifier.padding(bottom = 12.dp)) {
        Text(label, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = TextSecondary)
        Text(value, fontSize = 14.sp, color = TextPrimary)
    }
}
