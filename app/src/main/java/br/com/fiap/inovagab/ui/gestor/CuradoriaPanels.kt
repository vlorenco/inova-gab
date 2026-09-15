package br.com.fiap.inovagab.ui.gestor

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import br.com.fiap.inovagab.data.remote.dto.CurationSummaryDto
import br.com.fiap.inovagab.ui.components.FunnelStage
import br.com.fiap.inovagab.ui.components.InovaBarRow
import br.com.fiap.inovagab.ui.components.InovaDivider
import br.com.fiap.inovagab.ui.components.InovaFunnel
import br.com.fiap.inovagab.ui.components.InovaHeroMetric
import br.com.fiap.inovagab.ui.components.InovaLegend
import br.com.fiap.inovagab.ui.components.InovaPanel
import br.com.fiap.inovagab.ui.components.InovaStackedBar
import br.com.fiap.inovagab.ui.components.MonoLabel
import br.com.fiap.inovagab.ui.components.SectionHeader
import br.com.fiap.inovagab.ui.components.StackSegment
import br.com.fiap.inovagab.ui.components.formatPercentWholeBr
import br.com.fiap.inovagab.ui.theme.InovaBlueLight
import br.com.fiap.inovagab.ui.theme.InovaStatusDone
import br.com.fiap.inovagab.ui.theme.InovaStatusError
import br.com.fiap.inovagab.ui.theme.InovaStatusRunning
import br.com.fiap.inovagab.ui.theme.InovaTextPrimary
import br.com.fiap.inovagab.ui.theme.InovaTextTertiary
import br.com.fiap.inovagab.ui.theme.InovaType

// ─────────────────────────────────────────────────────────────────────────────
// Painéis da curadoria, montados sobre GET /api/dashboard/curation.
//
// O dashboard da liderança fala de dinheiro; estes falam de trabalho. Ficam em
// arquivo próprio porque a home do gestor é feita deles — e uma tela de home
// que também define seus gráficos vira um arquivo que ninguém lê inteiro.
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Taxa de aproveitamento: das ideias que saíram da fila, quantas foram
 * aprovadas. Ideias ainda em análise ficam de fora — elas não foram decididas,
 * e contá-las como não-aprovadas faria o número cair sozinho a cada cadastro.
 */
@Composable
fun AproveitamentoPanel(data: CurationSummaryDto) {
    val decididas = data.approved + data.rejected
    val taxa = if (decididas == 0) 0.0 else data.approved * 100.0 / decididas

    InovaPanel {
        InovaHeroMetric(
            eyebrow = "Aproveitamento",
            value = if (decididas == 0) "—" else formatPercentWholeBr(taxa),
            unit = if (decididas == 0) null else "%",
            valueColor = if (decididas == 0) InovaTextTertiary else InovaStatusDone,
            support = if (decididas == 0) {
                "Nenhuma ideia decidida ainda"
            } else {
                "${data.approved} aprovadas de $decididas decididas"
            }
        )

        Spacer(modifier = Modifier.height(20.dp))
        InovaDivider()
        Spacer(modifier = Modifier.height(14.dp))

        Row(modifier = Modifier.fillMaxWidth()) {
            // Rótulos curtos: com quatro colunas, qualquer coisa acima de
            // ~12 caracteres é cortada com reticências.
            MiniStat("Na fila", data.underAnalysis.toString(), Modifier.weight(1f))
            MiniStat("Convertidas", data.convertedToProject.toString(), Modifier.weight(1f))
            MiniStat("Com IA", data.withAiAnalysis.toString(), Modifier.weight(1f))
            MiniStat("Operadores", data.contributingOperators.toString(), Modifier.weight(1f))
        }
    }
}

/** Etapas da curadoria, da entrada à decisão. */
@Composable
fun CuradoriaPanel(data: CurationSummaryDto) {
    SectionHeader(
        title = "Fila de Curadoria",
        trailing = data.totalIdeas.toString().padStart(2, '0')
    )

    InovaPanel {
        val segmentos = listOf(
            StackSegment("Em análise", data.underAnalysis, InovaStatusRunning),
            StackSegment("Priorizadas", data.prioritized, InovaBlueLight),
            StackSegment("Aprovadas", data.approved, InovaStatusDone),
            StackSegment("Rejeitadas", data.rejected, InovaStatusError)
        )

        InovaStackedBar(segments = segmentos)
        Spacer(modifier = Modifier.height(18.dp))
        InovaLegend(segments = segmentos)
    }
}

/** Quanto da curadoria virou execução de fato. */
@Composable
fun ProjetosGeradosPanel(data: CurationSummaryDto) {
    SectionHeader(
        title = "Projetos Gerados",
        trailing = data.totalProjects.toString().padStart(2, '0')
    )

    InovaPanel {
        InovaFunnel(
            stages = listOf(
                FunnelStage("Ideias aprovadas", data.approved),
                FunnelStage("Viraram projeto", data.convertedToProject),
                FunnelStage("Projetos concluídos", data.completedProjects)
            )
        )

        Spacer(modifier = Modifier.height(20.dp))
        InovaDivider()
        Spacer(modifier = Modifier.height(14.dp))

        Row(modifier = Modifier.fillMaxWidth()) {
            MiniStat("Planejados", data.plannedProjects.toString(), Modifier.weight(1f))
            MiniStat("Em andamento", data.activeProjects.toString(), Modifier.weight(1f))
            MiniStat("Concluídos", data.completedProjects.toString(), Modifier.weight(1f))
            MiniStat("Cancelados", data.cancelledProjects.toString(), Modifier.weight(1f))
        }
    }
}

/** De onde as ideias estão vindo — mostra qual área está engajada e qual não. */
@Composable
fun AreasPanel(data: CurationSummaryDto) {
    if (data.topAreas.isEmpty()) return

    SectionHeader(title = "Ideias por Área")

    InovaPanel {
        val maior = data.topAreas.maxOf { it.total }.coerceAtLeast(1)

        data.topAreas.forEachIndexed { index, area ->
            if (index > 0) Spacer(modifier = Modifier.height(14.dp))
            InovaBarRow(
                label = area.area,
                value = area.total.toString(),
                fraction = area.total / maior.toFloat()
            )
        }
    }
}

/** Número e rótulo empilhados, para as contagens de rodapé. */
@Composable
fun MiniStat(label: String, value: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(text = value, style = InovaType.metricSmall, color = InovaTextPrimary)
        Spacer(modifier = Modifier.height(3.dp))
        MonoLabel(text = label, color = InovaTextTertiary, style = InovaType.monoTiny)
    }
}
