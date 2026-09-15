package br.com.fiap.inovagab.ui.operador

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import br.com.fiap.inovagab.data.remote.dto.OperatorPerformanceDto
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
import br.com.fiap.inovagab.ui.theme.InovaBlueLight
import br.com.fiap.inovagab.ui.theme.InovaStatusDone
import br.com.fiap.inovagab.ui.theme.InovaStatusError
import br.com.fiap.inovagab.ui.theme.InovaStatusRunning
import br.com.fiap.inovagab.ui.theme.InovaTextPrimary
import br.com.fiap.inovagab.ui.theme.InovaTextSecondary
import br.com.fiap.inovagab.ui.theme.InovaTextTertiary
import br.com.fiap.inovagab.ui.theme.InovaType

// ─────────────────────────────────────────────────────────────────────────────
// Painéis do operador, montados sobre GET /api/dashboard/my-performance.
//
// A home dele não precisa do portfólio da empresa — precisa responder "como eu
// estou indo". Por isso a pontuação abre a tela e o resto é o caminho das
// próprias ideias.
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Pontuação e posição.
 *
 * A barra compara com o líder do ranking, não com um teto fixo: "360 pontos"
 * sozinho não diz se é muito ou pouco, e a distância para o primeiro é o que
 * dá sentido ao número.
 */
@Composable
fun PontuacaoPanel(data: OperatorPerformanceDto) {
    val lidera = data.position == 1
    val faltam = (data.leaderPoints - data.points).coerceAtLeast(0)

    InovaPanel {
        InovaHeroMetric(
            eyebrow = "Meus pontos",
            value = data.points.toString(),
            unit = "pts",
            valueColor = InovaStatusDone,
            support = when {
                data.position <= 0 -> "Cadastre uma ideia para entrar no ranking"
                lidera -> "Você lidera o ranking de ${data.totalOperators} inovadores"
                else -> "${data.position}º de ${data.totalOperators} · faltam $faltam pts para o 1º"
            }
        )

        if (data.leaderPoints > 0) {
            Spacer(modifier = Modifier.height(20.dp))
            InovaBarRow(
                label = if (lidera) "Você" else "Sua pontuação",
                value = data.points.toString(),
                fraction = data.points / data.leaderPoints.toFloat(),
                barColor = InovaStatusDone
            )
            if (!lidera) {
                Spacer(modifier = Modifier.height(14.dp))
                InovaBarRow(
                    label = "Líder",
                    value = data.leaderPoints.toString(),
                    fraction = 1f,
                    barColor = InovaBlueLight
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
        InovaDivider()
        Spacer(modifier = Modifier.height(14.dp))

        Row(modifier = Modifier.fillMaxWidth()) {
            MiniStat("Enviadas", data.totalIdeas.toString(), Modifier.weight(1f))
            MiniStat("Aprovadas", data.approved.toString(), Modifier.weight(1f))
            MiniStat("Projetos", data.convertedToProject.toString(), Modifier.weight(1f))
        }
    }
}

/** Situação das próprias ideias, por etapa. */
@Composable
fun MinhasIdeiasPanel(data: OperatorPerformanceDto) {
    SectionHeader(
        title = "Minhas Ideias",
        trailing = data.totalIdeas.toString().padStart(2, '0')
    )

    InovaPanel {
        if (data.totalIdeas == 0) {
            Text(
                text = "Você ainda não enviou nenhuma ideia. Comece por aí!",
                style = InovaType.bodySmall,
                color = InovaTextSecondary
            )
            return@InovaPanel
        }

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

/**
 * O caminho que uma ideia percorre até render os 100 pontos da conversão.
 * Deixa explícito o que falta para pontuar mais.
 */
@Composable
fun MinhaJornadaPanel(data: OperatorPerformanceDto) {
    if (data.totalIdeas == 0) return

    SectionHeader(title = "Minha Jornada")

    InovaPanel {
        InovaFunnel(
            stages = listOf(
                FunnelStage("Ideias enviadas", data.totalIdeas),
                FunnelStage("Aprovadas", data.approved),
                FunnelStage("Viraram projeto", data.convertedToProject)
            )
        )

        Spacer(modifier = Modifier.height(20.dp))
        InovaDivider()
        Spacer(modifier = Modifier.height(14.dp))

        // A regra de pontuação, à vista: sem isso o número do topo é mágico.
        PontosLinha("Cadastrar ideia", "+10 pts")
        Spacer(modifier = Modifier.height(10.dp))
        PontosLinha("Ideia aprovada", "+50 pts")
        Spacer(modifier = Modifier.height(10.dp))
        PontosLinha("Virou projeto", "+100 pts")
    }
}

@Composable
private fun PontosLinha(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween,
        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
    ) {
        MonoLabel(text = label, color = InovaTextTertiary, style = InovaType.monoTiny)
        Text(text = value, style = InovaType.cardLabel, color = InovaStatusDone)
    }
}

/** Número e rótulo empilhados. */
@Composable
private fun MiniStat(label: String, value: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(text = value, style = InovaType.metricSmall, color = InovaTextPrimary)
        Spacer(modifier = Modifier.height(3.dp))
        MonoLabel(text = label, color = InovaTextTertiary, style = InovaType.monoTiny)
    }
}
