package br.com.fiap.inovagab.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import br.com.fiap.inovagab.R
import br.com.fiap.inovagab.ui.theme.InovaBackground
import br.com.fiap.inovagab.ui.theme.InovaBorder
import br.com.fiap.inovagab.ui.theme.InovaGridLine
import br.com.fiap.inovagab.ui.theme.InovaHalo
import br.com.fiap.inovagab.ui.theme.InovaSize
import br.com.fiap.inovagab.ui.theme.InovaSpacing
import br.com.fiap.inovagab.ui.theme.InovaTextPrimary
import br.com.fiap.inovagab.ui.theme.InovaTextSecondary
import br.com.fiap.inovagab.ui.theme.InovaType

// ─────────────────────────────────────────────────────────────────────────────
// Fundação visual: malha de grid, halo azul, estrutura de tela e headers.
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Malha de grid de 30dp + halo azul no canto superior direito.
 * É a assinatura visual do header do INOVA+.
 */
fun Modifier.inovaHeaderBackdrop(): Modifier = this.drawBehind {
    val step = 30.dp.toPx()
    val hairline = 1.dp.toPx()

    var x = 0f
    while (x <= size.width) {
        drawLine(InovaGridLine, Offset(x, 0f), Offset(x, size.height), hairline)
        x += step
    }
    var y = 0f
    while (y <= size.height) {
        drawLine(InovaGridLine, Offset(0f, y), Offset(size.width, y), hairline)
        y += step
    }

    // Círculo de 280dp posicionado em top:-120dp right:-80dp.
    val radius = 140.dp.toPx()
    val center = Offset(
        x = size.width + 80.dp.toPx() - radius,
        y = -120.dp.toPx() + radius
    )
    drawCircle(
        brush = Brush.radialGradient(
            colorStops = arrayOf(0f to InovaHalo, 0.68f to Color.Transparent),
            center = center,
            radius = radius
        ),
        radius = radius,
        center = center
    )
}

/** Hairline inferior do header. */
private fun Modifier.bottomHairline(): Modifier = this.drawBehind {
    val h = 1.dp.toPx()
    drawRect(
        color = InovaBorder,
        topLeft = Offset(0f, size.height - h),
        size = androidx.compose.ui.geometry.Size(size.width, h)
    )
}

/**
 * Header padrão: seta de voltar à esquerda, ações e marca à direita,
 * título abaixo.
 *
 * A marca fica no canto direito porque a linha de cima é lida da esquerda
 * para a direita e termina nela — assinatura, não cabeçalho.
 *
 * @param onBack quando informado, desenha uma seta de voltar à esquerda.
 */
@Composable
fun InovaHeader(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    eyebrow: String? = null,
    showLogo: Boolean = true,
    onBack: (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {}
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(InovaBackground)
            .inovaHeaderBackdrop()
            .bottomHairline()
            .statusBarsPadding()
            .padding(
                start = InovaSpacing.screenHorizontal,
                end = InovaSpacing.screenHorizontal,
                top = InovaSpacing.headerTop,
                bottom = InovaSpacing.headerBottom
            )
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (onBack != null) {
                InovaIconButton(
                    icon = Icons.AutoMirrored.Outlined.ArrowBack,
                    contentDescription = "Voltar",
                    onClick = onBack
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                content = actions
            )

            if (showLogo) {
                Spacer(modifier = Modifier.width(10.dp))
                Image(
                    painter = painterResource(id = R.drawable.logo_inova_plus),
                    contentDescription = "INOVA+",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.height(InovaSize.headerLogo)
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        if (eyebrow != null) {
            MonoLabel(text = eyebrow)
            Spacer(modifier = Modifier.height(6.dp))
        }

        Text(text = title, style = InovaType.screenTitle, color = InovaTextPrimary)

        if (subtitle != null) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = subtitle, style = InovaType.subtitle, color = InovaTextSecondary)
        }
    }
}

/**
 * Header compacto das telas internas: seta de voltar e título na mesma linha.
 * Mantém a malha e o halo para continuidade visual.
 */
@Composable
fun InovaTopBar(
    title: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    actions: @Composable RowScope.() -> Unit = {}
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(InovaBackground)
            .inovaHeaderBackdrop()
            .bottomHairline()
            .statusBarsPadding()
            .padding(
                start = 8.dp,
                end = InovaSpacing.screenHorizontal,
                top = 10.dp,
                bottom = 14.dp
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        InovaIconButton(
            icon = Icons.AutoMirrored.Outlined.ArrowBack,
            contentDescription = "Voltar",
            onClick = onBack
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = title,
            style = InovaType.screenTitleSmall,
            color = InovaTextPrimary,
            modifier = Modifier.weight(1f)
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(2.dp),
            content = actions
        )
    }
}

/** Botão de ícone com alvo de toque de 44dp. */
@Composable
fun InovaIconButton(
    icon: ImageVector,
    contentDescription: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    tint: Color = InovaTextPrimary
) {
    IconButton(
        onClick = onClick,
        modifier = modifier.size(InovaSize.touchTarget)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = tint,
            modifier = Modifier.size(InovaSize.iconAction)
        )
    }
}


/**
 * Estrutura de tela: um único fundo, header fixo, conteúdo rolável
 * e bottom nav opcional.
 */
@Composable
fun InovaScreen(
    modifier: Modifier = Modifier,
    header: @Composable () -> Unit,
    bottomBar: @Composable (() -> Unit)? = null,
    scrollable: Boolean = true,
    contentPadding: PaddingValues = PaddingValues(
        horizontal = InovaSpacing.screenHorizontal,
        vertical = InovaSpacing.screenVertical
    ),
    contentSpacing: androidx.compose.ui.unit.Dp = InovaSpacing.block,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(InovaBackground)
    ) {
        header()

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .then(if (scrollable) Modifier.verticalScroll(rememberScrollState()) else Modifier)
                .padding(contentPadding),
            verticalArrangement = Arrangement.spacedBy(contentSpacing),
            content = content
        )

        if (bottomBar != null) {
            bottomBar()
        } else {
            Spacer(modifier = Modifier.navigationBarsPadding())
        }
    }
}

/** Contêiner de tela sem rolagem automática (para listas Lazy). */
@Composable
fun InovaListScreen(
    modifier: Modifier = Modifier,
    header: @Composable () -> Unit,
    bottomBar: @Composable (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(InovaBackground)
    ) {
        header()
        Column(modifier = Modifier.weight(1f).fillMaxWidth(), content = content)
        if (bottomBar != null) {
            bottomBar()
        } else {
            Spacer(modifier = Modifier.navigationBarsPadding())
        }
    }
}

