package br.com.fiap.inovagab.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import br.com.fiap.inovagab.ui.theme.InovaTextPrimary
import br.com.fiap.inovagab.ui.theme.InovaTextSecondary
import br.com.fiap.inovagab.ui.theme.InovaType

/**
 * Linha de dois cards com a mesma altura.
 *
 * Sem isto, um rótulo que quebra em duas linhas (ou um número em grau maior)
 * deixa o card vizinho mais baixo e a grade fica desalinhada.
 */
@Composable
fun InovaCardRow(
    modifier: Modifier = Modifier,
    spacing: androidx.compose.ui.unit.Dp = br.com.fiap.inovagab.ui.theme.InovaSpacing.card,
    content: @Composable androidx.compose.foundation.layout.RowScope.() -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(androidx.compose.foundation.layout.IntrinsicSize.Min),
        horizontalArrangement = Arrangement.spacedBy(spacing),
        content = content
    )
}

/**
 * Card de ação rápida: tile de ícone, contador mono, rótulo e descrição.
 * É o bloco que forma as grades das telas iniciais de cada perfil.
 */
@Composable
fun InovaActionCard(
    icon: ImageVector,
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    index: Int? = null,
    primary: Boolean = false,
    onClick: (() -> Unit)? = null
) {
    InovaCard(modifier = modifier, onClick = onClick) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconTile(icon = icon, primary = primary, contentDescription = null)
            if (index != null) {
                MonoCounter(index = index)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = title,
            style = InovaType.cardLabel,
            color = InovaTextPrimary
        )

        if (subtitle != null) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = subtitle,
                style = InovaType.bodyTiny,
                color = InovaTextSecondary
            )
        }
    }
}

/**
 * Card de métrica: número grande com a unidade em azul claro e o rótulo abaixo.
 * Usado no "Resumo geral" da liderança e nos KPIs do dashboard.
 */
@Composable
fun InovaMetricCard(
    value: String,
    label: String,
    modifier: Modifier = Modifier,
    unit: String? = null,
    index: Int? = null,
    valueColor: androidx.compose.ui.graphics.Color = InovaTextPrimary
) {
    InovaCard(modifier = modifier) {
        if (index != null) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                MonoCounter(index = index)
            }
            Spacer(modifier = Modifier.height(4.dp))
        }

        // Números curtos ficam no tamanho cheio; valores longos (moeda) caem
        // para o grau menor para não quebrar dentro do card.
        val valueStyle = if (value.length > 7) InovaType.metricSmall else InovaType.metric

        Row(verticalAlignment = Alignment.Bottom) {
            Text(
                text = value,
                style = valueStyle,
                color = valueColor,
                maxLines = 1
            )
            if (unit != null) {
                Spacer(modifier = Modifier.width(5.dp))
                Text(
                    text = unit,
                    style = InovaType.cardLabel,
                    color = br.com.fiap.inovagab.ui.theme.InovaBlueLight,
                    modifier = Modifier.padding(bottom = 5.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(text = label, style = InovaType.bodyTiny, color = InovaTextSecondary)
    }
}

/**
 * Card de ação em linha — ícone à esquerda, texto à direita.
 * Usado quando o destaque é um único atalho e não uma grade.
 */
@Composable
fun InovaWideActionCard(
    icon: ImageVector,
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    primary: Boolean = true,
    onClick: (() -> Unit)? = null
) {
    InovaCard(modifier = modifier, onClick = onClick) {
        Row(
            modifier = Modifier.fillMaxWidth().heightIn(min = 30.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconTile(icon = icon, primary = primary, contentDescription = null)
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, style = InovaType.cardLabel, color = InovaTextPrimary)
                if (subtitle != null) {
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = subtitle,
                        style = InovaType.bodyTiny,
                        color = InovaTextSecondary
                    )
                }
            }
        }
    }
}
