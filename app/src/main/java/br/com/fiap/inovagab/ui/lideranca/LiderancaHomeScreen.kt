package br.com.fiap.inovagab.ui.lideranca

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountTree
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.Flag
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import br.com.fiap.inovagab.data.model.DashboardSummary
import br.com.fiap.inovagab.data.repository.DashboardRepository
import br.com.fiap.inovagab.ui.components.InovaBottomNav
import br.com.fiap.inovagab.ui.components.InovaErrorState
import br.com.fiap.inovagab.ui.components.InovaHeader
import br.com.fiap.inovagab.ui.components.InovaLoading
import br.com.fiap.inovagab.ui.components.InovaNavItem
import br.com.fiap.inovagab.ui.components.InovaScreen
import br.com.fiap.inovagab.ui.components.InovaWideActionCard

/**
 * Home da liderança.
 *
 * A home **é** o dashboard. Antes havia uma tela separada e um card para
 * chegar até ela, o que dava dois caminhos para o mesmo conteúdo. Estratégias
 * e Projetos já são abas da barra inferior, então o único card que sobra é
 * Indicadores, que abre o recorte por orientação — informação que não está
 * em lugar nenhum.
 */
@Composable
fun LiderancaHomeScreen(
    onProfileClick: () -> Unit = {},
    onStrategiesClick: () -> Unit = {},
    onProjectsClick: () -> Unit = {},
    onIndicatorsClick: () -> Unit = {}
) {
    var selectedTab by remember { mutableIntStateOf(0) }

    val dashboardRepository = remember { DashboardRepository() }
    var summary by remember { mutableStateOf<DashboardSummary?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        dashboardRepository.getSummary()
            .onSuccess { summary = it }
            .onFailure { errorMsg = it.message }
        isLoading = false
    }

    InovaScreen(
        header = {
            InovaHeader(
                title = "Olá, Liderança",
                subtitle = "Visão Estratégica"
            )
        },
        bottomBar = {
            InovaBottomNav(
                selectedIndex = selectedTab,
                items = listOf(
                    InovaNavItem("Início", Icons.Outlined.Home) { selectedTab = 0 },
                    InovaNavItem("Estratégia", Icons.Outlined.Flag) {
                        selectedTab = 1; onStrategiesClick()
                    },
                    InovaNavItem("Projetos", Icons.Outlined.AccountTree) {
                        selectedTab = 2; onProjectsClick()
                    },
                    InovaNavItem("Perfil", Icons.Outlined.Person) {
                        selectedTab = 3; onProfileClick()
                    }
                )
            )
        }
    ) {
        when {
            isLoading -> Box(modifier = Modifier.fillMaxWidth().height(280.dp)) { InovaLoading() }

            summary == null -> Box(modifier = Modifier.fillMaxWidth().height(280.dp)) {
                InovaErrorState(errorMsg ?: "Não foi possível carregar os indicadores.")
            }

            else -> {
                val data = summary!!

                FinanceiroPanel(data)
                ProjetosPanel(data)
                FunilPanel(data)
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        InovaWideActionCard(
            icon = Icons.Outlined.BarChart,
            title = "Indicadores",
            subtitle = "ROI e resultados por orientação estratégica",
            primary = true,
            onClick = onIndicatorsClick
        )

        Spacer(modifier = Modifier.height(8.dp))
    }
}
