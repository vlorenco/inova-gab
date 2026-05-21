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
import androidx.lifecycle.viewmodel.compose.viewModel
import br.com.fiap.inovagab.ui.components.AppActionCard
import br.com.fiap.inovagab.ui.components.AppTopBar
import br.com.fiap.inovagab.ui.operador.model.Ideia
import br.com.fiap.inovagab.ui.theme.*

@Composable
fun GestorHomeScreen(onProfileClick: () -> Unit = {}) {
    val viewModel: GestorViewModel = viewModel()

    var selectedTab by remember { mutableIntStateOf(0) }
    var ideiaSelecionada by remember { mutableStateOf<Ideia?>(null) }
    var mostrarCadastroProjeto by remember { mutableStateOf(false) }
    var projetosReloadKey by remember { mutableIntStateOf(0) }

    // Ao abrir tela de cadastro de projeto, aprovamos a ideia primeiro
    if (mostrarCadastroProjeto && ideiaSelecionada != null) {
        CadastroProjetoScreen(
            viewModel = viewModel,
            ideiaId = ideiaSelecionada!!.id,
            ideiaTitulo = ideiaSelecionada!!.titulo,
            onProjetoSalvo = {
                mostrarCadastroProjeto = false
                ideiaSelecionada = null
                selectedTab = 2
                projetosReloadKey++
            }
        )
        return
    }

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
                            if (index == 3) onProfileClick()
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
        when (selectedTab) {
            1 -> IdeiasGestorScreen(
                viewModel = viewModel,
                modifier = Modifier.padding(innerPadding),
                onAprovarECriarProjeto = { ideia ->
                    ideiaSelecionada = ideia
                    mostrarCadastroProjeto = true
                    viewModel.aprovarIdeiaParaProjeto(ideia.id)
                }
            )
            2 -> ProjetosGestorScreen(
                viewModel = viewModel,
                modifier = Modifier.padding(innerPadding),
                reloadKey = projetosReloadKey
            )
            else -> GestorHomeContent(innerPadding)
        }
    }
}

@Composable
private fun GestorHomeContent(innerPadding: PaddingValues) {
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
                        text = "12 ideias aguardando sua avaliação",
                        fontSize = 13.sp,
                        color = TextSecondary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Cards de resumo
        Text(
            text = "Resumo geral",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
            modifier = Modifier.padding(horizontal = 20.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            ResumoCard("58", "Recebidas", PrimaryBlue, Modifier.weight(1f))
            ResumoCard("12", "Em análise", WarningYellow, Modifier.weight(1f))
            ResumoCard("18", "Aprovadas", SuccessGreen, Modifier.weight(1f))
            ResumoCard("7", "Projetos", DarkBlue, Modifier.weight(1f))
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
                    subtitle = "12 pendentes",
                    modifier = Modifier.weight(1f)
                )
                AppActionCard(
                    icon = Icons.Default.AccountTree,
                    title = "Projetos",
                    subtitle = "7 ativos",
                    modifier = Modifier.weight(1f)
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

@Composable
private fun ResumoCard(valor: String, label: String, cor: Color, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = valor,
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                color = cor
            )
            Text(
                text = label,
                fontSize = 10.sp,
                color = TextSecondary
            )
        }
    }
}
