package br.com.fiap.inovagab.ui.operador

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.fiap.inovagab.data.model.Idea
import br.com.fiap.inovagab.data.repository.IdeaRepository
import br.com.fiap.inovagab.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MinhasIdeiasNewScreen(onBack: () -> Unit) {
    val repository = remember { IdeaRepository() }
    val scope = rememberCoroutineScope()

    var ideas by remember { mutableStateOf<List<Idea>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMsg by remember { mutableStateOf<String?>(null) }
    var feedback by remember { mutableStateOf<String?>(null) }

    var editing by remember { mutableStateOf<Idea?>(null) }
    var deleting by remember { mutableStateOf<Idea?>(null) }

    suspend fun reload() {
        repository.getMyIdeas()
            .onSuccess { ideas = it; errorMsg = null }
            .onFailure { errorMsg = it.message }
        isLoading = false
    }

    LaunchedEffect(Unit) { reload() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Minhas Ideias", color = Color.White, fontWeight = FontWeight.Bold) },
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
                errorMsg != null -> Text(
                    errorMsg!!,
                    color = DangerRed,
                    modifier = Modifier.align(Alignment.Center).padding(32.dp)
                )
                ideas.isEmpty() -> Text(
                    "Você ainda não cadastrou nenhuma ideia.",
                    color = TextSecondary,
                    modifier = Modifier.align(Alignment.Center)
                )
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(top = 16.dp, bottom = 24.dp)
                    ) {
                        feedback?.let { text ->
                            item {
                                Text(text, fontSize = 13.sp, color = SuccessGreen, fontWeight = FontWeight.Medium)
                            }
                        }
                        items(ideas) { idea ->
                            IdeaCard(
                                idea = idea,
                                onEdit = { editing = idea },
                                onDelete = { deleting = idea }
                            )
                        }
                    }
                }
            }
        }
    }

    editing?.let { idea ->
        EditIdeaDialog(
            idea = idea,
            onDismiss = { editing = null },
            onConfirm = { title, problem, solution, area, benefit ->
                scope.launch {
                    repository.updateIdea(idea.id, title, problem, solution, area, benefit, idea.strategyId)
                        .onSuccess { feedback = "Ideia atualizada."; reload() }
                        .onFailure { errorMsg = it.message }
                    editing = null
                }
            }
        )
    }

    deleting?.let { idea ->
        AlertDialog(
            onDismissRequest = { deleting = null },
            title = { Text("Excluir ideia") },
            text = { Text("Deseja realmente excluir \"${idea.title}\"?") },
            confirmButton = {
                TextButton(onClick = {
                    scope.launch {
                        repository.deleteIdea(idea.id)
                            .onSuccess { feedback = "Ideia excluída."; reload() }
                            .onFailure { errorMsg = it.message }
                        deleting = null
                    }
                }) {
                    Text("Excluir", color = DangerRed)
                }
            },
            dismissButton = {
                TextButton(onClick = { deleting = null }) { Text("Cancelar") }
            }
        )
    }
}

@Composable
private fun IdeaCard(idea: Idea, onEdit: () -> Unit, onDelete: () -> Unit) {
    val statusColor = statusColorOf(idea.status)
    val statusLabel = statusLabelOf(idea.status)

    // Ideias já avaliadas pelo gestor ficam somente leitura, como no backend.
    val editable = idea.status == "EM_ANALISE" || idea.status == "PRIORIZADA"

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
                    idea.title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    modifier = Modifier.weight(1f)
                )
                Surface(shape = RoundedCornerShape(20.dp), color = statusColor.copy(alpha = 0.15f)) {
                    Text(
                        statusLabel,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = statusColor,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            if (idea.area.isNotBlank()) {
                Text("Área: ${idea.area}", fontSize = 12.sp, color = TextSecondary)
            }
            if (idea.benefit.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text("Benefício: ${idea.benefit}", fontSize = 12.sp, color = TextSecondary)
            }
            if (idea.strategyTitle.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text("Estratégia: ${idea.strategyTitle}", fontSize = 12.sp, color = AccentBlue)
            }
            if (idea.priority != "NORMAL") {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    "Prioridade: ${idea.priority}",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (idea.priority == "ALTA") DangerRed else TextSecondary
                )
            }
            if (idea.convertedToProject) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    "Convertida em projeto",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = SuccessGreen
                )
            }

            if (editable) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    TextButton(onClick = onEdit) {
                        Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Editar", fontSize = 12.sp)
                    }
                    TextButton(onClick = onDelete) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = null,
                            tint = DangerRed,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Excluir", fontSize = 12.sp, color = DangerRed)
                    }
                }
            }
        }
    }
}

@Composable
private fun EditIdeaDialog(
    idea: Idea,
    onDismiss: () -> Unit,
    onConfirm: (String, String, String, String, String) -> Unit
) {
    var title by remember { mutableStateOf(idea.title) }
    var problem by remember { mutableStateOf(idea.problem) }
    var solution by remember { mutableStateOf(idea.solution) }
    var area by remember { mutableStateOf(idea.area) }
    var benefit by remember { mutableStateOf(idea.benefit) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Editar ideia", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                DialogField("Título", title) { title = it }
                DialogField("Problema", problem, minLines = 2) { problem = it }
                DialogField("Solução", solution, minLines = 2) { solution = it }
                DialogField("Área", area) { area = it }
                DialogField("Benefício", benefit) { benefit = it }
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirm(title, problem, solution, area, benefit) },
                enabled = title.isNotBlank() && problem.isNotBlank() && solution.isNotBlank()
            ) {
                Text("Salvar", color = PrimaryBlue, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar", color = TextSecondary) }
        }
    )
}

@Composable
private fun DialogField(label: String, value: String, minLines: Int = 1, onValueChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, fontSize = 12.sp) },
        minLines = minLines,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = PrimaryBlue,
            unfocusedBorderColor = Color(0xFFE2E8F0)
        )
    )
}

internal fun statusColorOf(status: String): Color = when (status) {
    "EM_ANALISE" -> WarningYellow
    "PRIORIZADA" -> AccentBlue
    "APROVADA" -> SuccessGreen
    "REJEITADA" -> DangerRed
    else -> TextSecondary
}

internal fun statusLabelOf(status: String): String = when (status) {
    "EM_ANALISE" -> "Em análise"
    "PRIORIZADA" -> "Priorizada"
    "APROVADA" -> "Aprovada"
    "REJEITADA" -> "Rejeitada"
    else -> status
}
