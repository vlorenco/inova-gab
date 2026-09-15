package br.com.fiap.inovagab.ui.gestor

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AutoAwesome
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
import androidx.compose.ui.unit.dp
import br.com.fiap.inovagab.data.model.AiAnalysis
import br.com.fiap.inovagab.data.model.Idea
import br.com.fiap.inovagab.data.repository.IdeaRepository
import br.com.fiap.inovagab.ui.components.IdeaStatusBadge
import br.com.fiap.inovagab.ui.components.InfoRow
import br.com.fiap.inovagab.ui.components.InovaCard
import br.com.fiap.inovagab.ui.components.InovaDivider
import br.com.fiap.inovagab.ui.components.InovaErrorState
import br.com.fiap.inovagab.ui.components.InovaFilledButton
import br.com.fiap.inovagab.ui.components.InovaInlineMessage
import br.com.fiap.inovagab.ui.components.InovaLoading
import br.com.fiap.inovagab.ui.components.InovaOutlineButton
import br.com.fiap.inovagab.ui.components.InovaPanel
import br.com.fiap.inovagab.ui.components.InovaPrimaryButton
import br.com.fiap.inovagab.ui.components.InovaProgressBar
import br.com.fiap.inovagab.ui.components.InovaScreen
import br.com.fiap.inovagab.ui.components.InovaTopBar
import br.com.fiap.inovagab.ui.components.MonoLabel
import br.com.fiap.inovagab.ui.components.StatusBadge
import br.com.fiap.inovagab.ui.components.scoreColor
import br.com.fiap.inovagab.ui.theme.InovaBlueLight
import br.com.fiap.inovagab.ui.theme.InovaStatusDone
import br.com.fiap.inovagab.ui.theme.InovaStatusError
import br.com.fiap.inovagab.ui.theme.InovaTextPrimary
import br.com.fiap.inovagab.ui.theme.InovaTextSecondary
import br.com.fiap.inovagab.ui.theme.InovaTextTertiary
import br.com.fiap.inovagab.ui.theme.InovaType
import kotlinx.coroutines.launch

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

    InovaScreen(
        header = { InovaTopBar(title = "Detalhe da Ideia", onBack = onBack) }
    ) {
        when {
            isLoading -> {
                Box(modifier = Modifier.fillMaxWidth().height(220.dp)) { InovaLoading() }
            }

            idea == null -> {
                Box(modifier = Modifier.fillMaxWidth().height(220.dp)) {
                    InovaErrorState(errorMessage ?: "Ideia não encontrada.")
                }
            }

            else -> {
                val currentIdea = idea!!

                InovaCard(contentPadding = PaddingValues(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = currentIdea.title,
                            style = InovaType.screenTitleSmall,
                            color = InovaTextPrimary,
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        IdeaStatusBadge(status = currentIdea.status)
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    InovaDivider()
                    Spacer(modifier = Modifier.height(16.dp))

                    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        InfoRow(label = "Operador", value = currentIdea.operatorName)
                        InfoRow(label = "Estratégia vinculada", value = currentIdea.strategyTitle)
                        InfoRow(label = "Área", value = currentIdea.area)
                        InfoRow(label = "Prioridade", value = currentIdea.priority)
                        InfoRow(label = "Problema", value = currentIdea.problem)
                        InfoRow(label = "Solução", value = currentIdea.solution)
                        InfoRow(label = "Benefício", value = currentIdea.benefit)
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

                message?.let { InovaInlineMessage(message = it, isError = false) }
                errorMessage?.let { InovaInlineMessage(message = it, isError = true) }

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
                        InovaOutlineButton(
                            text = "Reprovar",
                            onClick = { applyStatus("REJEITADA", "Ideia rejeitada.") },
                            enabled = !actionLoading,
                            color = InovaStatusError,
                            modifier = Modifier.weight(1f)
                        )

                        if (currentIdea.status == "EM_ANALISE") {
                            InovaFilledButton(
                                text = "Priorizar",
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
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    if (currentIdea.status != "APROVADA") {
                        InovaFilledButton(
                            text = "Aprovar",
                            onClick = { applyStatus("APROVADA", "Ideia aprovada!") },
                            enabled = !actionLoading,
                            container = InovaStatusDone,
                            contentColor = br.com.fiap.inovagab.ui.theme.InovaBackground,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    if (currentIdea.status == "APROVADA" && !currentIdea.convertedToProject) {
                        InovaPrimaryButton(
                            text = "Criar Projeto",
                            onClick = { onCriarProjeto(ideaId) }
                        )
                    }

                    if (currentIdea.convertedToProject) {
                        InovaInlineMessage(
                            message = "Esta ideia já foi convertida em projeto.",
                            isError = false
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
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
    InovaPanel {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Outlined.AutoAwesome,
                contentDescription = null,
                tint = InovaBlueLight,
                modifier = Modifier.height(18.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = "Análise inteligente (Gemini)",
                style = InovaType.sectionTitle,
                color = InovaTextPrimary
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        if (analysis == null) {
            Text(
                text = "Peça à IA uma pontuação de impacto, viabilidade, inovação e alinhamento estratégico desta ideia.",
                style = InovaType.bodySmall,
                color = InovaTextSecondary
            )
        } else {
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = "${analysis.score}",
                    style = InovaType.metric,
                    color = scoreColor(analysis.score)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "/100",
                    style = InovaType.bodySmall,
                    color = InovaTextTertiary,
                    modifier = Modifier.padding(bottom = 6.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Box(modifier = Modifier.padding(bottom = 6.dp)) {
                    StatusBadge(
                        text = analysis.recommendation.replace("_", " "),
                        color = scoreColor(analysis.score)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            ScoreBar("Impacto", analysis.impactScore)
            ScoreBar("Viabilidade", analysis.feasibilityScore)
            ScoreBar("Inovação", analysis.innovationScore)
            ScoreBar("Alinhamento estratégico", analysis.strategicAlignmentScore)

            if (analysis.summary.isNotBlank()) {
                Spacer(modifier = Modifier.height(14.dp))
                InovaDivider()
                Spacer(modifier = Modifier.height(14.dp))
                Text(
                    text = analysis.summary,
                    style = InovaType.bodySmall,
                    color = InovaTextSecondary
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        InovaFilledButton(
            text = if (analysis == null) "Analisar com IA" else "Reanalisar com IA",
            onClick = onAnalyze,
            enabled = !isLoading,
            isLoading = isLoading,
            leadingIcon = Icons.Outlined.AutoAwesome,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun ScoreBar(label: String, value: Int) {
    Column(modifier = Modifier.padding(vertical = 6.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            MonoLabel(text = label, color = InovaTextTertiary, style = InovaType.monoTiny)
            Text(text = "$value", style = InovaType.cardLabel, color = InovaTextPrimary)
        }
        Spacer(modifier = Modifier.height(7.dp))
        InovaProgressBar(progress = value / 100f)
    }
}
