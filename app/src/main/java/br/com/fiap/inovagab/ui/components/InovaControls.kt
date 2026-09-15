package br.com.fiap.inovagab.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import br.com.fiap.inovagab.ui.theme.InovaBackground
import br.com.fiap.inovagab.ui.theme.InovaBlue
import br.com.fiap.inovagab.ui.theme.InovaBlueLight
import br.com.fiap.inovagab.ui.theme.InovaBorder
import br.com.fiap.inovagab.ui.theme.InovaDurationDefault
import br.com.fiap.inovagab.ui.theme.InovaFieldUnderline
import br.com.fiap.inovagab.ui.theme.InovaShapes
import br.com.fiap.inovagab.ui.theme.InovaSize
import br.com.fiap.inovagab.ui.theme.InovaTextDisabled
import br.com.fiap.inovagab.ui.theme.InovaTextFaint
import br.com.fiap.inovagab.ui.theme.InovaTextPrimary
import br.com.fiap.inovagab.ui.theme.InovaTextSecondary
import br.com.fiap.inovagab.ui.theme.InovaToggleTrack
import br.com.fiap.inovagab.ui.theme.InovaTrack
import br.com.fiap.inovagab.ui.theme.InovaType
import br.com.fiap.inovagab.ui.theme.inovaTween

// ─────────────────────────────────────────────────────────────────────────────
// Controles: botões, campos, toggle, badge de status e barra de progresso.
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Botão primário: pílula branca de 56dp, texto quase-preto e seta azul.
 */
