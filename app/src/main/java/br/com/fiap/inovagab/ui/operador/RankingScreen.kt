package br.com.fiap.inovagab.ui.operador

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import br.com.fiap.inovagab.data.model.MyRanking
import br.com.fiap.inovagab.data.model.RankingEntry
import br.com.fiap.inovagab.data.repository.RankingRepository
import br.com.fiap.inovagab.ui.components.InovaCard
import br.com.fiap.inovagab.ui.components.InovaEmptyState
import br.com.fiap.inovagab.ui.components.InovaErrorState
import br.com.fiap.inovagab.ui.components.InovaListScreen
import br.com.fiap.inovagab.ui.components.InovaLoading
import br.com.fiap.inovagab.ui.components.InovaTopBar
import br.com.fiap.inovagab.ui.components.PointsBlock
import br.com.fiap.inovagab.ui.components.SectionHeader
import br.com.fiap.inovagab.ui.theme.InovaBlueLight
import br.com.fiap.inovagab.ui.theme.InovaBlueTint
import br.com.fiap.inovagab.ui.theme.InovaBlueTintBorder
import br.com.fiap.inovagab.ui.theme.InovaSpacing
import br.com.fiap.inovagab.ui.theme.InovaTextPrimary
import br.com.fiap.inovagab.ui.theme.InovaTextSecondary
import br.com.fiap.inovagab.ui.theme.InovaTrack
import br.com.fiap.inovagab.ui.theme.InovaType

@Composable
fun RankingScreen(onBack: () -> Unit) {
    val repository = remember { RankingRepository() }

    var ranking by remember { mutableStateOf<List<RankingEntry>>(emptyList()) }
    var myPosition by remember { mutableStateOf<MyRanking?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        repository.getRanking()
            .onSuccess { ranking = it }
            .onFailure { errorMsg = it.message }
        repository.getMyPosition().onSuccess { myPosition = it }
        isLoading = false
    }

    val topPoints = ranking.maxOfOrNull { it.points } ?: 0

    InovaListScreen(
        header = { InovaTopBar(title = "Ranking de Inovadores", onBack = onBack) }
    ) {
        when {
            isLoading -> InovaLoading()
            errorMsg != null -> InovaErrorState(errorMsg!!)
            else -> LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(InovaSpacing.card),
                contentPadding = PaddingValues(
                    start = InovaSpacing.screenHorizontal,
                    end = InovaSpacing.screenHorizontal,
                    top = InovaSpacing.screenVertical,
                    bottom = 28.dp
                )
            ) {
                myPosition?.let { me ->
                    item {
                        PointsBlock(
                            pointsLabel = "Meus pontos",
                            points = me.points,
                            pointsUnit = "pts",
                            positionLabel = "Posição",
                            positionText = if (me.position > 0) {
                                "${me.position}º de ${me.totalOperators}"
                            } else {
                                null
                            },
                            progress = if (topPoints > 0) me.points / topPoints.toFloat() else 0f,
                            progressCaption = null
                        )
                    }
                    item { Spacer(modifier = Modifier.height(2.dp)) }
                }

                if (ranking.isEmpty()) {
                    item { InovaEmptyState("Nenhum operador pontuou ainda.") }
                } else {
                    item {
                        SectionHeader(
                            title = "Ranking de Inovadores",
                            trailing = ranking.size.toString().padStart(2, '0')
                        )
                    }
                    items(ranking) { entry ->
                        RankingItem(
                            posicao = entry.position,
                            nome = entry.name,
                            pontos = entry.points,
                            destaque = entry.position == myPosition?.position,
                            progress = if (topPoints > 0) entry.points / topPoints.toFloat() else 0f
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RankingItem(
    posicao: Int,
    nome: String,
    pontos: Int,
    destaque: Boolean,
    progress: Float
) {
    InovaCard(accent = destaque) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            PositionMedal(posicao = posicao)
            Spacer(modifier = Modifier.width(13.dp))
            Text(
                text = nome,
                style = InovaType.cardLabel,
                color = InovaTextPrimary,
                modifier = Modifier.weight(1f)
            )
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = pontos.toString(),
                    style = InovaType.metricSmall,
                    color = InovaTextPrimary
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "pts",
                    style = InovaType.monoTiny,
                    color = InovaBlueLight,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(11.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .clip(CircleShape)
                .background(InovaTrack)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(progress.coerceIn(0f, 1f))
                    .height(4.dp)
                    .clip(CircleShape)
                    .background(
                        androidx.compose.ui.graphics.Brush.horizontalGradient(
                            listOf(br.com.fiap.inovagab.ui.theme.InovaBlue, InovaBlueLight)
                        )
                    )
            )
        }
    }
}

/**
 * Posição no ranking. Os três primeiros ganham o troféu — único ícone
 * preenchido do app, por ser elemento de gamificação — sem perder o número.
 */
@Composable
private fun PositionMedal(posicao: Int) {
    val podium = posicao in 1..3
    Box(
        modifier = Modifier
            .size(34.dp)
            .clip(CircleShape)
            .background(if (podium) InovaBlueTint else Color.Transparent)
            .border(
                BorderStroke(1.dp, if (podium) InovaBlueTintBorder else InovaTrack),
                CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        if (podium) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.EmojiEvents,
                    contentDescription = null,
                    tint = InovaBlueLight,
                    modifier = Modifier.size(11.dp)
                )
                Spacer(modifier = Modifier.width(2.dp))
                Text(
                    text = posicao.toString(),
                    style = InovaType.monoBadge,
                    color = InovaBlueLight
                )
            }
        } else {
            Text(
                text = posicao.toString(),
                style = InovaType.monoBadge,
                color = InovaTextSecondary
            )
        }
    }
}
