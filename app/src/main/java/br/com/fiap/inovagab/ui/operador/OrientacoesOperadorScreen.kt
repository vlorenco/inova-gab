package br.com.fiap.inovagab.ui.operador

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import br.com.fiap.inovagab.data.model.Strategy
import br.com.fiap.inovagab.data.repository.StrategyRepository
import br.com.fiap.inovagab.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrientacoesOperadorScreen(onBack: () -> Unit) {
    val repository = remember { StrategyRepository() }
    var strategies by remember { mutableStateOf<List<Strategy>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        repository.getStrategies()
            .onSuccess { strategies = it.filter { s -> s.isActive }; isLoading = false }
            .onFailure { errorMsg = it.message; isLoading = false }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Orientações Estratégicas", color = Color.White, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkBlue)
            )
        },
        containerColor = LightBackground
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            when {
                isLoading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = PrimaryBlue)
                errorMsg != null -> Text(errorMsg!!, color = DangerRed, modifier = Modifier.align(Alignment.Center).padding(32.dp))
                strategies.isEmpty() -> Text("Nenhuma orientação estratégica disponível.", color = TextSecondary, modifier = Modifier.align(Alignment.Center))
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(top = 16.dp, bottom = 24.dp)
                    ) {
                        items(strategies) { strategy ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = CardWhite),
                                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(strategy.title, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                    if (strategy.description.isNotBlank()) {
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(strategy.description, fontSize = 13.sp, color = TextSecondary)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
