package br.com.fiap.inovagab.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

/**
 * O INOVA+ tem um único tema: quase-preto com azul institucional.
 * Não segue o claro/escuro do sistema — a identidade é sempre a mesma.
 */
private val InovaColorScheme = darkColorScheme(
    primary = InovaBlue,
    onPrimary = InovaTextPrimary,
    primaryContainer = InovaBlueDeep,
    onPrimaryContainer = InovaTextPrimary,
    secondary = InovaBlueLight,
    onSecondary = InovaBackground,
    background = InovaBackground,
    onBackground = InovaTextPrimary,
    surface = InovaSurface,
    onSurface = InovaTextPrimary,
    surfaceVariant = InovaToggleTrack,
    onSurfaceVariant = InovaTextSecondary,
    outline = InovaBorder,
    outlineVariant = InovaTrack,
    error = InovaStatusError,
    onError = InovaTextPrimary
)

@Composable
fun InovaGABTheme(
    content: @Composable () -> Unit
) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            // Ícones claros na status/nav bar, sobre o fundo quase-preto.
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = false
                isAppearanceLightNavigationBars = false
            }
        }
    }

    MaterialTheme(
        colorScheme = InovaColorScheme,
        typography = Typography,
        content = content
    )
}
