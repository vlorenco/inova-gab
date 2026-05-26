package br.com.fiap.inovagab.ui.lideranca

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import br.com.fiap.inovagab.data.model.Strategy
import br.com.fiap.inovagab.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StrategiesScreen(
    onBack: () -> Unit,
    viewModel: LiderancaViewModel = viewModel()
) {
    var showDeleteDialog by remember { mutableStateOf<Strategy?>(null) }

    LaunchedEffect(Unit) { viewModel.loadStrategies() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Orientações Estratégicas", color = Color.White, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar", tint = Color.White)
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.showForm = !viewModel.showForm }) {
                        Icon(Icons.Default.Add, contentDescription = "Adicionar", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkBlue)
            )
        },
        containerColor = LightBackground
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Abas
            TabRow(
                selectedTabIndex = listOf("Todas", "Ativas", "Inativas").indexOf(viewModel.selectedTab),
                containerColor = CardWhite,
                contentColor = PrimaryBlue
            ) {
                listOf("Todas", "Ativas", "Inativas").forEachIndexed { index, tab ->
                    Tab(
                        selected = viewModel.selectedTab == tab,
                        onClick = { viewModel.selectedTab = tab },
                        text = { Text(tab, fontSize = 13.sp, fontWeight = FontWeight.SemiBold) }
                    )
                }
            }

            // Formulário
            if (viewModel.showForm) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = CardWhite),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text("Nova orientação", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = TextPrimary)
                        OutlinedTextField(
                            value = viewModel.newStrategyTitle,
                            onValueChange = { viewModel.newStrategyTitle = it },
                            label = { Text("Título") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = PrimaryBlue,
                                unfocusedBorderColor = Color(0xFFE2E8F0)
                            )
                        )
                        OutlinedTextField(
                            value = viewModel.newStrategyDescription,
                            onValueChange = { viewModel.newStrategyDescription = it },
                            label = { Text("Descrição") },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 2,
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = PrimaryBlue,
                                unfocusedBorderColor = Color(0xFFE2E8F0)
                            )
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = { viewModel.showForm = false },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("Cancelar")
                            }
                            Button(
                                onClick = { viewModel.createStrategy() },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                            ) {
                                Text("Salvar")
                            }
                        }
                    }
                }
            }

            // Conteúdo
            Box(modifier = Modifier.fillMaxSize()) {
                when {
                    viewModel.isLoading -> {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center),
                            color = PrimaryBlue
                        )
                    }
                    viewModel.errorMessage != null -> {
                        Text(
                            text = viewModel.errorMessage!!,
                            color = DangerRed,
                            fontSize = 14.sp,
                            modifier = Modifier.align(Alignment.Center).padding(32.dp)
                        )
                    }
                    viewModel.getFilteredStrategies().isEmpty() -> {
                        Text(
                            text = "Nenhuma orientação estratégica encontrada.",
                            color = TextSecondary,
                            fontSize = 14.sp,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                    else -> {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            contentPadding = PaddingValues(top = 16.dp, bottom = 24.dp)
                        ) {
                            items(viewModel.getFilteredStrategies()) { strategy ->
                                StrategyCard(
                                    strategy = strategy,
                                    onDelete = { showDeleteDialog = strategy }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Diálogo de confirmação de exclusão
    showDeleteDialog?.let { strategy ->
        AlertDialog(
            onDismissRequest = { showDeleteDialog = null },
            title = { Text("Excluir orientação") },
            text = { Text("Deseja realmente excluir \"${strategy.title}\"?") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteStrategy(strategy.id)
                    showDeleteDialog = null
                }) {
                    Text("Excluir", color = DangerRed)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = null }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
private fun StrategyCard(strategy: Strategy, onDelete: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = strategy.title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    modifier = Modifier.weight(1f)
                )
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = if (strategy.isActive) SuccessGreen.copy(alpha = 0.15f) else TextSecondary.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = if (strategy.isActive) "Ativa" else "Inativa",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (strategy.isActive) SuccessGreen else TextSecondary,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            if (strategy.description.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(strategy.description, fontSize = 13.sp, color = TextSecondary)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (strategy.date.isNotBlank()) {
                    Text(strategy.date, fontSize = 11.sp, color = TextSecondary)
                }
                IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Default.Delete, contentDescription = "Excluir", tint = DangerRed, modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}
