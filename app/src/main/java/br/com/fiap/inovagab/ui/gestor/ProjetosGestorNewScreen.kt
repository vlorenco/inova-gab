package br.com.fiap.inovagab.ui.gestor

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.fiap.inovagab.data.model.Project
import br.com.fiap.inovagab.data.repository.ProjectRepository
import br.com.fiap.inovagab.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjetosGestorNewScreen(
    onBack: () -> Unit,
    onNovoProjeto: () -> Unit
) {
    val repository = remember { ProjectRepository() }
    var projects by remember { mutableStateOf<List<Project>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        repository.getProjects()
            .onSuccess { projects = it; isLoading = false }
            .onFailure { errorMsg = it.message; isLoading = false }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Projetos", color = Color.White, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar", tint = Color.White)
                    }
                },
                actions = {
                    IconButton(onClick = onNovoProjeto) {
                        Icon(Icons.Default.Add, contentDescription = "Novo Projeto", tint = Color.White)
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
                projects.isEmpty() -> Text("Nenhum projeto encontrado.", color = TextSecondary, modifier = Modifier.align(Alignment.Center))
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(top = 16.dp, bottom = 24.dp)
                    ) {
                        items(projects) { project -> ProjectCardGestor(project) }
                    }
                }
            }
        }
    }
}

@Composable
private fun ProjectCardGestor(project: Project) {
    val statusColor = when (project.status) {
        "PLANEJADO" -> PrimaryBlue
        "EM_ANDAMENTO" -> WarningYellow
        "CONCLUIDO" -> SuccessGreen
        "CANCELADO" -> DangerRed
        else -> TextSecondary
    }
    val statusLabel = when (project.status) {
        "EM_ANDAMENTO" -> "Em andamento"
        "CONCLUIDO" -> "Concluído"
        "PLANEJADO" -> "Planejado"
        "CANCELADO" -> "Cancelado"
        else -> project.status
    }

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
                Text(project.name, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimary, modifier = Modifier.weight(1f))
                Surface(shape = RoundedCornerShape(20.dp), color = statusColor.copy(alpha = 0.15f)) {
                    Text(statusLabel, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = statusColor, modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp))
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            if (project.currentStage.isNotBlank()) {
                Text("Etapa: ${project.currentStage}", fontSize = 12.sp, color = TextSecondary)
            }
            if (project.investment > 0) {
                Text("Investimento: R$ %,.2f".format(project.investment), fontSize = 12.sp, color = TextSecondary)
            }
            if (project.financialReturn > 0) {
                Text("Retorno: R$ %,.2f".format(project.financialReturn), fontSize = 12.sp, color = TextSecondary)
            }
        }
    }
}
