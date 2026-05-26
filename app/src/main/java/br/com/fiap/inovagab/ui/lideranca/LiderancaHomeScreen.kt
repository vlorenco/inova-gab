package br.com.fiap.inovagab.ui.lideranca

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
fun LiderancaHomeScreen(
    onProfileClick: () -> Unit = {},
    onStrategiesClick: () -> Unit = {},
    onDashboardClick: () -> Unit = {},
    onProjectsClick: () -> Unit = {}
) {
    var selectedTab by remember { mutableIntStateOf(0) }

    Scaffold(
        bottomBar = {
            NavigationBar(containerColor = CardWhite, tonalElevation = 4.dp) {
                listOf(
                    Pair("Início", Icons.Default.Home),
                    Pair("Estratégia", Icons.Default.Flag),
                    Pair("Projetos", Icons.Default.AccountTree),
                    Pair("Perfil", Icons.Default.Person)
                ).forEachIndexed { index, (label, icon) ->
                    NavigationBarItem(
                        selected = selectedTab == index,
                        onClick = {
                            selectedTab = index
                            when (index) {
                                1 -> onStrategiesClick()
                                2 -> onProjectsClick()
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
            AppTopBar(title = "Olá, Liderança", subtitle = "Visão Estratégica")

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Resumo geral",
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
                    ResumoGrandeCard(
                        valor = "128",
                        label = "Ideias cadastradas",
                        cor = PrimaryBlue,
                        modifier = Modifier.weight(1f)
                    )
                    ResumoGrandeCard(
                        valor = "36",
                        label = "Ideias aprovadas",
                        cor = SuccessGreen,
                        modifier = Modifier.weight(1f)
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    ResumoGrandeCard(
                        valor = "8",
                        label = "Projetos ativos",
                        cor = WarningYellow,
                        modifier = Modifier.weight(1f)
                    )
                    ResumoGrandeCard(
                        valor = "12",
                        label = "Projetos concluídos",
                        cor = DarkBlue,
                        modifier = Modifier.weight(1f)
                    )
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
                        icon = Icons.Default.Dashboard,
                        title = "Dashboard",
                        subtitle = "Visão consolidada",
                        modifier = Modifier.weight(1f),
                        onClick = onDashboardClick
                    )
                    AppActionCard(
                        icon = Icons.Default.Flag,
                        title = "Estratégias",
                        subtitle = "Gerencie diretrizes",
                        modifier = Modifier.weight(1f),
                        onClick = onStrategiesClick
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    AppActionCard(
                        icon = Icons.Default.AccountTree,
                        title = "Projetos",
                        subtitle = "Acompanhe projetos",
                        modifier = Modifier.weight(1f),
                        onClick = onProjectsClick
                    )
                    AppActionCard(
                        icon = Icons.Default.BarChart,
                        title = "Indicadores",
                        subtitle = "KPIs e métricas",
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun ResumoGrandeCard(valor: String, label: String, cor: Color, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = valor,
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                color = cor
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = label,
                fontSize = 12.sp,
                color = TextSecondary
            )
        }
    }
}
