package br.com.fiap.inovagab.ui.lideranca

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import br.com.fiap.inovagab.data.model.DashboardSummary
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
import br.com.fiap.inovagab.ui.components.formatCurrencyBr
import br.com.fiap.inovagab.ui.components.formatPercentBr
import br.com.fiap.inovagab.ui.components.formatPercentWholeBr
import br.com.fiap.inovagab.ui.theme.InovaBlue
import br.com.fiap.inovagab.ui.theme.InovaBlueLight
import br.com.fiap.inovagab.ui.theme.InovaStatusDone
import br.com.fiap.inovagab.ui.theme.InovaStatusError
import br.com.fiap.inovagab.ui.theme.InovaStatusRunning
import br.com.fiap.inovagab.ui.theme.InovaTextPrimary
import br.com.fiap.inovagab.ui.theme.InovaTextSecondary
import br.com.fiap.inovagab.ui.theme.InovaTextTertiary
import br.com.fiap.inovagab.ui.theme.InovaType

// ─────────────────────────────────────────────────────────────────────────────
// Painéis do dashboard.
//
// A home da liderança é feita deles. Ficam em arquivo próprio porque uma tela
// que também define seus gráficos vira um arquivo que ninguém lê inteiro.
//
// Todos recebem DashboardSummary pronto: nenhuma conta acontece na tela.
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Abre com o ROI — é o número que a liderança procura primeiro — e mostra de
 * onde ele vem com duas barras na mesma escala.
 */
@Composable
fun FinanceiroPanel(data: DashboardSummary) {
    // Investimento e retorno são a mesma unidade, então dividem a escala:
    // a diferença entre as barras é o lucro, visível sem precisar de legenda.
    val escala = maxOf(data.totalInvestment, data.totalFinancialReturn).coerceAtLeast(1.0)

    InovaPanel {
        InovaHeroMetric(
            eyebrow = "ROI Geral",
            value = formatPercentWholeBr(data.roi),
            unit = "%",
            valueColor = if (data.roi >= 0) InovaStatusDone else InovaStatusError,
            support = "Lucro de ${formatCurrencyBr(data.profit)}"
        )

        Spacer(modifier = Modifier.height(22.dp))
        InovaDivider()
        Spacer(modifier = Modifier.height(20.dp))

        InovaBarRow(
            label = "Investimento",
            value = formatCurrencyBr(data.totalInvestment),
            fraction = (data.totalInvestment / escala).toFloat(),
            barColor = InovaBlue
        )

        Spacer(modifier = Modifier.height(18.dp))

        InovaBarRow(
            label = "Retorno",
            value = formatCurrencyBr(data.totalFinancialReturn),
            fraction = (data.totalFinancialReturn / escala).toFloat(),
            barColor = InovaBlueLight
        )

        Spacer(modifier = Modifier.height(20.dp))
        InovaDivider()
        Spacer(modifier = Modifier.height(16.dp))

        ValorLinha("Lucro", formatCurrencyBr(data.profit))
        Spacer(modifier = Modifier.height(12.dp))
        ValorLinha("Redução custos", formatCurrencyBr(data.totalCostReduction))
        Spacer(modifier = Modifier.height(12.dp))
        ValorLinha("Produtividade", formatPercentBr(data.averageProductivityGain))
    }
}

/** Parte-do-todo dos projetos: barra empilhada + legenda com as contagens. */
@Composable
fun ProjetosPanel(data: DashboardSummary) {
    SectionHeader(
        title = "Desempenho dos Projetos",
        trailing = data.totalProjects.toString().padStart(2, '0')
    )

    InovaPanel {
        if (data.totalProjects > 0) {
            val segmentos = listOf(
                StackSegment("Em andamento", data.activeProjects, InovaStatusRunning),
                StackSegment("Concluídos", data.completedProjects, InovaStatusDone),
                StackSegment("Planejados", data.plannedProjects, InovaBlueLight),
                StackSegment("Cancelados", data.cancelledProjects, InovaStatusError)
            )

            InovaStackedBar(segments = segmentos)

            Spacer(modifier = Modifier.height(18.dp))

            // A legenda é o que torna a leitura independente da cor.
            InovaLegend(segments = segmentos)
        } else {
            Text(
                text = "Nenhum projeto cadastrado.",
                style = InovaType.bodySmall,
                color = InovaTextSecondary
            )
        }
    }
}

/** Etapas ordenadas da inovação, da captação à aprovação. */
@Composable
fun FunilPanel(data: DashboardSummary) {
    SectionHeader(title = "Funil de Inovação")

    InovaPanel {
        InovaFunnel(
            stages = listOf(
                FunnelStage("Ideias cadastradas", data.totalIdeas),
                FunnelStage("Ideias em análise", data.ideasUnderAnalysis),
                FunnelStage("Ideias aprovadas", data.approvedIdeas)
            )
        )

        Spacer(modifier = Modifier.height(20.dp))
        InovaDivider()
        Spacer(modifier = Modifier.height(16.dp))

        ValorLinha("Orientações vigentes", data.activeStrategies.toString())
        Spacer(modifier = Modifier.height(12.dp))
        ValorLinha("Orientações totais", data.totalStrategies.toString())
    }
}

/** Rótulo em mono à esquerda, valor em tinta primária à direita. */
@Composable
fun ValorLinha(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        MonoLabel(text = label, color = InovaTextTertiary, style = InovaType.monoTiny)
        Text(text = value, style = InovaType.cardLabel, color = InovaTextPrimary)
    }
}
