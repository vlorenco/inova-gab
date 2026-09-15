package br.com.fiap.inovagab.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import br.com.fiap.inovagab.R

// ─────────────────────────────────────────────────────────────────────────────
// Tipografia INOVA+
//
//   Inter           -> interface (títulos, corpo, rótulos de card)
//   JetBrains Mono  -> tudo que é "sistema": rótulos de campo, eyebrows,
//                      contadores, e-mails, datas, status. Sempre MAIÚSCULO.
//
// Piso absoluto de tamanho: 9.5sp. Credenciais e e-mails: 10sp.
// ─────────────────────────────────────────────────────────────────────────────

@OptIn(ExperimentalTextApi::class)
private fun interFont(weight: FontWeight) = Font(
    R.font.inter_variable,
    weight = weight,
    variationSettings = FontVariation.Settings(FontVariation.weight(weight.weight))
)

@OptIn(ExperimentalTextApi::class)
private fun monoFont(weight: FontWeight) = Font(
    R.font.jetbrains_mono_variable,
    weight = weight,
    variationSettings = FontVariation.Settings(FontVariation.weight(weight.weight))
)

val Inter = FontFamily(
    interFont(FontWeight.Normal),
    interFont(FontWeight.Medium),
    interFont(FontWeight.SemiBold),
    interFont(FontWeight.Bold),
    interFont(FontWeight.ExtraBold)
)

val JetBrainsMono = FontFamily(
    monoFont(FontWeight.Normal),
    monoFont(FontWeight.Medium),
    monoFont(FontWeight.Bold)
)

/** Estilos prontos do sistema visual. */
object InovaType {

    // ── Inter ────────────────────────────────────────────────────────────────

    /** Assinatura da marca no splash: 52sp / 800. */
    val brandDisplay = TextStyle(
        fontFamily = Inter,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 52.sp,
        lineHeight = 58.sp,
        letterSpacing = (-2).sp
    )

    /** Assinatura de apoio do splash: espaçada e leve. */
    val brandTagline = TextStyle(
        fontFamily = Inter,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 3.sp
    )

    /** Título de tela: 26sp / 800 / -1px de tracking. */
    val displayTitle = TextStyle(
        fontFamily = Inter,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 26.sp,
        lineHeight = 30.sp,
        letterSpacing = (-1).sp
    )

    /** Título de header: 22sp / 800. */
    val screenTitle = TextStyle(
        fontFamily = Inter,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 22.sp,
        lineHeight = 26.sp,
        letterSpacing = (-1).sp
    )

    /**
     * Título menor (cabeçalho de tela de detalhe): 20sp / 800.
     * Tracking um pouco menos fechado que os títulos grandes: a -1sp o espaço
     * entre palavras some neste corpo.
     */
    val screenTitleSmall = TextStyle(
        fontFamily = Inter,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 20.sp,
        lineHeight = 24.sp,
        letterSpacing = (-0.5).sp
    )

    /** Subtítulo abaixo do título: 13sp. */
    val subtitle = TextStyle(
        fontFamily = Inter,
        fontWeight = FontWeight.Normal,
        fontSize = 13.sp,
        lineHeight = 18.sp
    )

    /** Título de seção: 14sp / 700. */
    val sectionTitle = TextStyle(
        fontFamily = Inter,
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp,
        lineHeight = 18.sp
    )

    /** Rótulo de card: 13sp / 700. */
    val cardLabel = TextStyle(
        fontFamily = Inter,
        fontWeight = FontWeight.Bold,
        fontSize = 13.sp,
        lineHeight = 17.sp
    )

    /** Corpo padrão: 13sp. */
    val body = TextStyle(
        fontFamily = Inter,
        fontWeight = FontWeight.Normal,
        fontSize = 13.sp,
        lineHeight = 19.sp
    )

    /** Corpo compacto: 12sp. */
    val bodySmall = TextStyle(
        fontFamily = Inter,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 17.sp
    )

    /** Corpo mínimo: 11sp. */
    val bodyTiny = TextStyle(
        fontFamily = Inter,
        fontWeight = FontWeight.Normal,
        fontSize = 11.sp,
        lineHeight = 15.sp
    )

    /** Número grande (pontos, ROI, métricas): 34sp / 800. */
    val metric = TextStyle(
        fontFamily = Inter,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 34.sp,
        lineHeight = 38.sp,
        letterSpacing = (-1).sp
    )

    /** Número médio de KPI: 21sp / 800. */
    val metricSmall = TextStyle(
        fontFamily = Inter,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 21.sp,
        lineHeight = 25.sp,
        letterSpacing = (-0.5).sp
    )

    /** Texto do botão primário: 15sp / 700. */
    val button = TextStyle(
        fontFamily = Inter,
        fontWeight = FontWeight.Bold,
        fontSize = 15.sp,
        lineHeight = 19.sp
    )

    // ── JetBrains Mono (sempre MAIÚSCULO, tracking 1.2) ──────────────────────

    /** Eyebrow / rótulo de sistema: 10sp. */
    val mono = TextStyle(
        fontFamily = JetBrainsMono,
        fontWeight = FontWeight.Medium,
        fontSize = 10.sp,
        lineHeight = 14.sp,
        letterSpacing = 1.2.sp
    )

    /** Contador de card, status e demais microtextos: 9.5sp (piso). */
    val monoTiny = TextStyle(
        fontFamily = JetBrainsMono,
        fontWeight = FontWeight.Medium,
        fontSize = 9.5.sp,
        lineHeight = 13.sp,
        letterSpacing = 1.2.sp
    )

    /** Badge de status: 9.5sp / 700. */
    val monoBadge = TextStyle(
        fontFamily = JetBrainsMono,
        fontWeight = FontWeight.Bold,
        fontSize = 9.5.sp,
        lineHeight = 13.sp,
        letterSpacing = 1.2.sp
    )

    /** E-mail e credenciais: nunca abaixo de 10sp. */
    val monoCredential = TextStyle(
        fontFamily = JetBrainsMono,
        fontWeight = FontWeight.Normal,
        fontSize = 10.sp,
        lineHeight = 14.sp,
        letterSpacing = 1.2.sp
    )
}

/** Tipografia Material derivada da Inter, para componentes que a consomem. */
val Typography = Typography(
    displayLarge = InovaType.displayTitle,
    titleLarge = InovaType.screenTitle,
    titleMedium = InovaType.sectionTitle,
    titleSmall = InovaType.cardLabel,
    bodyLarge = InovaType.body,
    bodyMedium = InovaType.bodySmall,
    bodySmall = InovaType.bodyTiny,
    labelLarge = InovaType.button,
    labelMedium = InovaType.mono,
    labelSmall = InovaType.monoTiny
)
