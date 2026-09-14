package br.com.fiap.inovagab.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.fiap.inovagab.data.repository.DemoDataRepository
import br.com.fiap.inovagab.ui.theme.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@Composable
fun ProfileScreen(onLogout: () -> Unit) {
    val auth = FirebaseAuth.getInstance()
    val currentUser = auth.currentUser

    var name by remember { mutableStateOf("Usuário InovaGAB") }
    var role by remember { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(false) }

    // Demo data
    val coroutineScope = rememberCoroutineScope()
    var demoLoading by remember { mutableStateOf(false) }
    var demoMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(currentUser?.uid) {
        currentUser?.uid?.let { uid ->
            try {
                val doc = FirebaseFirestore.getInstance()
                    .collection("users").document(uid).get().await()
                name = doc.getString("name")?.ifBlank { null }
                    ?: currentUser.email?.substringBefore("@") ?: "Usuário InovaGAB"
                role = doc.getString("role") ?: ""
            } catch (_: Exception) {
                name = currentUser.email?.substringBefore("@") ?: "Usuário InovaGAB"
            }
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Sair da conta?", fontWeight = FontWeight.Bold) },
            text = { Text("Tem certeza que deseja sair da sua conta?") },
            confirmButton = {
                TextButton(onClick = {
                    auth.signOut()
                    onLogout()
                }) {
                    Text("Sair", color = DangerRed, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Cancelar", color = TextSecondary)
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LightBackground)
            .verticalScroll(rememberScrollState())
    ) {
        // Header azul
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp)
                .background(Brush.verticalGradient(listOf(DarkBlue, PrimaryBlue)))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .padding(bottom = 28.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Meu Perfil",
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 13.sp,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(16.dp))
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = name.take(1).uppercase(),
                        color = Color.White,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = name,
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                if (role.isNotBlank()) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = Color.White.copy(alpha = 0.2f)
                    ) {
                        Text(
                            text = role,
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Card informações
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = CardWhite),
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "Informações da conta",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextSecondary,
                    letterSpacing = 0.5.sp
                )
                Spacer(modifier = Modifier.height(16.dp))
                ProfileInfoRow(Icons.Default.Person, "Nome", name)
                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Color(0xFFF1F5F9))
                ProfileInfoRow(Icons.Default.Email, "E-mail", currentUser?.email ?: "—")
                if (role.isNotBlank()) {
                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Color(0xFFF1F5F9))
                    ProfileInfoRow(Icons.Default.Work, "Perfil", role)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Botão popular dados de demonstração
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = CardWhite),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Dados de demonstração",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = {
                        coroutineScope.launch {
                            demoLoading = true
                            demoMessage = null
                            DemoDataRepository().seedDemoDataIfNeeded()
                                .onSuccess { demoMessage = it }
                                .onFailure { demoMessage = "Erro: ${it.message}" }
                            demoLoading = false
                        }
                    },
                    enabled = !demoLoading,
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AccentBlue)
                ) {
                    if (demoLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("Popular dados de demonstração", fontWeight = FontWeight.SemiBold)
                    }
                }
                if (demoMessage != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = demoMessage!!,
                        fontSize = 12.sp,
                        color = if (demoMessage!!.startsWith("Erro")) DangerRed else SuccessGreen
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Botão sair
        OutlinedButton(
            onClick = { showDialog = true },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .height(52.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = DangerRed),
            border = androidx.compose.foundation.BorderStroke(1.5.dp, DangerRed)
        ) {
            Icon(Icons.Default.Logout, contentDescription = null, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Sair da conta", fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun ProfileInfoRow(icon: ImageVector, label: String, value: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(PrimaryBlue.copy(alpha = 0.08f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(18.dp))
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(label, fontSize = 11.sp, color = TextSecondary)
            Text(value, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = TextPrimary)
        }
    }
}
