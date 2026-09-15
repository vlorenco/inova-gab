package br.com.fiap.inovagab.ui.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.MailOutline
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import br.com.fiap.inovagab.R
import br.com.fiap.inovagab.ui.components.InovaFormField
import br.com.fiap.inovagab.ui.components.InovaInlineMessage
import br.com.fiap.inovagab.ui.components.InovaPrimaryButton
import br.com.fiap.inovagab.ui.components.InovaSegmentedToggle
import br.com.fiap.inovagab.ui.components.MonoLabel
import br.com.fiap.inovagab.ui.components.inovaHeaderBackdrop
import br.com.fiap.inovagab.ui.theme.InovaBackground
import br.com.fiap.inovagab.ui.theme.InovaBlue
import br.com.fiap.inovagab.ui.theme.InovaBlueLight
import br.com.fiap.inovagab.ui.theme.InovaPanelWhite
import br.com.fiap.inovagab.ui.theme.InovaSpacing
import br.com.fiap.inovagab.ui.theme.InovaTextPrimary
import br.com.fiap.inovagab.ui.theme.InovaTextTertiary
import br.com.fiap.inovagab.ui.theme.InovaType

/** Perfis de demonstração oferecidos pelo acesso rápido. */
private data class DemoProfile(val label: String, val email: String)

private val DEMO_PROFILES = listOf(
    DemoProfile("Operador", "operador@app.com"),
    DemoProfile("Gestor", "gestor@app.com"),
    DemoProfile("Liderança", "lider@app.com")
)

private const val DEMO_PASSWORD = "123456"

@Composable
fun LoginScreen(
    onLoginSuccess: (String) -> Unit,
    viewModel: LoginViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    var selectedProfile by remember { mutableStateOf(DEMO_PROFILES.first().label) }
    var passwordVisible by remember { mutableStateOf(false) }

    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(viewModel, lifecycleOwner) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.navigateTo.collect { role ->
                onLoginSuccess(role)
            }
        }
    }

    // Abre já com o perfil de operador preenchido — é o acesso rápido de testes.
    LaunchedEffect(Unit) {
        val profile = DEMO_PROFILES.first()
        viewModel.onEmailChange(profile.email)
        viewModel.onPasswordChange(DEMO_PASSWORD)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(InovaBackground)
            .verticalScroll(rememberScrollState())
            .imePadding()
    ) {
        BrandPanel()

        // A faixa branca por trás cria o recorte arredondado no canto superior
        // esquerdo da área escura.
        Box(modifier = Modifier.fillMaxWidth().background(InovaPanelWhite)) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(topStart = 38.dp))
                    .background(InovaBackground)
                    .inovaHeaderBackdrop()
                    .padding(
                        start = InovaSpacing.screenHorizontal,
                        end = InovaSpacing.screenHorizontal,
                        top = 30.dp,
                        bottom = 28.dp
                    )
            ) {
                // ── INOVA+ ───────────────────────────────────────────────────
                Text(
                    text = buildAnnotatedString {
                        append("INOVA")
                        withStyle(SpanStyle(color = InovaBlueLight)) { append("+") }
                    },
                    style = InovaType.displayTitle,
                    color = InovaTextPrimary
                )

                Spacer(modifier = Modifier.height(22.dp))

                // ── Acesso rápido ────────────────────────────────────────────
                MonoLabel(text = "Acesso rápido para testes", color = InovaTextTertiary)

                Spacer(modifier = Modifier.height(10.dp))

                InovaSegmentedToggle(
                    options = DEMO_PROFILES.map { it.label },
                    selected = selectedProfile,
                    fillWidth = true,
                    scrollable = false,
                    onSelect = { label ->
                        selectedProfile = label
                        val profile = DEMO_PROFILES.first { it.label == label }
                        viewModel.onEmailChange(profile.email)
                        viewModel.onPasswordChange(DEMO_PASSWORD)
                    }
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = DEMO_PROFILES.first { it.label == selectedProfile }.email,
                    style = InovaType.monoCredential,
                    color = InovaBlueLight,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(28.dp))

                // ── Formulário ───────────────────────────────────────────────
                Text(
                    text = "Faça seu login",
                    style = InovaType.screenTitleSmall,
                    color = InovaTextPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Acesse sua conta corporativa",
                    style = InovaType.bodySmall,
                    color = InovaBlueLight
                )

                Spacer(modifier = Modifier.height(24.dp))

                InovaFormField(
                    label = "E-mail",
                    value = uiState.email,
                    onValueChange = viewModel::onEmailChange,
                    placeholder = "nome@aguiabranca.com.br",
                    enabled = !uiState.isLoading,
                    keyboardType = KeyboardType.Email,
                    trailingIcon = Icons.Outlined.MailOutline
                )

                Spacer(modifier = Modifier.height(20.dp))

                InovaFormField(
                    label = "Senha",
                    value = uiState.password,
                    onValueChange = viewModel::onPasswordChange,
                    enabled = !uiState.isLoading,
                    isPassword = !passwordVisible,
                    keyboardType = KeyboardType.Password,
                    trailingIcon = if (passwordVisible) {
                        Icons.Outlined.VisibilityOff
                    } else {
                        Icons.Outlined.Visibility
                    },
                    onTrailingIconClick = { passwordVisible = !passwordVisible }
                )

                if (uiState.errorMessage != null) {
                    Spacer(modifier = Modifier.height(18.dp))
                    InovaInlineMessage(message = uiState.errorMessage!!, isError = true)
                }

                Spacer(modifier = Modifier.height(28.dp))

                InovaPrimaryButton(
                    text = "Entrar",
                    onClick = viewModel::login,
                    enabled = !uiState.isLoading,
                    isLoading = uiState.isLoading
                )

                Spacer(modifier = Modifier.navigationBarsPadding())
            }
        }
    }
}

/** Painel branco de marca: o único branco do app, com a logo em cor. */
@Composable
private fun BrandPanel() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(InovaPanelWhite)
            .statusBarsPadding()
            .padding(horizontal = 28.dp)
            .padding(top = 34.dp, bottom = 30.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo_aguia_branca),
            contentDescription = "Grupo Águia Branca",
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .widthIn(max = 260.dp)
                .height(56.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "80 ANOS • PLATAFORMA DE INOVAÇÃO CORPORATIVA",
            style = InovaType.monoTiny,
            color = InovaBlue,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
