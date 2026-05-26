package br.com.fiap.inovagab.ui.gestor

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import br.com.fiap.inovagab.ui.components.AppTopBar
import br.com.fiap.inovagab.ui.operador.model.Ideia
import br.com.fiap.inovagab.ui.theme.CardWhite
import br.com.fiap.inovagab.ui.theme.DangerRed
import br.com.fiap.inovagab.ui.theme.LightBackground
import br.com.fiap.inovagab.ui.theme.PrimaryBlue
import br.com.fiap.inovagab.ui.theme.SuccessGreen
import br.com.fiap.inovagab.ui.theme.TextPrimary
import br.com.fiap.inovagab.ui.theme.TextSecondary
import br.com.fiap.inovagab.ui.theme.WarningYellow
import br.com.fiap.inovagab.data.repositoryfirestore.RankingService
import kotlinx.coroutines.launch

@Composable
fun IdeiasGestorScreen(
    viewModel: GestorViewModel = viewModel(),
    modifier: Modifier = Modifier,
    onAprovarECriarProjeto: (Ideia) -> Unit = {}
) {
    val rankingService = remember { RankingService() }
    val coroutineScope = rememberCoroutineScope()
    
    LaunchedEffect(Unit) {
        viewModel.carregarIdeiasPendentes()
    }

    Column(
        modifier = modifier.fillMaxSize()
    ) {
        AppTopBar(
            title = "Avaliar ideias",
            subtitle = "Ideias pendentes dos operadores"
        )

        Box(modifier = Modifier.fillMaxSize()) {
            when {
                viewModel.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = PrimaryBlue
                    )
                }

                viewModel.errorMessage != null -> {
                    Text(
                        text = viewModel.errorMessage!!,
                        color = DangerRed,
                        fontSize = 14.sp,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(horizontal = 32.dp)
                    )
                }

                viewModel.ideiasPendentes.isEmpty() -> {
                    Text(
                        text = "Nenhuma ideia pendente no momento.",
                        color = TextSecondary,
                        fontSize = 14.sp,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(top = 16.dp, bottom = 24.dp)
                    ) {
                        items(viewModel.ideiasPendentes) { ideia ->
                            IdeiaGestorCard(
                                ideia = ideia,
                                operadorId = ideia.operadorId,
                                rankingService = rankingService,
                                coroutineScope = coroutineScope,
                                onAprovar = { 
                                    viewModel.aprovarIdeia(ideia.id)
                                    coroutineScope.launch {
                                        rankingService.adicionarPontos(ideia.operadorId, 50)
                                    }
                                },
                                onReprovar = { viewModel.reprovarIdeia(ideia.id) },
                                onAprovarECriarProjeto = { 
                                    onAprovarECriarProjeto(ideia)
                                    coroutineScope.launch {
                                        rankingService.adicionarPontos(ideia.operadorId, 100)
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun IdeiaGestorCard(
    ideia: Ideia,
    operadorId: String,
    rankingService: RankingService,
    coroutineScope: kotlinx.coroutines.CoroutineScope,
    onAprovar: () -> Unit,
    onReprovar: () -> Unit,
    onAprovarECriarProjeto: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = ideia.titulo,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = WarningYellow.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = ideia.status,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = WarningYellow,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = Color(0xFFF1F5F9))
            Spacer(modifier = Modifier.height(12.dp))

            IdeiaInfoRow(label = "Área", value = ideia.area)
            IdeiaInfoRow(label = "Categoria", value = ideia.categoria)
            IdeiaInfoRow(label = "Problema", value = ideia.problema)
            IdeiaInfoRow(label = "Solução", value = ideia.solucao)
            IdeiaInfoRow(label = "Benefício", value = ideia.beneficio)

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = onReprovar,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = DangerRed),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, DangerRed)
                ) {
                    Text("Reprovar", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                }
                Button(
                    onClick = onAprovar,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SuccessGreen,
                        contentColor = Color.White
                    )
                ) {
                    Text("Aprovar", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = onAprovarECriarProjeto,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryBlue,
                    contentColor = Color.White
                )
            ) {
                Text("Aprovar e criar projeto", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
            }
        }
    }
}

@Composable
private fun IdeiaInfoRow(label: String, value: String) {
    if (value.isBlank()) return
    Column(modifier = Modifier.padding(bottom = 8.dp)) {
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = TextSecondary
        )
        Text(
            text = value,
            fontSize = 13.sp,
            color = TextPrimary
        )
    }
}