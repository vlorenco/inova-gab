package br.com.fiap.inovagab.ui.gestor

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.fiap.inovagab.ui.components.AppActionCard
import br.com.fiap.inovagab.ui.components.AppTopBar
import br.com.fiap.inovagab.ui.theme.*

@Composable
fun GestorHomeScreen(
    onProfileClick: () -> Unit = {},
    onIdeiasClick: () -> Unit = {},
    onProjetosClick: () -> Unit = {}
) {
    var selectedTab by remember { mutableIntStateOf(0) }

    Scaffold(
        bottomBar = {
            NavigationBar(containerColor = CardWhite, tonalElevation = 4.dp) {
                listOf(
                    Pair("Início", Icons.Default.Home),
                    Pair("Ideias", Icons.Default.Lightbulb),
                    Pair("Projetos", Icons.Default.AccountTree),
                    Pair("Perfil", Icons.Default.Person)
                ).forEachIndexed { index, (label, icon) ->
                    NavigationBarItem(
                        selected = selectedTab == index,
                        onClick = {
                            selectedTab = index
                            when (index) {
                                1 -> onIdeiasClick()
                                2 -> onProjetosClick()
                                3 -> onProfileClick()
                            }
                        },
                        icon = { Icon(icon, contentDescription = label) },
                        label = { Text(label, fontSize = 11.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = PrimaryBlue,
                            selectedTextColor = PrimaryBlue,
                            indicatorColor = LightBackground
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(LightBackground)
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            AppTopBar(title = "Olá, Gestor", subtitle = "Painel de Curadoria")

            Spacer(modifier = Modifier.height(20.dp))

            // Card principal — Ideias pendentes
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CardWhite),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
            ) {
                Row(
                    modifier = Modifier.padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Pending,
                        contentDescription = null,
                        tint = WarningYellow,
                        modifier = Modifier.size(36.dp)
                    )
                    Column {
                        Text(
                            text = "Ideias pendentes",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "Avalie as ideias dos operadores",
                            fontSize = 13.sp,
                            color = TextSecondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Ações rápidas",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                modifier = Modifier.padding(horizontal = 20.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Column(
                modifier = Modifier.padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    AppActionCard(
                        icon = Icons.Default.RateReview,
                        title = "Avaliar ideias",
                        subtitle = "Ideias pendentes",
                        modifier = Modifier.weight(1f),
                        onClick = onIdeiasClick
                    )
                    AppActionCard(
                        icon = Icons.Default.AccountTree,
                        title = "Projetos",
                        subtitle = "Gerenciar projetos",
                        modifier = Modifier.weight(1f),
                        onClick = onProjetosClick
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    AppActionCard(
                        icon = Icons.Default.BarChart,
                        title = "Resultados",
                        subtitle = "Métricas do time",
                        modifier = Modifier.weight(1f)
                    )
                    AppActionCard(
                        icon = Icons.Default.Description,
                        title = "Relatórios",
                        subtitle = "Exportar dados",
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
