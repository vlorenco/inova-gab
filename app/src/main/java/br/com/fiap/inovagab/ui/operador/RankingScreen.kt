package br.com.fiap.inovagab.ui.operador

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import br.com.fiap.inovagab.data.model.Operador
import br.com.fiap.inovagab.data.repositoryfirestore.RankingService

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RankingScreen(onBack: () -> Unit) {
    val rankingService = remember { RankingService() }
    val coroutineScope = rememberCoroutineScope()
    
    var ranking by remember { mutableStateOf<List<Operador>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            ranking = rankingService.getRanking()
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Ranking de Operadores") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Text("←", fontSize = 20.sp)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF003399),
                    titleContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (ranking.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text("Nenhum operador cadastrado ainda.")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                itemsIndexed(ranking) { index, operador ->
                    RankingItem(
                        posicao = index + 1,
                        nome = operador.nome,
                        pontos = operador.pontos
                    )
                }
            }
        }
    }
}

@Composable
fun RankingItem(posicao: Int, nome: String, pontos: Int) {
    val medalColor = when (posicao) {
        1 -> Color(0xFFFFD700) // Ouro
        2 -> Color(0xFFC0C0C0) // Prata
        3 -> Color(0xFFCD7F32) // Bronze
        else -> Color(0xFFE0E0E0)
    }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Posição com medalha
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(medalColor, shape = androidx.compose.foundation.shape.CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "$posicao",
                        fontSize = 14.sp,
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                        color = if (posicao <= 3) Color.Black else Color.Gray
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = nome,
                    fontSize = 16.sp,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Medium
                )
            }
            Text(
                text = "$pontos pts",
                fontSize = 16.sp,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                color = Color(0xFF003399)
            )
        }
    }
}