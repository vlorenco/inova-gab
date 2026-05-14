package br.com.fiap.inovagab.ui.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import br.com.fiap.inovagab.R
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import br.com.fiap.inovagab.ui.components.AppLoginTextField
import br.com.fiap.inovagab.ui.components.AppPrimaryButton
import br.com.fiap.inovagab.ui.components.QuickAccessCard
import br.com.fiap.inovagab.ui.theme.*

@Composable
fun LoginScreen(
    onLoginSuccess: (String) -> Unit,
    viewModel: LoginViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(viewModel, lifecycleOwner) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.navigateTo.collect { role ->
                onLoginSuccess(role)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LightBackground)
            .verticalScroll(rememberScrollState())
    ) {
        // ── Header azul com degradê e marca d'água ──────────────────────────
        LoginHeader()

        // ── Card de login sobrepondo o header ───────────────────────────────
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .offset(y = (-32).dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = CardWhite),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier.padding(28.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                // Título do card
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(PrimaryBlue.copy(alpha = 0.08f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = PrimaryBlue,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Column {
                        Text(
                            text = "Faça seu login",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "Acesse sua conta corporativa",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                    }
                }

                HorizontalDivider(color = Color(0xFFF1F5F9))

                // Campos
                AppLoginTextField(
                    value = uiState.email,
                    onValueChange = viewModel::onEmailChange,
                    label = "E-mail",
                    leadingIcon = Icons.Default.Email,
                    enabled = !uiState.isLoading
                )

                AppLoginTextField(
                    value = uiState.password,
                    onValueChange = viewModel::onPasswordChange,
                    label = "Senha",
                    leadingIcon = Icons.Default.Lock,
                    isPassword = true,
                    enabled = !uiState.isLoading
                )

                // Mensagem de erro
                if (uiState.errorMessage != null) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(DangerRed.copy(alpha = 0.08f))
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = uiState.errorMessage!!,
                            color = DangerRed,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                // Botão Entrar com loading integrado
                AppPrimaryButton(
                    text = "Entrar",
                    onClick = viewModel::login,
                    isLoading = uiState.isLoading,
                    enabled = !uiState.isLoading
                )
            }
        }

        // ── Acesso rápido para testes ────────────────────────────────────────
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .offset(y = (-16).dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                HorizontalDivider(
                    modifier = Modifier.weight(1f),
                    color = Color(0xFFE2E8F0)
                )
                Text(
                    text = "  Acesso rápido para testes  ",
                    fontSize = 11.sp,
                    color = TextSecondary
                )
                HorizontalDivider(
                    modifier = Modifier.weight(1f),
                    color = Color(0xFFE2E8F0)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                QuickAccessCard(
                    label = "Operador",
                    description = "operador@app.com",
                    icon = Icons.Default.Person,
                    enabled = !uiState.isLoading,
                    onClick = {
                        viewModel.onEmailChange("operador@app.com")
                        viewModel.onPasswordChange("123456")
                        viewModel.login()
                    },
                    modifier = Modifier.weight(1f)
                )
                QuickAccessCard(
                    label = "Gestor",
                    description = "gestor@app.com",
                    icon = Icons.Default.Work,
                    enabled = !uiState.isLoading,
                    onClick = {
                        viewModel.onEmailChange("gestor@app.com")
                        viewModel.onPasswordChange("123456")
                        viewModel.login()
                    },
                    modifier = Modifier.weight(1f)
                )
                QuickAccessCard(
                    label = "Liderança",
                    description = "lider@app.com",
                    icon = Icons.Default.BarChart,
                    enabled = !uiState.isLoading,
                    onClick = {
                        viewModel.onEmailChange("lider@app.com")
                        viewModel.onPasswordChange("123456")
                        viewModel.login()
                    },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun LoginHeader() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(DarkBlue, PrimaryBlue)
                )
            )
    ) {
        // Marca d'água — círculos abstratos no fundo
        Box(
            modifier = Modifier
                .size(220.dp)
                .offset(x = (-60).dp, y = (-40).dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.04f))
        )
        Box(
            modifier = Modifier
                .size(160.dp)
                .align(Alignment.BottomEnd)
                .offset(x = 50.dp, y = 50.dp)
                .clip(CircleShape)
                .background(AccentBlue.copy(alpha = 0.18f))
        )
        Box(
            modifier = Modifier
                .size(90.dp)
                .align(Alignment.CenterEnd)
                .offset(x = 20.dp, y = (-20).dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.05f))
        )

        // Conteúdo do header
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = 32.dp, vertical = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Logo do Grupo Águia Branca
            Image(
                painter = painterResource(id = R.drawable.logobranca),
                contentDescription = "Logo Grupo Águia Branca",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .height(73.dp)
                    .widthIn(max = 260.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "INOVA+",
                color = Color.White,
                fontSize = 42.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 3.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Plataforma de Inovação Corporativa",
                color = Color.White.copy(alpha = 0.75f),
                fontSize = 13.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}
