package br.com.fiap.inovagab.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import java.util.Locale
import br.com.fiap.inovagab.ui.theme.InovaBlueLight
import br.com.fiap.inovagab.ui.theme.InovaStatusDone
import br.com.fiap.inovagab.ui.theme.InovaStatusError
import br.com.fiap.inovagab.ui.theme.InovaStatusRunning
import br.com.fiap.inovagab.ui.theme.InovaTextDisabled

// ─────────────────────────────────────────────────────────────────────────────
// Mapeamento visual dos status. Fica em um lugar só para que ideia, projeto
// e dashboard nunca divirjam de cor ou de rótulo.
// Os rótulos são exatamente os já usados no aplicativo.
// ─────────────────────────────────────────────────────────────────────────────

fun ideaStatusColor(status: String): Color = when (status) {
    "EM_ANALISE" -> InovaStatusRunning
    "PRIORIZADA" -> InovaBlueLight
    "APROVADA" -> InovaStatusDone
    "REJEITADA" -> InovaStatusError
    else -> InovaTextDisabled
}

fun ideaStatusLabel(status: String): String = when (status) {
    "EM_ANALISE" -> "Em análise"
    "PRIORIZADA" -> "Priorizada"
    "APROVADA" -> "Aprovada"
    "REJEITADA" -> "Rejeitada"
    else -> status
}

fun projectStatusColor(status: String): Color = when (status) {
    "PLANEJADO" -> InovaBlueLight
    "EM_ANDAMENTO" -> InovaStatusRunning
    "CONCLUIDO" -> InovaStatusDone
    "CANCELADO" -> InovaStatusError
    else -> InovaTextDisabled
}

fun projectStatusLabel(status: String): String = when (status) {
    "EM_ANDAMENTO" -> "Em andamento"
    "CONCLUIDO" -> "Concluído"
    "PLANEJADO" -> "Planejado"
    "CANCELADO" -> "Cancelado"
    else -> status
}

/** Faixa de cor da nota da IA. */
fun scoreColor(score: Int): Color = when {
    score >= 75 -> InovaStatusDone
    score >= 50 -> InovaStatusRunning
    else -> InovaStatusError
}

/** Badge pronto para o status de uma ideia. */
@Composable
fun IdeaStatusBadge(status: String, modifier: Modifier = Modifier) {
    StatusBadge(
        text = ideaStatusLabel(status),
        color = ideaStatusColor(status),
        modifier = modifier
    )
}

/** Badge pronto para o status de um projeto. */
@Composable
fun ProjectStatusBadge(status: String, modifier: Modifier = Modifier) {
    StatusBadge(
        text = projectStatusLabel(status),
        color = projectStatusColor(status),
        modifier = modifier
    )
}

/**
 * Locale fixo: sem ele o formato segue o idioma do aparelho e um celular em
 * inglês mostra "R$ 28,000.00" no lugar de "R$ 28.000,00".
 */
private val LocaleBr: Locale = Locale.forLanguageTag("pt-BR")

/** Formatação monetária usada nas telas de projeto e dashboard. */
fun formatCurrencyBr(value: Double): String = String.format(LocaleBr, "R$ %,.2f", value)

/** Formatação percentual usada nas telas de projeto e dashboard. */
fun formatPercentBr(value: Double): String = String.format(LocaleBr, "%.1f%%", value)

/** Percentual sem casas decimais, para números de destaque. */
fun formatPercentWholeBr(value: Double): String = String.format(LocaleBr, "%.0f", value)
