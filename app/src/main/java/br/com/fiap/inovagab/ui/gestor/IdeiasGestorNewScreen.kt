package br.com.fiap.inovagab.ui.gestor

import androidx.compose.foundation.clickable
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
import br.com.fiap.inovagab.data.model.Idea
import br.com.fiap.inovagab.data.repository.IdeaRepository
import br.com.fiap.inovagab.ui.theme.*

/** Filtro visual -> valor de status enviado ao backend. */
private val FILTERS = listOf(
    "Todas" to null,
    "Em análise" to "EM_ANALISE",
    "Priorizadas" to "PRIORIZADA",
    "Aprovadas" to "APROVADA",
    "Rejeitadas" to "REJEITADA"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IdeiasGestorNewScreen(
    onBack: () -> Unit,
    onIdeiaClick: (String) -> Unit
) {
    val repository = remember { IdeaRepository() }
    var ideas by remember { mutableStateOf<List<Idea>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMsg by remember { mutableStateOf<String?>(null) }
    var selectedFilter by remember { mutableStateOf("Todas") }

    // A filtragem acontece no backend (GET /api/ideas?status=...).
    LaunchedEffect(selectedFilter) {
        isLoading = true
        val status = FILTERS.firstOrNull { it.first == selectedFilter }?.second
        repository.getAllIdeas(status)
            .onSuccess { ideas = it; errorMsg = null }
            .onFailure { errorMsg = it.message }
        isLoading = false
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Avaliar Ideias", color = Color.White, fontWeight = FontWeight.Bold) },
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
        Column(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            ScrollableTabRow(
                selectedTabIndex = FILTERS.indexOfFirst { it.first == selectedFilter }.coerceAtLeast(0),
                containerColor = CardWhite,
                contentColor = PrimaryBlue,
                edgePadding = 16.dp
            ) {
                FILTERS.forEach { (label, _) ->
                    Tab(
                        selected = selectedFilter == label,
                        onClick = { selectedFilter = label },
                        text = { Text(label, fontSize = 13.sp, fontWeight = FontWeight.SemiBold) }
                    )
                }
            }

            Box(modifier = Modifier.fillMaxSize()) {
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
                    ideas.isEmpty() -> Text(
                        "Nenhuma ideia encontrada.",
                        color = TextSecondary,
                        modifier = Modifier.align(Alignment.Center)
                    )
                    else -> LazyColumn(
                        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(top = 16.dp, bottom = 24.dp)
                    ) {
                        items(ideas) { idea ->
                            GestorIdeaCard(idea = idea, onClick = { onIdeiaClick(idea.id) })
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun GestorIdeaCard(idea: Idea, onClick: () -> Unit) {
    val statusColor = ideaStatusColor(idea.status)
    val statusLabel = ideaStatusLabel(idea.status)

    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
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
            Text("Operador: ${idea.operatorName}", fontSize = 12.sp, color = TextSecondary)
            if (idea.area.isNotBlank()) {
                Text("Área: ${idea.area}", fontSize = 12.sp, color = TextSecondary)
            }
            if (idea.strategyTitle.isNotBlank()) {
                Text("Estratégia: ${idea.strategyTitle}", fontSize = 12.sp, color = AccentBlue)
            }

            Spacer(modifier = Modifier.height(6.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (idea.priority != "NORMAL") {
                    Text(
                        "Prioridade: ${idea.priority}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (idea.priority == "ALTA") DangerRed else TextSecondary
                    )
                }
                idea.aiAnalysis?.let { analysis ->
                    Surface(shape = RoundedCornerShape(20.dp), color = scoreColor(analysis.score).copy(alpha = 0.15f)) {
                        Text(
                            "IA ${analysis.score}/100",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = scoreColor(analysis.score),
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }
    }
}

internal fun ideaStatusColor(status: String): Color = when (status) {
    "EM_ANALISE" -> WarningYellow
    "PRIORIZADA" -> AccentBlue
    "APROVADA" -> SuccessGreen
    "REJEITADA" -> DangerRed
    else -> TextSecondary
}

internal fun ideaStatusLabel(status: String): String = when (status) {
    "EM_ANALISE" -> "Em análise"
    "PRIORIZADA" -> "Priorizada"
    "APROVADA" -> "Aprovada"
    "REJEITADA" -> "Rejeitada"
    else -> status
}

internal fun scoreColor(score: Int): Color = when {
    score >= 75 -> SuccessGreen
    score >= 50 -> WarningYellow
    else -> DangerRed
}
