package br.com.fiap.inovagab.ui.operador

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
fun OperadorHomeScreen(
    onProfileClick: () -> Unit = {},
    onCadastrarIdeiaClick: () -> Unit = {},
    onMinhasIdeiasClick: () -> Unit = {},
    onOrientacoesClick: () -> Unit = {},
    onRankingClick: () -> Unit = {}
) {
    var selectedTab by remember { mutableIntStateOf(0) }

    Scaffold(
        bottomBar = {
            NavigationBar(containerColor = CardWhite, tonalElevation = 4.dp) {
                listOf(
                    Pair("Início", Icons.Default.Home),
                    Pair("Ideias", Icons.Default.Lightbulb),
                    Pair("Estratégias", Icons.Default.Flag),
                    Pair("Perfil", Icons.Default.Person)
                ).forEachIndexed { index, (label, icon) ->
                    NavigationBarItem(
                        selected = selectedTab == index,
                        onClick = {
                            selectedTab = index
                            when (index) {
                                1 -> onMinhasIdeiasClick()
                                2 -> onOrientacoesClick()
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
            AppTopBar(title = "Olá, Operador", subtitle = "Vamos inovar hoje?")

            Spacer(modifier = Modifier.height(20.dp))

            // Card principal — Orientações estratégicas
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
                        imageVector = Icons.Default.Flag,
                        contentDescription = null,
                        tint = PrimaryBlue,
                        modifier = Modifier.size(36.dp)
                    )
                    Column {
                        Text(
                            text = "Orientações estratégicas",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "Confira as diretrizes disponíveis",
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
                        icon = Icons.Default.Add,
                        title = "Cadastrar nova ideia",
                        subtitle = "Submeta sua inovação",
                        modifier = Modifier.weight(1f),
                        onClick = onCadastrarIdeiaClick
                    )
                    AppActionCard(
                        icon = Icons.Default.Lightbulb,
                        title = "Minhas ideias",
                        subtitle = "Acompanhe o status",
                        modifier = Modifier.weight(1f),
                        onClick = onMinhasIdeiasClick
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    AppActionCard(
                        icon = Icons.Default.Flag,
                        title = "Orientações estratégicas",
                        subtitle = "Diretrizes do grupo",
                        modifier = Modifier.weight(1f),
                        onClick = onOrientacoesClick
                    )
                    AppActionCard(
                        icon = Icons.Default.EmojiEvents,
                        title = "Ranking de inovadores",
                        subtitle = "Veja sua posição",
                        modifier = Modifier.weight(1f),
                        onClick = onRankingClick
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Card motivacional
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = PrimaryBlue)
            ) {
                Row(
                    modifier = Modifier.padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                    Text(
                        text = "Sua ideia pode transformar o futuro da Águia Branca!",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
