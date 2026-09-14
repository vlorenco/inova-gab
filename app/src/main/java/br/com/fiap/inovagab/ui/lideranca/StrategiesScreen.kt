package br.com.fiap.inovagab.ui.lideranca

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.History
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
                    IconButton(onClick = { viewModel.startCreate() }) {
                        Icon(Icons.Default.Add, contentDescription = "Adicionar", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkBlue)
            )
        },
        containerColor = LightBackground
    ) { innerPadding ->
        Column(modifier = Modifier.fillMaxSize().padding(innerPadding)) {

            TabRow(
                selectedTabIndex = listOf("Todas", "Ativas", "Inativas").indexOf(viewModel.selectedTab),
                containerColor = CardWhite,
                contentColor = PrimaryBlue
            ) {
                listOf("Todas", "Ativas", "Inativas").forEach { tab ->
                    Tab(
                        selected = viewModel.selectedTab == tab,
                        onClick = { viewModel.selectedTab = tab },
                        text = { Text(tab, fontSize = 13.sp, fontWeight = FontWeight.SemiBold) }
                    )
                }
            }

            if (viewModel.showForm) {
                StrategyForm(viewModel)
            }

            Box(modifier = Modifier.fillMaxSize()) {
                when {
                    viewModel.isLoading && viewModel.strategies.isEmpty() -> CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = PrimaryBlue
                    )
                    viewModel.errorMessage != null && viewModel.strategies.isEmpty() -> Text(
                        text = viewModel.errorMessage!!,
                        color = DangerRed,
                        fontSize = 14.sp,
                        modifier = Modifier.align(Alignment.Center).padding(32.dp)
                    )
                    viewModel.getFilteredStrategies().isEmpty() -> Text(
                        text = "Nenhuma orientação estratégica encontrada.",
                        color = TextSecondary,
                        fontSize = 14.sp,
                        modifier = Modifier.align(Alignment.Center)
                    )
                    else -> LazyColumn(
                        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(top = 16.dp, bottom = 24.dp)
                    ) {
                        viewModel.errorMessage?.let { error ->
                            item { Text(error, color = DangerRed, fontSize = 13.sp) }
                        }
                        items(viewModel.getFilteredStrategies()) { strategy ->
                            StrategyCard(
                                strategy = strategy,
                                onEdit = { viewModel.startEdit(strategy) },
                                onHistory = { viewModel.openHistory(strategy) },
                                onDelete = { showDeleteDialog = strategy }
                            )
                        }
                    }
                }
            }
        }
    }

    showDeleteDialog?.let { strategy ->
        AlertDialog(
            onDismissRequest = { showDeleteDialog = null },
            title = { Text("Excluir orientação") },
            text = { Text("Deseja realmente excluir \"${strategy.title}\"? O histórico será preservado.") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteStrategy(strategy.id)
                    showDeleteDialog = null
                }) { Text("Excluir", color = DangerRed) }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = null }) { Text("Cancelar") }
            }
        )
    }

    viewModel.historyOf?.let { strategy ->
        HistoryDialog(viewModel = viewModel, strategy = strategy)
    }
}

@Composable
private fun StrategyForm(viewModel: LiderancaViewModel) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                if (viewModel.editingId == null) "Nova orientação" else "Editar orientação",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = TextPrimary
            )

            FormTextField("Título", viewModel.formTitle) { viewModel.formTitle = it }
            FormTextField("Descrição", viewModel.formDescription, minLines = 2) {
                viewModel.formDescription = it
            }
            FormTextField("Categoria", viewModel.formCategory) { viewModel.formCategory = it }
            FormTextField("Campanha", viewModel.formCampaign) { viewModel.formCampaign = it }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Switch(
                    checked = viewModel.formActive,
                    onCheckedChange = { viewModel.formActive = it },
                    colors = SwitchDefaults.colors(checkedThumbColor = PrimaryBlue)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    if (viewModel.formActive) "Orientação vigente" else "Orientação inativa",
                    fontSize = 13.sp,
                    color = TextSecondary
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = { viewModel.cancelForm() },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) { Text("Cancelar") }

                Button(
                    onClick = { viewModel.saveStrategy() },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                ) { Text("Salvar") }
            }
        }
    }
}

@Composable
private fun FormTextField(
    label: String,
    value: String,
    minLines: Int = 1,
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth(),
        minLines = minLines,
        singleLine = minLines == 1,
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = PrimaryBlue,
            unfocusedBorderColor = Color(0xFFE2E8F0)
        )
    )
}

@Composable
private fun StrategyCard(
    strategy: Strategy,
    onEdit: () -> Unit,
    onHistory: () -> Unit,
    onDelete: () -> Unit
) {
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
                    color = if (strategy.active) SuccessGreen.copy(alpha = 0.15f)
                    else TextSecondary.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = if (strategy.active) "Ativa" else "Inativa",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (strategy.active) SuccessGreen else TextSecondary,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            if (strategy.description.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(strategy.description, fontSize = 13.sp, color = TextSecondary)
            }

            if (strategy.category.isNotBlank() || strategy.campaign.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    listOf(strategy.category, strategy.campaign).filter { it.isNotBlank() }
                        .joinToString(" • "),
                    fontSize = 11.sp,
                    color = AccentBlue,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (strategy.date.isNotBlank()) {
                    Text(strategy.date, fontSize = 11.sp, color = TextSecondary)
                } else {
                    Spacer(modifier = Modifier.width(1.dp))
                }
                Row {
                    IconButton(onClick = onHistory, modifier = Modifier.size(36.dp)) {
                        Icon(
                            Icons.Default.History,
                            contentDescription = "Histórico",
                            tint = AccentBlue,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    IconButton(onClick = onEdit, modifier = Modifier.size(36.dp)) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Editar",
                            tint = PrimaryBlue,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    IconButton(onClick = onDelete, modifier = Modifier.size(36.dp)) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Excluir",
                            tint = DangerRed,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun HistoryDialog(viewModel: LiderancaViewModel, strategy: Strategy) {
    AlertDialog(
        onDismissRequest = { viewModel.closeHistory() },
        title = { Text("Histórico: ${strategy.title}", fontSize = 16.sp, fontWeight = FontWeight.Bold) },
        text = {
            when {
                viewModel.historyLoading -> Box(
                    modifier = Modifier.fillMaxWidth().padding(24.dp),
                    contentAlignment = Alignment.Center
                ) { CircularProgressIndicator(color = PrimaryBlue) }

                viewModel.history.isEmpty() -> Text("Nenhum registro de histórico.", fontSize = 13.sp)

                else -> Column(
                    modifier = Modifier.verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    viewModel.history.forEach { entry ->
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    entry.action,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PrimaryBlue
                                )
                                Text(
                                    entry.changedAt.take(19).replace("T", " "),
                                    fontSize = 11.sp,
                                    color = TextSecondary
                                )
                            }
                            Text(entry.title, fontSize = 13.sp, color = TextPrimary)
                            if (entry.description.isNotBlank()) {
                                Text(entry.description, fontSize = 11.sp, color = TextSecondary)
                            }
                            HorizontalDivider(
                                modifier = Modifier.padding(top = 6.dp),
                                color = Color(0xFFF1F5F9)
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { viewModel.closeHistory() }) { Text("Fechar") }
        }
    )
}
