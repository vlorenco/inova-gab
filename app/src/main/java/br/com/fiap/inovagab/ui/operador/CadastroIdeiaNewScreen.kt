package br.com.fiap.inovagab.ui.operador

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.fiap.inovagab.data.model.Idea
import br.com.fiap.inovagab.data.repository.IdeaRepository
import br.com.fiap.inovagab.ui.theme.*
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CadastroIdeiaNewScreen(onBack: () -> Unit) {
    var titulo by remember { mutableStateOf("") }
    var problema by remember { mutableStateOf("") }
    var solucao by remember { mutableStateOf("") }
    var area by remember { mutableStateOf("") }
    var beneficio by remember { mutableStateOf("") }

    var isLoading by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf<String?>(null) }
    var isError by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()
    val repository = remember { IdeaRepository() }
    val currentUser = FirebaseAuth.getInstance().currentUser

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nova Ideia", color = Color.White, fontWeight = FontWeight.Bold) },
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            FormField("Título da ideia", titulo, { titulo = it })
            FormField("Problema encontrado", problema, { problema = it }, minLines = 3)
            FormField("Solução sugerida", solucao, { solucao = it }, minLines = 3)
            FormField("Área impactada", area, { area = it })
            FormField("Benefício esperado", beneficio, { beneficio = it }, minLines = 2)

            if (message != null) {
                Text(
                    text = message!!,
                    fontSize = 13.sp,
                    color = if (isError) DangerRed else SuccessGreen,
                    fontWeight = FontWeight.Medium
                )
            }

            Button(
                onClick = {
                    if (titulo.isBlank() || problema.isBlank() || solucao.isBlank()) {
                        message = "Preencha título, problema e solução."
                        isError = true
                        return@Button
                    }
                    coroutineScope.launch {
                        isLoading = true
                        message = null
                        val idea = Idea(
                            title = titulo.trim(),
                            problem = problema.trim(),
                            solution = solucao.trim(),
                            area = area.trim(),
                            benefit = beneficio.trim(),
                            status = "EM_ANALISE",
                            priority = "NORMAL",
                            operatorId = currentUser?.uid ?: "",
                            operatorName = currentUser?.email?.substringBefore("@") ?: "Operador"
                        )
                        repository.createIdea(idea)
                            .onSuccess {
                                message = "Ideia cadastrada com sucesso!"
                                isError = false
                                titulo = ""; problema = ""; solucao = ""; area = ""; beneficio = ""
                            }
                            .onFailure {
                                message = "Erro ao salvar: ${it.message}"
                                isError = true
                            }
                        isLoading = false
                    }
                },
                enabled = !isLoading,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(22.dp), color = Color.White, strokeWidth = 2.5.dp)
                } else {
                    Text("Salvar Ideia", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun FormField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    minLines: Int = 1
) {
    Column {
        Text(label, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            minLines = minLines,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PrimaryBlue,
                unfocusedBorderColor = Color(0xFFE2E8F0)
            )
        )
    }
}
