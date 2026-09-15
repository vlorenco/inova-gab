package br.com.fiap.inovagab.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import br.com.fiap.inovagab.ui.theme.InovaBlue
import br.com.fiap.inovagab.ui.theme.InovaBlueDeep
import br.com.fiap.inovagab.ui.theme.InovaBlueLight
import br.com.fiap.inovagab.ui.theme.InovaBlueTint
import br.com.fiap.inovagab.ui.theme.InovaBorder
import br.com.fiap.inovagab.ui.theme.InovaDurationDefault
import br.com.fiap.inovagab.ui.theme.InovaHalo
import br.com.fiap.inovagab.ui.theme.InovaNavSurface
import br.com.fiap.inovagab.ui.theme.InovaShapes
import br.com.fiap.inovagab.ui.theme.InovaSize
import br.com.fiap.inovagab.ui.theme.InovaStatusError
import br.com.fiap.inovagab.ui.theme.InovaTextDisabled
import br.com.fiap.inovagab.ui.theme.InovaTextPrimary
import br.com.fiap.inovagab.ui.theme.InovaTextTertiary
import br.com.fiap.inovagab.ui.theme.InovaType
import br.com.fiap.inovagab.ui.theme.inovaTween

// ─────────────────────────────────────────────────────────────────────────────
// Blocos maiores: gamificação, faixa de incentivo, bottom nav e estados.
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Bloco de gamificação do topo da home do operador.
 * Pontos e posição ficam à vista, não escondidos no ranking.
 */
@Composable
fun PointsBlock(
    pointsLabel: String,
    points: Int,
    pointsUnit: String,
    positionLabel: String,
    positionText: String?,
    progress: Float,
    progressCaption: String?,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    InovaPanel(modifier = modifier.then(
        if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier
    )) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                MonoLabel(text = pointsLabel, color = InovaTextTertiary)
                Spacer(modifier = Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = points.toString(),
                        style = InovaType.metric,
                        color = InovaTextPrimary
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = pointsUnit,
                        style = InovaType.cardLabel,
                        color = InovaBlueLight,
                        modifier = Modifier.padding(bottom = 5.dp)
                    )
                }
            }

            if (positionText != null) {
                Column(horizontalAlignment = Alignment.End) {
                    MonoLabel(text = positionLabel, color = InovaTextTertiary)
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier
                            .clip(InovaShapes.pill)
                            .background(InovaBlueTint)
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.EmojiEvents,
                            contentDescription = null,
                            tint = InovaBlueLight,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = positionText,
                            style = InovaType.monoBadge,
                            color = InovaBlueLight,
                            maxLines = 1,
                            softWrap = false
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))
        InovaProgressBar(progress = progress)

        if (progressCaption != null) {
            Spacer(modifier = Modifier.height(8.dp))
            MonoLabel(text = progressCaption, color = InovaTextTertiary, style = InovaType.monoTiny)
        }
    }
}

/**
 * Faixa de incentivo: gradiente azul, halo no canto e ícone em azul claro.
 */
@Composable
fun IncentiveBanner(
    text: String,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(InovaShapes.panel)
            .background(
                Brush.linearGradient(
                    colors = listOf(InovaBlueDeep, InovaBlue),
                    start = Offset.Zero,
                    end = Offset(1000f, 360f) // ≈110°
                )
            )
            .drawBehind {
                val radius = 110.dp.toPx()
                val center = Offset(size.width - 20.dp.toPx(), -10.dp.toPx())
                drawCircle(
                    brush = Brush.radialGradient(
                        colorStops = arrayOf(0f to InovaHalo, 0.7f to Color.Transparent),
                        center = center,
                        radius = radius
                    ),
                    radius = radius,
                    center = center
                )
            }
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = InovaBlueLight,
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.width(14.dp))
            Text(
                text = text,
                style = InovaType.body,
                color = InovaTextPrimary,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

/** Item da barra inferior. */
data class InovaNavItem(
    val label: String,
    val icon: ImageVector,
    val onClick: () -> Unit
)

/**
 * Bottom nav em grade de 4 colunas. O item ativo ganha pílula azul
 * translúcida com ícone e texto em azul claro.
 */
@Composable
fun InovaBottomNav(
    items: List<InovaNavItem>,
    selectedIndex: Int,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(InovaNavSurface)
            .drawBehind {
                drawRect(
                    color = InovaBorder,
                    size = androidx.compose.ui.geometry.Size(size.width, 1.dp.toPx())
                )
            }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            items.forEachIndexed { index, item ->
                val isActive = index == selectedIndex
                val tint by animateColorAsState(
                    targetValue = if (isActive) InovaBlueLight else InovaTextDisabled,
                    animationSpec = inovaTween(InovaDurationDefault),
                    label = "navTint"
                )
                val bg by animateColorAsState(
                    targetValue = if (isActive) InovaBlueTint else Color.Transparent,
                    animationSpec = inovaTween(InovaDurationDefault),
                    label = "navBg"
                )

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clip(InovaShapes.pill)
                        .background(bg)
                        .clickable(onClick = item.onClick)
                        .heightIn(min = InovaSize.touchTarget)
                        .padding(vertical = 7.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label,
                        tint = tint,
                        modifier = Modifier.size(19.dp)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = item.label,
                        style = InovaType.monoTiny,
                        color = tint,
                        maxLines = 1,
                        softWrap = false
                    )
                }
            }
        }
        Spacer(modifier = Modifier.navigationBarsPadding())
    }
}

// ── Estados: loading, erro e vazio ───────────────────────────────────────────

@Composable
fun InovaLoading(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize().padding(40.dp),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(color = InovaBlueLight, strokeWidth = 2.5.dp)
    }
}

@Composable
fun InovaErrorState(message: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize().padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = message,
            style = InovaType.body,
            color = InovaStatusError,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun InovaEmptyState(message: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize().padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = message,
            style = InovaType.body,
            color = InovaTextDisabled,
            textAlign = TextAlign.Center
        )
    }
}

/** Mensagem inline de sucesso ou erro dentro de um formulário. */
@Composable
fun InovaInlineMessage(
    message: String,
    isError: Boolean,
    modifier: Modifier = Modifier
) {
    val color = if (isError) InovaStatusError else br.com.fiap.inovagab.ui.theme.InovaStatusDone
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(InovaShapes.card)
            .background(color.copy(alpha = 0.12f))
            .padding(horizontal = 14.dp, vertical = 11.dp)
    ) {
        Text(text = message, style = InovaType.bodySmall, color = color)
    }
}

