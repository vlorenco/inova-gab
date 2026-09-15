package br.com.fiap.inovagab.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import br.com.fiap.inovagab.ui.theme.InovaBlue
import br.com.fiap.inovagab.ui.theme.InovaBlueLight
import br.com.fiap.inovagab.ui.theme.InovaDurationSlow
import br.com.fiap.inovagab.ui.theme.InovaShapes
import br.com.fiap.inovagab.ui.theme.InovaTextPrimary
import br.com.fiap.inovagab.ui.theme.InovaTextSecondary
import br.com.fiap.inovagab.ui.theme.InovaTextTertiary
import br.com.fiap.inovagab.ui.theme.InovaTrack
import br.com.fiap.inovagab.ui.theme.InovaType
import br.com.fiap.inovagab.ui.theme.inovaTween

// ─────────────────────────────────────────────────────────────────────────────
// Primitivas de gráfico.
//
// Regras seguidas em todas elas:
//  · marcas finas, pontas arredondadas, trilha discreta;
//  · 2dp de respiro entre segmentos vizinhos, nunca um contorno;
//  · todo valor tem rótulo direto — a cor nunca é o único código.
//    Isso importa aqui porque o par "em andamento" (âmbar) e "concluído"
//    (verde) da identidade fica no limite de separação para protanopia.
// ─────────────────────────────────────────────────────────────────────────────

/** Espessura padrão das barras de comparação. */
private val BarThickness = 8.dp

/** Respiro entre segmentos vizinhos, na cor da superfície. */
private val SegmentGap = 2.dp

/**
 * Número de destaque que abre um painel: sobrescrito em mono, valor grande
 * e uma linha de apoio.
 */
@Composable
fun InovaHeroMetric(
    eyebrow: String,
    value: String,
    modifier: Modifier = Modifier,
    unit: String? = null,
    valueColor: Color = InovaTextPrimary,
    support: String? = null
) {
    Column(modifier = modifier.fillMaxWidth()) {
        MonoLabel(text = eyebrow, color = InovaTextTertiary)
        Spacer(modifier = Modifier.height(8.dp))
        Row(verticalAlignment = Alignment.Bottom) {
            Text(text = value, style = InovaType.brandDisplay, color = valueColor, maxLines = 1)
            if (unit != null) {
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = unit,
                    style = InovaType.screenTitleSmall,
                    color = InovaBlueLight,
                    modifier = Modifier.height(38.dp)
                )
            }
        }
        if (support != null) {
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = support, style = InovaType.bodySmall, color = InovaTextSecondary)
        }
    }
}

/**
 * Linha de comparação de magnitude: rótulo e valor na mesma linha, barra
 * abaixo. Uma única matiz para todas as barras — o comprimento já carrega
 * a informação, então a cor não precisa variar.
 */
@Composable
fun InovaBarRow(
    label: String,
    value: String,
    fraction: Float,
    modifier: Modifier = Modifier,
    barColor: Color = InovaBlueLight,
    thickness: Dp = BarThickness
) {
    val animated by animateFloatAsState(
        targetValue = fraction.coerceIn(0f, 1f),
        animationSpec = inovaTween(InovaDurationSlow),
        label = "barRow"
    )

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            MonoLabel(text = label, color = InovaTextTertiary, style = InovaType.monoTiny)
            Text(text = value, style = InovaType.cardLabel, color = InovaTextPrimary)
        }
        Spacer(modifier = Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(thickness)
                .clip(InovaShapes.progress)
                .background(InovaTrack)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(animated)
                    .height(thickness)
                    .clip(RoundedCornerShape(4.dp))
                    .background(barColor)
            )
        }
    }
}

/** Fatia de uma barra empilhada. */
data class StackSegment(val label: String, val value: Int, val color: Color)

/**
 * Barra empilhada horizontal para parte-do-todo. Substitui a rosca quando
 * há poucas fatias e os nomes são longos: compara melhor e ocupa menos tela.
 * Cada fatia é separada por um respiro, nunca por contorno.
 */
@Composable
fun InovaStackedBar(
    segments: List<StackSegment>,
    modifier: Modifier = Modifier,
    thickness: Dp = 14.dp
) {
    val total = segments.sumOf { it.value }.coerceAtLeast(1)
    val visible = segments.filter { it.value > 0 }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(thickness)
            .clip(InovaShapes.progress)
            .background(InovaTrack),
        horizontalArrangement = Arrangement.spacedBy(SegmentGap)
    ) {
        visible.forEachIndexed { index, segment ->
            Box(
                modifier = Modifier
                    .weight(segment.value.toFloat() / total)
                    .height(thickness)
                    .clip(
                        when {
                            visible.size == 1 -> InovaShapes.progress
                            index == 0 -> RoundedCornerShape(topStart = 7.dp, bottomStart = 7.dp)
                            index == visible.lastIndex ->
                                RoundedCornerShape(topEnd = 7.dp, bottomEnd = 7.dp)
                            else -> RoundedCornerShape(0.dp)
                        }
                    )
                    .background(segment.color)
            )
        }
    }
}

/**
 * Legenda de uma barra empilhada: marca de cor + rótulo + contagem.
 * É o rótulo direto que torna a leitura independente da cor.
 */
@Composable
fun InovaLegend(
    segments: List<StackSegment>,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        segments.forEach { segment ->
            Row(
                modifier = Modifier.fillMaxWidth().height(28.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(width = 10.dp, height = 3.dp)
                            .clip(InovaShapes.progress)
                            .background(segment.color)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = segment.label,
                        style = InovaType.bodySmall,
                        color = InovaTextSecondary
                    )
                }
                Text(
                    text = segment.value.toString(),
                    style = InovaType.cardLabel,
                    color = InovaTextPrimary
                )
            }
        }
    }
}

/** Etapa de um funil, na ordem em que deve ser lida. */
data class FunnelStage(val label: String, val value: Int)

/**
 * Funil: etapas ordenadas, barras proporcionais à maior delas.
 * Como as categorias têm ordem natural, a cor usa uma rampa de uma só matiz
 * (claro no topo, escuro na base) em vez de matizes distintas.
 */
@Composable
fun InovaFunnel(
    stages: List<FunnelStage>,
    modifier: Modifier = Modifier
) {
    val max = (stages.maxOfOrNull { it.value } ?: 0).coerceAtLeast(1)

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        stages.forEachIndexed { index, stage ->
            val step = if (stages.size == 1) 0f else index / (stages.size - 1f)
            InovaBarRow(
                label = stage.label,
                value = stage.value.toString(),
                fraction = stage.value / max.toFloat(),
                barColor = lerp(InovaBlueLight, InovaBlue, step)
            )
        }
    }
}
