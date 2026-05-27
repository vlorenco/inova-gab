package br.com.fiap.inovagab.ui.gestor

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalheIdeiaScreen(
    ideaId: String,
    onBack: () -> Unit,
    onCriarProjeto: (String) -> Unit
) {
    val repository = remember { IdeaRepository() }
    var idea by remember { mutableStateOf<Idea?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var actionLoading by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(ideaId) {
        try {
            val doc = FirebaseFirestore.getInstance().collection("ideas").document(ideaId).get().await()
            idea = doc.toObject(Idea::class.java)?.copy(id = doc.id)
        } catch (e: Exception) {
            message = "Erro ao carregar ideia."
        }
        isLoading = false
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalhe da Ideia", color = Color.White, fontWeight = FontWeight.Bold) },
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
                idea == null -> Text(message ?: "Ideia não encontrada.", color = DangerRed, modifier = Modifier.align(Alignment.Center))
                else -> {
                    val currentIdea = idea!!
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Status badge
                        val statusColor = when (currentIdea.status) {
                            "EM_ANALISE" -> WarningYellow
                            "PRIORIZADA" -> AccentBlue
                            "APROVADA" -> SuccessGreen
                            "REJEITADA" -> DangerRed
                            else -> TextSecondary
                        }
                        val statusLabel = when (currentIdea.status) {
                            "EM_ANALISE" -> "Em análise"
                            "PRIORIZADA" -> "Priorizada"
                            "APROVADA" -> "Aprovada"
                            "REJEITADA" -> "Rejeitada"
                            else -> currentIdea.status
                        }

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = CardWhite),
                            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                        ) {
                            Column(modifier = Modifier.padding(20.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(currentIdea.title, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimary, modifier = Modifier.weight(1f))
                                    Surface(shape = RoundedCornerShape(20.dp), color = statusColor.copy(alpha = 0.15f)) {
                                        Text(statusLabel, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = statusColor, modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp))
                                    }
                                }

                                Spacer(modifier = Modifier.height(16.dp))
                                HorizontalDivider(color = Color(0xFFF1F5F9))
                                Spacer(modifier = Modifier.height(16.dp))

                                InfoRow("Operador", currentIdea.operatorName)
                                InfoRow("Área", currentIdea.area)
                                InfoRow("Prioridade", currentIdea.priority)
                                InfoRow("Problema", currentIdea.problem)
                                InfoRow("Solução", currentIdea.solution)
                                InfoRow("Benefício", currentIdea.benefit)
                            }
                        }

                        if (message != null) {
                            Text(message!!, fontSize = 13.sp, color = SuccessGreen, fontWeight = FontWeight.Medium)
                        }

                        // Action buttons
                        if (currentIdea.status != "REJEITADA") {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                OutlinedButton(
                                    onClick = {
                                        coroutineScope.launch {
                                            actionLoading = true
                                            repository.updateIdeaStatus(ideaId, "REJEITADA")
                                                .onSuccess { idea = currentIdea.copy(status = "REJEITADA"); message = "Ideia rejeitada." }
                                            actionLoading = false
                                        }
                                    },
                                    enabled = !actionLoading,
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = DangerRed),
                                    border = androidx.compose.foundation.BorderStroke(1.5.dp, DangerRed)
                                ) {
                                    Text("Reprovar", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                                }

                                if (currentIdea.status == "EM_ANALISE") {
                                    Button(
                                        onClick = {
                                            coroutineScope.launch {
                                                actionLoading = true
                                                repository.updateIdeaStatus(ideaId, "PRIORIZADA")
                                                repository.updateIdeaPriority(ideaId, "ALTA")
                                                    .onSuccess { idea = currentIdea.copy(status = "PRIORIZADA", priority = "ALTA"); message = "Ideia priorizada." }
                                                actionLoading = false
                                            }
                                        },
                                        enabled = !actionLoading,
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(12.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = AccentBlue)
                                    ) {
                                        Text("Priorizar", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                                    }
                                }
                            }

                            if (currentIdea.status != "APROVADA") {
                                Button(
                                    onClick = {
                                        coroutineScope.launch {
                                            actionLoading = true
                                            repository.updateIdeaStatus(ideaId, "APROVADA")
                                                .onSuccess { idea = currentIdea.copy(status = "APROVADA"); message = "Ideia aprovada!" }
                                            actionLoading = false
                                        }
                                    },
                                    enabled = !actionLoading,
                                    modifier = Modifier.fillMaxWidth().height(48.dp),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen)
                                ) {
                                    Text("Aprovar", fontWeight = FontWeight.SemiBold, color = Color.White)
                                }
                            }

                            if (currentIdea.status == "APROVADA" && !currentIdea.convertedToProject) {
                                Button(
                                    onClick = { onCriarProjeto(ideaId) },
                                    modifier = Modifier.fillMaxWidth().height(48.dp),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                                ) {
                                    Text("Criar Projeto", fontWeight = FontWeight.SemiBold)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    if (value.isBlank()) return
    Column(modifier = Modifier.padding(bottom = 12.dp)) {
        Text(label, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = TextSecondary)
        Text(value, fontSize = 14.sp, color = TextPrimary)
    }
}
