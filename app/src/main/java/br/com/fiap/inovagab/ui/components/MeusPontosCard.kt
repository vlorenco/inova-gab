package br.com.fiap.inovagab.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import br.com.fiap.inovagab.data.repositoryfirestore.RankingService

@Composable
fun MeusPontosCard(
    operadorId: String,
    modifier: Modifier = Modifier,
    onVerRanking: () -> Unit = {}
) {
    val rankingService = remember { RankingService() }
    val coroutineScope = rememberCoroutineScope()
    var pontos by remember { mutableStateOf(0) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(operadorId) {
        coroutineScope.launch {
            pontos = rankingService.getPontosOperador(operadorId)
            isLoading = false
        }
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF003399)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "🏆 Meus Pontos",
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.8f)
                )
                Spacer(modifier = Modifier.height(4.dp))
                if (isLoading) {
                    Text(
                        text = "...",
                        fontSize = 24.sp,
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                        color = Color.White
                    )
                } else {
                    Text(
                        text = "$pontos pts",
                        fontSize = 24.sp,
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
            
            Button(
                onClick = onVerRanking,
                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(20.dp)
            ) {
                Text("Ver Ranking", color = Color(0xFF003399))
            }
        }
    }
}