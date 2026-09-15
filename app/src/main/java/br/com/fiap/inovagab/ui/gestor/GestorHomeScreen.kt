package br.com.fiap.inovagab.ui.gestor

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountTree
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Lightbulb
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
import br.com.fiap.inovagab.data.remote.dto.CurationSummaryDto
import br.com.fiap.inovagab.data.repository.DashboardRepository
import br.com.fiap.inovagab.ui.components.InovaBottomNav
import br.com.fiap.inovagab.ui.components.InovaErrorState
import br.com.fiap.inovagab.ui.components.InovaHeader
import br.com.fiap.inovagab.ui.components.InovaLoading
import br.com.fiap.inovagab.ui.components.InovaNavItem
import br.com.fiap.inovagab.ui.components.InovaScreen
import br.com.fiap.inovagab.ui.components.InovaWideActionCard

/**
 * Home do gestor.
 *
 * É o próprio painel de curadoria, não um menu para chegar até ele. Avaliar
 * ideias e gerenciar projetos já são abas da barra inferior, então repeti-los
 * como cards seria dar dois caminhos para o mesmo lugar na mesma tela — sobra
 * só Relatórios, que não tem aba.
 */
@Composable
fun GestorHomeScreen(
    onProfileClick: () -> Unit = {},
    onIdeiasClick: () -> Unit = {},
    onProjetosClick: () -> Unit = {},
    onRelatoriosClick: () -> Unit = {}
) {
    var selectedTab by remember { mutableIntStateOf(0) }

    val repository = remember { DashboardRepository() }
    var data by remember { mutableStateOf<CurationSummaryDto?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        repository.getCuration()
            .onSuccess { data = it }
            .onFailure { errorMsg = it.message }
        isLoading = false
    }

    InovaScreen(
        header = {
            InovaHeader(
                title = "Olá, Gestor",
                subtitle = "Painel de Curadoria"
            )
        },
        bottomBar = {
            InovaBottomNav(
                selectedIndex = selectedTab,
                items = listOf(
                    InovaNavItem("Início", Icons.Outlined.Home) { selectedTab = 0 },
                    InovaNavItem("Ideias", Icons.Outlined.Lightbulb) {
                        selectedTab = 1; onIdeiasClick()
                    },
                    InovaNavItem("Projetos", Icons.Outlined.AccountTree) {
                        selectedTab = 2; onProjetosClick()
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

            data == null -> Box(modifier = Modifier.fillMaxWidth().height(280.dp)) {
                InovaErrorState(errorMsg ?: "Não foi possível carregar os resultados.")
            }

            else -> {
                val info = data!!

                AproveitamentoPanel(info)
                CuradoriaPanel(info)
                ProjetosGeradosPanel(info)
                AreasPanel(info)
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        InovaWideActionCard(
            icon = Icons.Outlined.Description,
            title = "Relatórios",
            subtitle = "Exportar ideias, projetos e ranking em CSV",
            primary = true,
            onClick = onRelatoriosClick
        )

        Spacer(modifier = Modifier.height(8.dp))
    }
}
