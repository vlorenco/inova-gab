package br.com.fiap.inovagab.ui.operador

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.fiap.inovagab.data.model.MyRanking
import br.com.fiap.inovagab.data.model.RankingEntry
import br.com.fiap.inovagab.data.repository.RankingRepository
import br.com.fiap.inovagab.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RankingScreen(onBack: () -> Unit) {
    val repository = remember { RankingRepository() }

    var ranking by remember { mutableStateOf<List<RankingEntry>>(emptyList()) }
    var myPosition by remember { mutableStateOf<MyRanking?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        repository.getRanking()
            .onSuccess { ranking = it }
            .onFailure { errorMsg = it.message }
        repository.getMyPosition().onSuccess { myPosition = it }
        isLoading = false
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Ranking de Inovadores", color = Color.White, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkBlue)
            )
        },
        containerColor = LightBackground
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            when {
                isLoading -> CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = PrimaryBlue
                )
                errorMsg != null -> Text(
                    errorMsg!!,
                    color = DangerRed,
                    modifier = Modifier.align(Alignment.Center).padding(32.dp)
                )
                else -> LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(vertical = 16.dp)
                ) {
                    myPosition?.let { me ->
                        item { MyPointsCard(me) }
                        item { Spacer(modifier = Modifier.height(4.dp)) }
                    }

                    if (ranking.isEmpty()) {
                        item {
                            Text(
                                "Nenhum operador pontuou ainda.",
                                color = TextSecondary,
                                modifier = Modifier.fillMaxWidth().padding(32.dp)
                            )
                        }
                    } else {
                        items(ranking) { entry ->
                            RankingItem(
                                posicao = entry.position,
                                nome = entry.name,
                                pontos = entry.points,
                                destaque = entry.position == myPosition?.position
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MyPointsCard(me: MyRanking) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = PrimaryBlue)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Meus pontos", color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    "${me.points} pts",
                    color = Color.White,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }
            if (me.position > 0) {
                Column(horizontalAlignment = Alignment.End) {
                    Text("Posição", color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "${me.position}º de ${me.totalOperators}",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun RankingItem(posicao: Int, nome: String, pontos: Int, destaque: Boolean) {
    val medalColor = when (posicao) {
        1 -> Color(0xFFFFD700) // Ouro
        2 -> Color(0xFFC0C0C0) // Prata
        3 -> Color(0xFFCD7F32) // Bronze
        else -> Color(0xFFE0E0E0)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (destaque) AccentBlue.copy(alpha = 0.08f) else CardWhite
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(32.dp).background(medalColor, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "$posicao",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (posicao <= 3) Color.Black else Color.Gray
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(text = nome, fontSize = 16.sp, fontWeight = FontWeight.Medium, color = TextPrimary)
            }
            Text(
                text = "$pontos pts",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryBlue
            )
        }
    }
}
