package br.com.fiap.inovagab.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import br.com.fiap.inovagab.ui.theme.InovaBlue
import br.com.fiap.inovagab.ui.theme.InovaBlueLight
import br.com.fiap.inovagab.ui.theme.InovaBlueTint
import br.com.fiap.inovagab.ui.theme.InovaBlueTintBorder
import br.com.fiap.inovagab.ui.theme.InovaBorder
import br.com.fiap.inovagab.ui.theme.InovaShapes
import br.com.fiap.inovagab.ui.theme.InovaSize
import br.com.fiap.inovagab.ui.theme.InovaSpacing
import br.com.fiap.inovagab.ui.theme.InovaSurface
import br.com.fiap.inovagab.ui.theme.InovaTextFaint
import br.com.fiap.inovagab.ui.theme.InovaTextPrimary
import br.com.fiap.inovagab.ui.theme.InovaTextTertiary
import br.com.fiap.inovagab.ui.theme.InovaTrack
import br.com.fiap.inovagab.ui.theme.InovaType

// ─────────────────────────────────────────────────────────────────────────────
// Card, tile de ícone, contador mono, seção e linha de informação.
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Card padrão do sistema.
 *
 * @param accent acrescenta o marcador lateral de 3dp em azul claro,
 *               usado em itens com prioridade ou destaque.
 */
@Composable
fun InovaCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    accent: Boolean = false,
    shape: Shape = InovaShapes.card,
    contentPadding: PaddingValues = PaddingValues(InovaSpacing.cardPadding),
    content: @Composable ColumnScope.() -> Unit
) {
    val base = modifier
        .fillMaxWidth()
        .clip(shape)
        .background(InovaSurface)
        .border(BorderStroke(InovaSize.border, InovaBorder), shape)

    val withAccent = if (accent) {
        base.drawBehind {
            drawRect(
                color = InovaBlueLight,
                topLeft = Offset.Zero,
                size = Size(InovaSize.accentEdge.toPx(), size.height)
            )
        }
    } else {
        base
    }

    Column(
        modifier = (if (onClick != null) withAccent.clickable(onClick = onClick) else withAccent)
            .padding(contentPadding),
        content = content
    )
}

/** Painel maior — mesmo material do card, com raio e respiro um pouco maiores. */
@Composable
fun InovaPanel(
    modifier: Modifier = Modifier,
    accent: Boolean = false,
    content: @Composable ColumnScope.() -> Unit
) = InovaCard(
    modifier = modifier,
    accent = accent,
    shape = InovaShapes.panel,
    contentPadding = PaddingValues(InovaSpacing.panelPadding),
    content = content
)

/**
 * Tile de ícone de 30dp.
 *
 * @param primary azul sólido com ícone branco (ação principal do bloco).
 *                Quando falso, azul translúcido com ícone azul claro.
 */
@Composable
fun IconTile(
    icon: ImageVector,
    modifier: Modifier = Modifier,
    primary: Boolean = false,
    contentDescription: String? = null
) {
    val shape = InovaShapes.tile
    Box(
        modifier = modifier
            .size(InovaSize.iconTile)
            .clip(shape)
            .background(if (primary) InovaBlue else InovaBlueTint)
            .then(
                if (primary) Modifier
                else Modifier.border(BorderStroke(InovaSize.border, InovaBlueTintBorder), shape)
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = if (primary) InovaTextPrimary else InovaBlueLight,
            modifier = Modifier.size(InovaSize.iconInTile)
        )
    }
}

/** Contador mono do canto do card: 01, 02, 03… */
@Composable
fun MonoCounter(index: Int, modifier: Modifier = Modifier) {
    Text(
        text = index.toString().padStart(2, '0'),
        style = InovaType.monoTiny,
        color = InovaTextFaint,
        maxLines = 1,
        modifier = modifier
    )
}

/** Rótulo mono genérico — sempre maiúsculo e em linha única. */
@Composable
fun MonoLabel(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = InovaTextTertiary,
    style: androidx.compose.ui.text.TextStyle = InovaType.mono
) {
    Text(
        text = text.uppercase(),
        style = style,
        color = color,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        softWrap = false,
        modifier = modifier
    )
}

/**
 * Cabeçalho de seção: título, hairline ocupando o resto da linha e
 * total em mono à direita.
 */
@Composable
fun SectionHeader(
    title: String,
    modifier: Modifier = Modifier,
    trailing: String? = null
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = title, style = InovaType.sectionTitle, color = InovaTextPrimary)
        Spacer(modifier = Modifier.width(12.dp))
        Box(
            modifier = Modifier
                .weight(1f)
                .height(1.dp)
                .background(InovaBorder)
        )
        if (trailing != null) {
            Spacer(modifier = Modifier.width(12.dp))
            MonoLabel(text = trailing, color = InovaTextFaint, style = InovaType.monoTiny)
        }
    }
}

/**
 * Linha de informação de leitura: rótulo em mono acima, valor em Inter abaixo.
 * Usada nas telas de detalhe.
 */
@Composable
fun InfoRow(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    if (value.isBlank()) return
    Column(modifier = modifier.fillMaxWidth()) {
        MonoLabel(text = label, color = InovaTextTertiary, style = InovaType.monoTiny)
        Spacer(modifier = Modifier.height(3.dp))
        Text(text = value, style = InovaType.body, color = InovaTextPrimary)
    }
}

/** Divisor interno de card. */
@Composable
fun InovaDivider(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(InovaTrack)
    )
}

