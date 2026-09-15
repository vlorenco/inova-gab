package br.com.fiap.inovagab.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.EmojiEvents
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.outlined.MailOutline
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Work
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import br.com.fiap.inovagab.data.model.User
import br.com.fiap.inovagab.data.repository.AuthRepository
import br.com.fiap.inovagab.ui.components.InovaCard
import br.com.fiap.inovagab.ui.components.InovaDivider
import br.com.fiap.inovagab.ui.components.InovaInlineMessage
import br.com.fiap.inovagab.ui.components.InovaLoading
import br.com.fiap.inovagab.ui.components.InovaOutlineButton
import br.com.fiap.inovagab.ui.components.InovaScreen
import br.com.fiap.inovagab.ui.components.MonoLabel
import br.com.fiap.inovagab.ui.components.SectionHeader
import br.com.fiap.inovagab.ui.components.StatusBadge
import br.com.fiap.inovagab.ui.components.inovaHeaderBackdrop
import br.com.fiap.inovagab.ui.theme.InovaBackground
import br.com.fiap.inovagab.ui.theme.InovaBlue
import br.com.fiap.inovagab.ui.theme.InovaBlueLight
import br.com.fiap.inovagab.ui.theme.InovaSpacing
import br.com.fiap.inovagab.ui.theme.InovaStatusError
import br.com.fiap.inovagab.ui.theme.InovaSurface
import br.com.fiap.inovagab.ui.theme.InovaTextPrimary
import br.com.fiap.inovagab.ui.theme.InovaTextSecondary
import br.com.fiap.inovagab.ui.theme.InovaTextTertiary
import br.com.fiap.inovagab.ui.theme.InovaType
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
            containerColor = InovaSurface,
            titleContentColor = InovaTextPrimary,
            textContentColor = InovaTextSecondary,
            title = { Text("Sair da conta?", style = InovaType.sectionTitle) },
            text = {
                Text("Tem certeza que deseja sair da sua conta?", style = InovaType.body)
            },
            confirmButton = {
                TextButton(onClick = {
                    coroutineScope.launch {
                        // Logout no app = apagar o JWT local.
                        repository.logout()
                        showDialog = false
                        onLogout()
                    }
                }) {
                    Text("Sair", style = InovaType.cardLabel, color = InovaStatusError)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Cancelar", style = InovaType.cardLabel, color = InovaTextSecondary)
                }
            }
        )
    }

    InovaScreen(
        header = { ProfileHeader(name = name, role = role) }
    ) {
        if (isLoading) {
            Box(modifier = Modifier.fillMaxWidth().height(160.dp)) { InovaLoading() }
        }

        errorMsg?.let { InovaInlineMessage(message = it, isError = true) }

        SectionHeader(title = "Informações da conta")

        InovaCard(contentPadding = PaddingValues(16.dp)) {
            ProfileInfoRow(Icons.Outlined.Person, "Nome", name)
            InovaDivider(modifier = Modifier.padding(vertical = 14.dp))
            ProfileInfoRow(Icons.Outlined.MailOutline, "E-mail", user?.email ?: "—")
            if (role.isNotBlank()) {
                InovaDivider(modifier = Modifier.padding(vertical = 14.dp))
                ProfileInfoRow(Icons.Outlined.Work, "Perfil", role)
            }
            // Pontuação só faz sentido para quem submete ideias.
            if (role == "OPERADOR") {
                InovaDivider(modifier = Modifier.padding(vertical = 14.dp))
                ProfileInfoRow(
                    Icons.Outlined.EmojiEvents,
                    "Pontos de inovação",
                    "${user?.points ?: 0} pts"
                )
            }
        }

        InovaOutlineButton(
            text = "Sair da conta",
            onClick = { showDialog = true },
            color = InovaStatusError,
            leadingIcon = Icons.AutoMirrored.Outlined.Logout,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))
    }
}

/** Header do perfil: avatar azul sólido sobre a malha e o halo. */
@Composable
private fun ProfileHeader(name: String, role: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(InovaBackground)
            .inovaHeaderBackdrop()
            .statusBarsPadding()
            .padding(
                start = InovaSpacing.screenHorizontal,
                end = InovaSpacing.screenHorizontal,
                top = InovaSpacing.headerTop,
                bottom = InovaSpacing.headerBottom
            ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        MonoLabel(text = "Meu Perfil", color = InovaTextTertiary)

        Spacer(modifier = Modifier.height(20.dp))

        Box(
            modifier = Modifier
                .size(76.dp)
                .clip(CircleShape)
                .background(InovaBlue),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = name.take(1).uppercase(),
                style = InovaType.displayTitle,
                color = InovaTextPrimary
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = name, style = InovaType.screenTitle, color = InovaTextPrimary)

        if (role.isNotBlank()) {
            Spacer(modifier = Modifier.height(10.dp))
            StatusBadge(text = role, color = InovaBlueLight)
        }
    }
}

@Composable
private fun ProfileInfoRow(icon: ImageVector, label: String, value: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(34.dp)
                .clip(CircleShape)
                .background(br.com.fiap.inovagab.ui.theme.InovaBlueTint),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = InovaBlueLight,
                modifier = Modifier.size(16.dp)
            )
        }
        Spacer(modifier = Modifier.width(14.dp))
        Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
            MonoLabel(text = label, color = InovaTextTertiary, style = InovaType.monoTiny)
            Text(text = value, style = InovaType.body, color = InovaTextPrimary)
        }
    }
}
