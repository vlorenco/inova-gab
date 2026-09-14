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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import br.com.fiap.inovagab.ui.components.AppTopBar
import br.com.fiap.inovagab.ui.gestor.model.Projeto
import br.com.fiap.inovagab.ui.theme.CardWhite
import br.com.fiap.inovagab.ui.theme.DangerRed
import br.com.fiap.inovagab.ui.theme.PrimaryBlue
import br.com.fiap.inovagab.ui.theme.SuccessGreen
import br.com.fiap.inovagab.ui.theme.TextPrimary
import br.com.fiap.inovagab.ui.theme.TextSecondary
import br.com.fiap.inovagab.ui.theme.WarningYellow

@Composable
fun ProjetosGestorScreen(
    viewModel: GestorViewModel = viewModel(),
    modifier: Modifier = Modifier,
    reloadKey: Int = 0
) {
    // Recarrega sempre que reloadKey mudar (inclusive na primeira composição)
    LaunchedEffect(reloadKey) {
        viewModel.carregarProjetos()
    }

    Column(modifier = modifier.fillMaxSize()) {
        AppTopBar(
            title = "Projetos",
            subtitle = "Acompanhamento dos projetos criados"
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

                viewModel.projetos.isEmpty() -> {
                    Text(
                        text = "Nenhum projeto cadastrado ainda.",
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
                        items(viewModel.projetos) { projeto ->
                            ProjetoCard(projeto = projeto)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ProjetoCard(projeto: Projeto) {
    val statusColor = when (projeto.status) {
        "Criado" -> PrimaryBlue
        "Em andamento" -> WarningYellow
        "Concluído" -> SuccessGreen
        else -> TextSecondary
    }

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
                    text = projeto.titulo,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    modifier = Modifier.weight(1f)
                )
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = statusColor.copy(alpha = 0.12f)
                ) {
                    Text(
                        text = projeto.status,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = statusColor,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            if (projeto.descricao.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = projeto.descricao,
                    fontSize = 13.sp,
                    color = TextSecondary
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = Color(0xFFF1F5F9))
            Spacer(modifier = Modifier.height(12.dp))

            ProjetoInfoRow(label = "Responsável", value = projeto.responsavel)

            if (projeto.roiEstimado > 0.0) {
                ProjetoInfoRow(
                    label = "ROI estimado",
                    value = "R$ %.2f".format(projeto.roiEstimado)
                )
            }
            if (projeto.reducaoCustoEstimada > 0.0) {
                ProjetoInfoRow(
                    label = "Redução de custo estimada",
                    value = "R$ %.2f".format(projeto.reducaoCustoEstimada)
                )
            }
        }
    }
}

@Composable
private fun ProjetoInfoRow(label: String, value: String) {
    if (value.isBlank()) return
    Column(modifier = Modifier.padding(bottom = 8.dp)) {
        Text(text = label, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = TextSecondary)
        Text(text = value, fontSize = 13.sp, color = TextPrimary)
    }
}
