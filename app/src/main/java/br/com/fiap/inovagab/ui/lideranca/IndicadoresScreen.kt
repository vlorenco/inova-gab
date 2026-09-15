package br.com.fiap.inovagab.ui.lideranca

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import br.com.fiap.inovagab.data.remote.dto.StrategyDashboardDto
import br.com.fiap.inovagab.data.repository.DashboardRepository
import br.com.fiap.inovagab.data.repository.StrategyRepository
import br.com.fiap.inovagab.ui.components.InovaBarRow
import br.com.fiap.inovagab.ui.components.InovaDivider
import br.com.fiap.inovagab.ui.components.InovaEmptyState
import br.com.fiap.inovagab.ui.components.InovaErrorState
import br.com.fiap.inovagab.ui.components.InovaLoading
import br.com.fiap.inovagab.ui.components.InovaPanel
import br.com.fiap.inovagab.ui.components.InovaScreen
import br.com.fiap.inovagab.ui.components.InovaTag
import br.com.fiap.inovagab.ui.components.InovaTopBar
import br.com.fiap.inovagab.ui.components.MonoLabel
import br.com.fiap.inovagab.ui.components.SectionHeader
import br.com.fiap.inovagab.ui.components.formatCurrencyBr
import br.com.fiap.inovagab.ui.components.formatPercentWholeBr
import br.com.fiap.inovagab.ui.theme.InovaBlue
import br.com.fiap.inovagab.ui.theme.InovaBlueLight
import br.com.fiap.inovagab.ui.theme.InovaStatusDone
import br.com.fiap.inovagab.ui.theme.InovaStatusError
import br.com.fiap.inovagab.ui.theme.InovaTextPrimary
import br.com.fiap.inovagab.ui.theme.InovaTextSecondary
import br.com.fiap.inovagab.ui.theme.InovaTextTertiary
import br.com.fiap.inovagab.ui.theme.InovaType

/**
 * Indicadores por orientação estratégica.
 *
 * O dashboard responde "como vai o portfólio inteiro". Esta tela responde a
 * pergunta seguinte, que é a que decide onde investir: qual orientação está
 * devolvendo resultado e qual não saiu do papel.
 *
 * Um GET /api/dashboard/strategies/{id} por orientação — o backend já entrega
 * cada recorte calculado, inclusive o ROI.
 */
@Composable
fun IndicadoresScreen(onBack: () -> Unit) {
    val strategyRepository = remember { StrategyRepository() }
    val dashboardRepository = remember { DashboardRepository() }

    var indicadores by remember { mutableStateOf<List<StrategyDashboardDto>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        strategyRepository.getStrategies()
            .onSuccess { strategies ->
                // Sequencial de propósito: são poucas orientações e assim uma
                // falha isolada não derruba a tela inteira.
                indicadores = strategies.mapNotNull { strategy ->
                    dashboardRepository.getByStrategy(strategy.id).getOrNull()
                }
            }
            .onFailure { errorMsg = it.message }
        isLoading = false
    }

    InovaScreen(
        header = { InovaTopBar(title = "Indicadores", onBack = onBack) }
    ) {
        when {
            isLoading -> Box(modifier = Modifier.fillMaxWidth().height(280.dp)) { InovaLoading() }

            errorMsg != null -> Box(modifier = Modifier.fillMaxWidth().height(280.dp)) {
                InovaErrorState(errorMsg!!)
            }

            indicadores.isEmpty() -> Box(modifier = Modifier.fillMaxWidth().height(280.dp)) {
                InovaEmptyState("Nenhuma orientação estratégica cadastrada.")
            }

            else -> {
                SectionHeader(
                    title = "Por orientação",
                    trailing = indicadores.size.toString().padStart(2, '0')
                )

                // Escala comum a todos os cards: o retorno de uma orientação só
                // significa alguma coisa comparado ao das outras.
                val escala = indicadores
                    .maxOfOrNull { maxOf(it.totalInvestment, it.totalFinancialReturn) }
                    ?.coerceAtLeast(1.0) ?: 1.0

                indicadores.forEach { item ->
                    IndicadorCard(item, escala)
                }

                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun IndicadorCard(data: StrategyDashboardDto, escala: Double) {
    // Sem projeto não há capital aplicado, e um ROI de 0% ali significaria
    // "empatou" em vez de "ainda não começou".
    val semProjetos = data.totalProjects == 0

    InovaPanel {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top
        ) {
            Text(
                text = data.strategyTitle,
                style = InovaType.cardLabel,
                color = InovaTextPrimary,
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(12.dp))
            InovaTag(
                text = if (data.active) "vigente" else "encerrada",
                color = if (data.active) InovaStatusDone else InovaTextTertiary
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(verticalAlignment = Alignment.Bottom) {
            Text(
                text = if (semProjetos) "—" else formatPercentWholeBr(data.roi),
                style = InovaType.screenTitle,
                color = when {
                    semProjetos -> InovaTextTertiary
                    data.roi >= 0 -> InovaStatusDone
                    else -> InovaStatusError
                }
            )
            if (!semProjetos) {
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = "%", style = InovaType.cardLabel, color = InovaBlueLight)
            }
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = if (semProjetos) "sem projetos ainda" else "de ROI",
                style = InovaType.bodySmall,
                color = InovaTextSecondary
            )
        }

        if (!semProjetos) {
            Spacer(modifier = Modifier.height(16.dp))

            InovaBarRow(
                label = "Investimento",
                value = formatCurrencyBr(data.totalInvestment),
                fraction = (data.totalInvestment / escala).toFloat(),
                barColor = InovaBlue
            )
            Spacer(modifier = Modifier.height(14.dp))
            InovaBarRow(
                label = "Retorno",
                value = formatCurrencyBr(data.totalFinancialReturn),
                fraction = (data.totalFinancialReturn / escala).toFloat(),
                barColor = InovaBlueLight
            )
        }

        Spacer(modifier = Modifier.height(18.dp))
        InovaDivider()
        Spacer(modifier = Modifier.height(14.dp))

        Row(modifier = Modifier.fillMaxWidth()) {
            MiniStat("Ideias", data.totalIdeas.toString(), Modifier.weight(1f))
            MiniStat("Aprovadas", data.approvedIdeas.toString(), Modifier.weight(1f))
            MiniStat("Projetos", data.totalProjects.toString(), Modifier.weight(1f))
            MiniStat("Concluídos", data.completedProjects.toString(), Modifier.weight(1f))
        }
    }
}

/** Número e rótulo empilhados, para as contagens do rodapé do card. */
@Composable
private fun MiniStat(label: String, value: String, modifier: Modifier = Modifier) {
    androidx.compose.foundation.layout.Column(modifier = modifier) {
        Text(text = value, style = InovaType.metricSmall, color = InovaTextPrimary)
        Spacer(modifier = Modifier.height(3.dp))
        MonoLabel(text = label, color = InovaTextTertiary, style = InovaType.monoTiny)
    }
}
