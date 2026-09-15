package br.com.fiap.inovagab.ui.splash

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.StartOffset
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import br.com.fiap.inovagab.R
import br.com.fiap.inovagab.ui.theme.InovaBackground
import br.com.fiap.inovagab.ui.theme.InovaBlue
import br.com.fiap.inovagab.ui.theme.InovaBlueLight
import br.com.fiap.inovagab.ui.theme.InovaTextPrimary
import br.com.fiap.inovagab.ui.theme.InovaTextSecondary
import br.com.fiap.inovagab.ui.theme.InovaType
import kotlinx.coroutines.delay

/**
 * Rede de segurança: se ninguém tocar, a abertura sai sozinha.
 * O caminho normal é o toque.
 */
private const val SPLASH_TIMEOUT_MS = 8000L

/**
 * Abertura da marca: arte do Grupo Águia Branca em tela cheia, assinatura
 * INOVA+ e indicador de carregamento.
 *
 * Um toque em qualquer ponto da tela abre o login; quem não tocar é levado
 * adiante pelo tempo limite.
 */
@Composable
fun SplashScreen(onFinished: () -> Unit) {
    var contentVisible by remember { mutableStateOf(false) }
    // Garante que o login seja aberto uma vez só, mesmo com toque e tempo
    // limite acontecendo quase juntos.
    var finished by remember { mutableStateOf(false) }

    fun finishOnce() {
        if (!finished) {
            finished = true
            onFinished()
        }
    }

    LaunchedEffect(Unit) {
        contentVisible = true
        delay(SPLASH_TIMEOUT_MS)
        finishOnce()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(InovaBackground)
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() },
                onClick = { finishOnce() }
            )
    ) {
        Image(
            painter = painterResource(id = R.drawable.splash_bg),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Véu sutil no miolo: garante contraste do texto sobre a fotografia
        // sem apagar a arte.
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colorStops = arrayOf(
                            0.00f to Color.Transparent,
                            0.38f to InovaBackground.copy(alpha = 0.45f),
                            0.62f to InovaBackground.copy(alpha = 0.72f),
                            1.00f to InovaBackground.copy(alpha = 0.55f)
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.weight(0.44f))

            AnimatedVisibility(
                visible = contentVisible,
                enter = fadeIn(tween(700)) + slideInVertically(tween(700)) { it / 8 }
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Image(
                        painter = painterResource(id = R.drawable.logo_aguia_mono),
                        contentDescription = "Grupo Águia Branca",
                        contentScale = ContentScale.Fit,
                        colorFilter = ColorFilter.tint(InovaTextPrimary),
                        modifier = Modifier
                            .widthIn(max = 250.dp)
                            .height(54.dp)
                    )

                    Spacer(modifier = Modifier.height(26.dp))

                    Text(
                        text = buildAnnotatedString {
                            append("INOVA")
                            withStyle(SpanStyle(color = InovaBlueLight)) { append("+") }
                        },
                        style = InovaType.brandDisplay,
                        color = InovaTextPrimary
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "Inovação que move pessoas",
                        style = InovaType.brandTagline,
                        color = InovaTextSecondary,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.weight(0.38f))

            LoadingDots()

            Spacer(modifier = Modifier.weight(0.18f))
        }
    }
}

/** Três pontos pulsando em sequência, como no material da marca. */
@Composable
private fun LoadingDots(modifier: Modifier = Modifier) {
    val transition = rememberInfiniteTransition(label = "splashDots")

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(3) { index ->
            val pulse by transition.animateFloat(
                initialValue = 0.3f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(durationMillis = 520),
                    repeatMode = RepeatMode.Reverse,
                    initialStartOffset = StartOffset(index * 180)
                ),
                label = "dot$index"
            )

            Box(
                modifier = Modifier
                    .padding(horizontal = 7.dp)
                    .size(9.dp)
                    .clip(CircleShape)
                    .alpha(pulse)
                    .background(if (pulse > 0.75f) InovaBlueLight else InovaBlue)
            )
        }
    }
}
