package br.com.fiap.inovagab.ui.theme

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.dp

// ─────────────────────────────────────────────────────────────────────────────
// Espaçamento, formas e movimento do sistema INOVA+.
// ─────────────────────────────────────────────────────────────────────────────

object InovaSpacing {
    /** Respiro lateral padrão da tela. */
    val screenHorizontal = 22.dp

    /** Padding vertical do conteúdo. */
    val screenVertical = 18.dp

    /** Distância entre blocos de conteúdo. */
    val block = 18.dp

    /** Distância entre cards de uma mesma lista. */
    val card = 12.dp

    /** Padding interno de card. */
    val cardPadding = 14.dp

    /** Padding interno de painel maior. */
    val panelPadding = 16.dp

    // Header: padding 20 / 22 / 26
    val headerTop = 20.dp
    val headerBottom = 26.dp
}

object InovaShapes {
    /** Card padrão. */
    val card = RoundedCornerShape(12.dp)

    /** Painel / bloco maior. */
    val panel = RoundedCornerShape(14.dp)

    /** Tile de ícone. */
    val tile = RoundedCornerShape(8.dp)

    /** Pílula (botões, badges, toggles). */
    val pill = RoundedCornerShape(999.dp)

    /** Barra de progresso. */
    val progress = RoundedCornerShape(999.dp)
}

object InovaSize {
    /** Tile de ícone. */
    val iconTile = 30.dp

    /** Ícone dentro de tile. */
    val iconInTile = 16.dp

    /** Ícone de apoio em linha. */
    val iconInline = 15.dp

    /** Ícone de ação (header, top bar). */
    val iconAction = 19.dp

    /** Altura do botão primário. */
    val primaryButton = 56.dp

    /** Alvo mínimo de toque. */
    val touchTarget = 44.dp

    /** Espessura da barra de progresso. */
    val progressBar = 4.dp

    /** Borda de card. */
    val border = 1.dp

    /** Marcador lateral de card em destaque. */
    val accentEdge = 3.dp

    /** Altura do logo no header. */
    val headerLogo = 22.dp

    /** Avatar do header. */
    val avatar = 34.dp
}

// ── Movimento: rápido e quieto. 120–180ms, sem bounce. ───────────────────────

val InovaEasing = CubicBezierEasing(0.2f, 0f, 0f, 1f)

fun <T> inovaTween(durationMillis: Int = 160) = tween<T>(
    durationMillis = durationMillis,
    easing = InovaEasing
)

const val InovaDurationFast = 120
const val InovaDurationDefault = 160
const val InovaDurationSlow = 180
