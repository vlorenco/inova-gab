package br.com.fiap.inovagab.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
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
import br.com.fiap.inovagab.data.model.User
import br.com.fiap.inovagab.data.repository.AuthRepository
import br.com.fiap.inovagab.ui.theme.*
import kotlinx.coroutines.launch

/**
 * Perfil do usuário autenticado.
 * Antes: Firebase Auth + Firestore. Agora: GET /api/auth/me.
 */
@Composable
fun ProfileScreen(onLogout: () -> Unit) {
    val repository = remember { AuthRepository() }
    val coroutineScope = rememberCoroutineScope()

    var user by remember { mutableStateOf<User?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMsg by remember { mutableStateOf<String?>(null) }
    var showDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        repository.currentUser()
            .onSuccess { user = it }
            .onFailure { errorMsg = it.message }
        isLoading = false
    }

    val name = user?.name?.ifBlank { null } ?: "Usuário InovaGAB"
    val role = user?.role.orEmpty()

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Sair da conta?", fontWeight = FontWeight.Bold) },
            text = { Text("Tem certeza que deseja sair da sua conta?") },
            confirmButton = {
                TextButton(onClick = {
                    coroutineScope.launch {
                        // Logout no app = apagar o JWT local.
                        repository.logout()
                        showDialog = false
                        onLogout()
                    }
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
                Text(text = name, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                if (role.isNotBlank()) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Surface(shape = RoundedCornerShape(20.dp), color = Color.White.copy(alpha = 0.2f)) {
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

        if (isLoading) {
            Box(modifier = Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = PrimaryBlue)
            }
        }

        errorMsg?.let {
            Text(
                it,
                color = DangerRed,
                fontSize = 13.sp,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        Card(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
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
                ProfileInfoRow(Icons.Default.Email, "E-mail", user?.email ?: "—")
                if (role.isNotBlank()) {
                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Color(0xFFF1F5F9))
                    ProfileInfoRow(Icons.Default.Work, "Perfil", role)
                }
                // Pontuação só faz sentido para quem submete ideias.
                if (role == "OPERADOR") {
                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Color(0xFFF1F5F9))
                    ProfileInfoRow(
                        Icons.Default.EmojiEvents,
                        "Pontos de inovação",
                        "${user?.points ?: 0} pts"
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

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