@Composable
fun InovaPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    trailingIcon: ImageVector? = Icons.AutoMirrored.Outlined.ArrowForward
) {
    val active = enabled && !isLoading
    val container by animateColorAsState(
        targetValue = if (active) Color.White else Color(0xFFCBD3DC),
        animationSpec = inovaTween(InovaDurationDefault),
        label = "primaryButtonContainer"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(InovaSize.primaryButton)
            .shadow(
                elevation = 14.dp,
                shape = InovaShapes.pill,
                ambientColor = Color.Black,
                spotColor = Color.Black
            )
            .clip(InovaShapes.pill)
            .background(container)
            .clickable(enabled = active, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(22.dp),
                color = InovaBlue,
                strokeWidth = 2.5.dp
            )
        } else {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(text = text, style = InovaType.button, color = InovaBackground)
                if (trailingIcon != null) {
                    Spacer(modifier = Modifier.width(10.dp))
                    Icon(
                        imageVector = trailingIcon,
                        contentDescription = null,
                        tint = InovaBlue,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

/** Botão secundário: contorno discreto sobre o fundo da tela. */
@Composable
fun InovaOutlineButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    color: Color = InovaTextSecondary,
    leadingIcon: ImageVector? = null
) {
    Box(
        modifier = modifier
            .heightIn(min = InovaSize.touchTarget)
            .clip(InovaShapes.pill)
            .border(BorderStroke(InovaSize.border, if (enabled) color else InovaTextFaint), InovaShapes.pill)
            .clickable(enabled = enabled, onClick = onClick)
            .padding(horizontal = 18.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (leadingIcon != null) {
                Icon(
                    imageVector = leadingIcon,
                    contentDescription = null,
                    tint = if (enabled) color else InovaTextFaint,
                    modifier = Modifier.size(InovaSize.iconInline)
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(
                text = text,
                style = InovaType.cardLabel,
                color = if (enabled) color else InovaTextFaint,
                maxLines = 1
            )
        }
    }
}

/** Botão sólido azul, para a ação de apoio dentro de um card. */
@Composable
fun InovaFilledButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    container: Color = InovaBlue,
    contentColor: Color = InovaTextPrimary,
    leadingIcon: ImageVector? = null
) {
    Box(
        modifier = modifier
            .heightIn(min = InovaSize.touchTarget)
            .clip(InovaShapes.pill)
            .background(if (enabled) container else InovaTrack)
            .clickable(enabled = enabled && !isLoading, onClick = onClick)
            .padding(horizontal = 18.dp),
        contentAlignment = Alignment.Center
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(18.dp),
                color = contentColor,
                strokeWidth = 2.dp
            )
        } else {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (leadingIcon != null) {
                    Icon(
                        imageVector = leadingIcon,
                        contentDescription = null,
                        tint = if (enabled) contentColor else InovaTextFaint,
                        modifier = Modifier.size(InovaSize.iconInline)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text(
                    text = text,
                    style = InovaType.cardLabel,
                    color = if (enabled) contentColor else InovaTextFaint,
                    maxLines = 1
                )
            }
        }
    }
}

/**
 * Campo de formulário: rótulo acima, valor em linha com sublinhado.
 * Foco engrossa o sublinhado e o pinta de azul claro.
 */
@Composable
fun InovaFormField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String? = null,
    enabled: Boolean = true,
    singleLine: Boolean = true,
    minLines: Int = 1,
    isPassword: Boolean = false,
    keyboardType: KeyboardType = KeyboardType.Text,
    trailingIcon: ImageVector? = null,
    onTrailingIconClick: (() -> Unit)? = null
) {
    val interactionSource = remember { MutableInteractionSource() }
    val focused by interactionSource.collectIsFocusedAsState()

    val underlineColor by animateColorAsState(
        targetValue = if (focused) InovaBlueLight else InovaFieldUnderline,
        animationSpec = inovaTween(InovaDurationDefault),
        label = "fieldUnderline"
    )
    val underlineHeight by animateDpAsState(
        targetValue = if (focused) 1.5.dp else 1.dp,
        animationSpec = inovaTween(InovaDurationDefault),
        label = "fieldUnderlineHeight"
    )

    Column(modifier = modifier.fillMaxWidth()) {
        Text(text = label, style = InovaType.cardLabel, color = InovaTextPrimary)
        Spacer(modifier = Modifier.height(10.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.weight(1f)) {
                BasicTextField(
                    value = value,
                    onValueChange = onValueChange,
                    enabled = enabled,
                    singleLine = singleLine,
                    minLines = minLines,
                    textStyle = InovaType.body.copy(color = InovaTextPrimary),
                    cursorBrush = SolidColor(InovaBlueLight),
                    interactionSource = interactionSource,
                    visualTransformation = if (isPassword) {
                        PasswordVisualTransformation()
                    } else {
                        VisualTransformation.None
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
                    modifier = Modifier
                        .fillMaxWidth()
                        .defaultMinSize(minHeight = 26.dp)
                )
                if (value.isEmpty() && placeholder != null) {
                    Text(
                        text = placeholder,
                        style = InovaType.body,
                        color = InovaTextFaint,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            if (trailingIcon != null) {
                Spacer(modifier = Modifier.width(10.dp))
                val tint = if (focused) InovaBlueLight else InovaTextDisabled
                if (onTrailingIconClick != null) {
                    // Ação no ícone (ex.: mostrar/ocultar senha) precisa de alvo de 44dp.
                    Box(
                        modifier = Modifier
                            .size(InovaSize.touchTarget)
                            .clip(InovaShapes.pill)
                            .clickable(onClick = onTrailingIconClick),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = trailingIcon,
                            contentDescription = null,
                            tint = tint,
                            modifier = Modifier.size(InovaSize.iconAction)
                        )
                    }
                } else {
                    Icon(
                        imageVector = trailingIcon,
                        contentDescription = null,
                        tint = tint,
                        modifier = Modifier.size(InovaSize.iconInline)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(underlineHeight)
                .background(underlineColor)
        )
    }
}

/** Campo somente leitura que abre um seletor ao ser tocado. */
@Composable
fun InovaPickerField(
    label: String,
    value: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    trailingIcon: ImageVector = Icons.Outlined.KeyboardArrowDown,
    expanded: Boolean = false
) {
    val rotation by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f,
        animationSpec = inovaTween(InovaDurationDefault),
        label = "pickerChevron"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Text(text = label, style = InovaType.cardLabel, color = InovaTextPrimary)
        Spacer(modifier = Modifier.height(10.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.heightIn(min = 26.dp)
        ) {
            Text(
                text = value,
                style = InovaType.body,
                color = InovaTextPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Icon(
                imageVector = trailingIcon,
                contentDescription = null,
                tint = if (expanded) InovaBlueLight else InovaTextDisabled,
                modifier = Modifier
                    .size(InovaSize.iconInline)
                    .androidxRotate(rotation)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(if (expanded) 1.5.dp else 1.dp)
                .background(if (expanded) InovaBlueLight else InovaFieldUnderline)
        )
    }
}

private fun Modifier.androidxRotate(degrees: Float): Modifier = this.rotate(degrees)

/**
 * Toggle segmentado. Rola horizontalmente quando há muitos segmentos,
 * preservando o alvo de toque mínimo.
 */
@Composable
fun InovaSegmentedToggle(
    options: List<String>,
    selected: String,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier,
    scrollable: Boolean = options.size > 3,
    /** Divide a largura igualmente entre os segmentos, em vez de ajustar ao texto. */
    fillWidth: Boolean = false
) {
    val row: @Composable () -> Unit = {
        Row(
            modifier = Modifier
                .then(if (fillWidth) Modifier.fillMaxWidth() else Modifier)
                .clip(InovaShapes.pill)
                .background(InovaToggleTrack)
                .border(BorderStroke(InovaSize.border, InovaBorder), InovaShapes.pill)
                .padding(3.dp),
            horizontalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            options.forEach { option ->
                val isActive = option == selected
                val bg by animateColorAsState(
                    targetValue = if (isActive) InovaBlue else Color.Transparent,
                    animationSpec = inovaTween(InovaDurationDefault),
                    label = "segmentBg"
                )
                val fg by animateColorAsState(
                    targetValue = if (isActive) InovaTextPrimary else InovaTextSecondary,
                    animationSpec = inovaTween(InovaDurationDefault),
                    label = "segmentFg"
                )
                Box(
                    modifier = Modifier
                        .then(if (fillWidth) Modifier.weight(1f) else Modifier)
                        .clip(InovaShapes.pill)
                        .background(bg)
                        .clickable { onSelect(option) }
                        .heightIn(min = 38.dp)
                        .padding(horizontal = if (fillWidth) 6.dp else 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = option,
                        style = InovaType.bodySmall.copy(
                            fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold
                        ),
                        color = fg,
                        maxLines = 1,
                        softWrap = false
                    )
                }
            }
        }
    }

    if (scrollable) {
        Box(modifier = modifier.fillMaxWidth().horizontalScroll(rememberScrollState())) { row() }
    } else {
        Box(modifier = modifier) { row() }
    }
}

/** Badge de status: fundo da cor a 14% e texto na cor cheia. */
@Composable
fun StatusBadge(
    text: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(InovaShapes.pill)
            .background(color.copy(alpha = 0.14f))
            .padding(horizontal = 10.dp, vertical = 5.dp)
    ) {
        Text(
            text = text.uppercase(),
            style = InovaType.monoBadge,
            color = color,
            maxLines = 1,
            softWrap = false
        )
    }
}

/** Barra de progresso de 4dp com preenchimento em gradiente azul. */
@Composable
fun InovaProgressBar(
    progress: Float,
    modifier: Modifier = Modifier
) {
    val animated by animateFloatAsState(
        targetValue = progress.coerceIn(0f, 1f),
        animationSpec = inovaTween(InovaDurationSlowValue),
        label = "progress"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(InovaSize.progressBar)
            .clip(InovaShapes.progress)
            .background(InovaTrack)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(animated)
                .height(InovaSize.progressBar)
                .clip(InovaShapes.progress)
                .background(
                    Brush.horizontalGradient(listOf(InovaBlue, InovaBlueLight))
                )
        )
    }
}

private const val InovaDurationSlowValue = 180

/** Chip discreto de metadado (categoria, campanha). */
@Composable
fun InovaTag(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = InovaBlueLight
) {
    Box(
        modifier = modifier
            .clip(InovaShapes.pill)
            .background(color.copy(alpha = 0.12f))
            .border(BorderStroke(InovaSize.border, color.copy(alpha = 0.28f)), InovaShapes.pill)
            .padding(horizontal = 9.dp, vertical = 4.dp)
    ) {
        Text(
            text = text.uppercase(),
            style = InovaType.monoTiny,
            color = color,
            maxLines = 1,
            softWrap = false
        )
    }
}

/** Interruptor de duas posições, para "vigente / inativa". */
@Composable
fun InovaSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val trackColor by animateColorAsState(
        targetValue = if (checked) InovaBlue else InovaToggleTrack,
        animationSpec = inovaTween(InovaDurationDefault),
        label = "switchTrack"
    )
    val knobOffset by animateDpAsState(
        targetValue = if (checked) 20.dp else 2.dp,
        animationSpec = inovaTween(InovaDurationDefault),
        label = "switchKnob"
    )

    Box(
        modifier = modifier
            .size(width = 44.dp, height = 26.dp)
            .clip(InovaShapes.pill)
            .background(trackColor)
            .border(BorderStroke(InovaSize.border, InovaBorder), InovaShapes.pill)
            .clickable { onCheckedChange(!checked) }
    ) {
        Box(
            modifier = Modifier
                .padding(start = knobOffset, top = 2.dp)
                .size(20.dp)
                .clip(InovaShapes.pill)
                .background(if (checked) InovaTextPrimary else InovaTextDisabled)
        )
    }
}
