package br.com.fiap.inovagab.ui.theme

import androidx.compose.ui.graphics.Color

// ─────────────────────────────────────────────────────────────────────────────
// Design tokens INOVA+ — quase-preto com azul institucional nos acentos.
// Uma tela, um fundo: nunca dois tons de fundo concorrendo.
// Estas são as únicas cores do app.
// ─────────────────────────────────────────────────────────────────────────────

/** Fundo da tela. */
val InovaBackground = Color(0xFF17191C)

/** Superfície de card. */
val InovaSurface = Color(0xFF1F2327)

/** Fundo da bottom nav. */
val InovaNavSurface = Color(0xFF1B1E21)

/** Borda de card, header e nav. */
val InovaBorder = Color(0xFF2E3237)

/** Trilha de progresso e divisor interno de card. */
val InovaTrack = Color(0xFF2B3035)

/** Trilha de toggle/filtro. */
val InovaToggleTrack = Color(0xFF232629)

/** Azul sólido: ícone principal, item ativo, avatar. */
val InovaBlue = Color(0xFF0F4A8A)

/** Azul escuro — início da faixa de destaque. */
val InovaBlueDeep = Color(0xFF0A3566)

/** Azul claro: destaque, números, links, ícones em card. */
val InovaBlueLight = Color(0xFF4FA3F0)

/** Tile de ícone secundário: azul translúcido. */
val InovaBlueTint = Color(0x292F8AE0)      // rgba(47,138,224,.16)
val InovaBlueTintBorder = Color(0x474FA3F0) // rgba(79,163,240,.28)

// Texto — sempre tinta sólida, nunca transparência.
val InovaTextPrimary = Color(0xFFFFFFFF)
val InovaTextSecondary = Color(0xFFADBDD0)
val InovaTextTertiary = Color(0xFF9AA3AD)
val InovaTextDisabled = Color(0xFF7E8DA2)
val InovaTextFaint = Color(0xFF5F6B78)

/** Painel branco — só no login. */
val InovaPanelWhite = Color(0xFFFFFFFF)

// Status
val InovaStatusDone = Color(0xFF4EC17F)      // concluído
val InovaStatusRunning = Color(0xFFE0A53A)   // em andamento
val InovaStatusError = Color(0xFFE2705F)     // erro / excluir

/** Sublinhado padrão de campo de formulário. */
val InovaFieldUnderline = Color(0x2EFFFFFF)  // rgba(255,255,255,.18)

/** Malha de grid do header. */
val InovaGridLine = Color(0x09FFFFFF)        // rgba(255,255,255,.035)

/** Halo azul do header. */
val InovaHalo = Color(0x4D2F8AE0)            // rgba(47,138,224,.3)
