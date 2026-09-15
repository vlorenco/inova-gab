package br.com.fiap.inovagab.ui.operador

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.EmojiEvents
import androidx.compose.material.icons.outlined.Flag
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Star
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import br.com.fiap.inovagab.data.remote.dto.OperatorPerformanceDto
import br.com.fiap.inovagab.data.repository.DashboardRepository
import br.com.fiap.inovagab.ui.components.IncentiveBanner
import br.com.fiap.inovagab.ui.components.InovaBottomNav
import br.com.fiap.inovagab.ui.components.InovaErrorState
import br.com.fiap.inovagab.ui.components.InovaHeader
import br.com.fiap.inovagab.ui.components.InovaLoading
import br.com.fiap.inovagab.ui.components.InovaNavItem
import br.com.fiap.inovagab.ui.components.InovaScreen
import br.com.fiap.inovagab.ui.components.InovaWideActionCard

/**
 * Home do operador.
 *
 * Responde "como eu estou indo" antes de oferecer qualquer botão. Minhas
 * ideias e Orientações já são abas da barra inferior — e Orientações ainda
 * aparecia duas vezes na tela antiga — então sobram só as duas ações que não
 * têm aba: cadastrar e ver o ranking.
 */
@Composable
fun OperadorHomeScreen(
    onProfileClick: () -> Unit = {},
    onCadastrarIdeiaClick: () -> Unit = {},
    onMinhasIdeiasClick: () -> Unit = {},
    onOrientacoesClick: () -> Unit = {},
    onRankingClick: () -> Unit = {}
) {
    var selectedTab by remember { mutableIntStateOf(0) }

    val repository = remember { DashboardRepository() }
    var data by remember { mutableStateOf<OperatorPerformanceDto?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        repository.getMyPerformance()
            .onSuccess { data = it }
            .onFailure { errorMsg = it.message }
        isLoading = false
    }

    InovaScreen(
        header = {
            InovaHeader(
                title = "Olá, Operador",
                subtitle = "Vamos inovar hoje?"
            )
        },
        bottomBar = {
            InovaBottomNav(
                selectedIndex = selectedTab,
                items = listOf(
                    InovaNavItem("Início", Icons.Outlined.Home) { selectedTab = 0 },
                    InovaNavItem("Ideias", Icons.Outlined.Lightbulb) {
                        selectedTab = 1; onMinhasIdeiasClick()
                    },
                    InovaNavItem("Estratégias", Icons.Outlined.Flag) {
                        selectedTab = 2; onOrientacoesClick()
                    },
                    InovaNavItem("Perfil", Icons.Outlined.Person) {
                        selectedTab = 3; onProfileClick()
                    }
                )
            )
        }
    ) {
        when {
            isLoading -> Box(modifier = Modifier.fillMaxWidth().height(260.dp)) { InovaLoading() }

            data == null -> Box(modifier = Modifier.fillMaxWidth().height(260.dp)) {
                InovaErrorState(errorMsg ?: "Não foi possível carregar seu desempenho.")
            }

            else -> {
                val info = data!!

                PontuacaoPanel(info)
                MinhasIdeiasPanel(info)
                MinhaJornadaPanel(info)
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        InovaWideActionCard(
            icon = Icons.Outlined.Add,
            title = "Cadastrar nova ideia",
            subtitle = "Submeta sua inovação e ganhe 10 pontos",
            primary = true,
            onClick = onCadastrarIdeiaClick
        )

        InovaWideActionCard(
            icon = Icons.Outlined.EmojiEvents,
            title = "Ranking de inovadores",
            subtitle = "Veja sua posição entre os colegas",
            primary = false,
            onClick = onRankingClick
        )

        IncentiveBanner(
            text = "Sua ideia pode transformar o futuro da Águia Branca!",
            icon = Icons.Outlined.Star
        )

        Spacer(modifier = Modifier.height(8.dp))
    }
}
