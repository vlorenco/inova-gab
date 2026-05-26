package br.com.fiap.inovagab.ui.lideranca

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.fiap.inovagab.data.model.Project
import br.com.fiap.inovagab.data.repository.ProjectRepository
import br.com.fiap.inovagab.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(onBack: () -> Unit) {
    val repository = remember { ProjectRepository() }
    var projects by remember { mutableStateOf<List<Project>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        repository.getProjects()
            .onSuccess { projects = it; isLoading = false }
            .onFailure { isLoading = false }
    }

    // Calculate indicators
    val totalProjetos = projects.size
    val ativos = projects.count { it.status == "EM_ANDAMENTO" }
    val concluidos = projects.count { it.status == "CONCLUIDO" }
    val planejados = projects.count { it.status == "PLANEJADO" }
    val cancelados = projects.count { it.status == "CANCELADO" }
    val investimentoTotal = projects.sumOf { it.investment }
    val retornoTotal = projects.sumOf { it.financialReturn }
    val lucro = retornoTotal - investimentoTotal
    val roi = if (investimentoTotal > 0) ((retornoTotal - investimentoTotal) / investimentoTotal) * 100 else 0.0
    val reducaoCustos = projects.sumOf { it.costReduction }
    val produtividadeMedia = if (projects.isNotEmpty()) projects.sumOf { it.productivityGain } / projects.size else 0.0

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Dashboard", color = Color.White, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkBlue)
            )
        },
        containerColor = LightBackground
    ) { innerPadding ->
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = PrimaryBlue)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // KPIs
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    KpiCard("ROI Geral", "%.0f%%".format(roi), SuccessGreen, Modifier.weight(1f))
                    KpiCard("Retorno", formatCurrency(retornoTotal), PrimaryBlue, Modifier.weight(1f))
                }
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    KpiCard("Investimento", formatCurrency(investimentoTotal), WarningYellow, Modifier.weight(1f))
                    KpiCard("Lucro", formatCurrency(lucro), AccentBlue, Modifier.weight(1f))
                }
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    KpiCard("Redução custos", formatCurrency(reducaoCustos), SuccessGreen, Modifier.weight(1f))
                    KpiCard("Produtividade", "%.1f%%".format(produtividadeMedia), PrimaryBlue, Modifier.weight(1f))
                }

                // Gráfico de projetos
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = CardWhite),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text("Desempenho dos Projetos", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        Spacer(modifier = Modifier.height(16.dp))

                        if (totalProjetos > 0) {
                            Box(
                                modifier = Modifier.fillMaxWidth().height(180.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                DonutChart(ativos, concluidos, planejados, cancelados)
                                Text("$totalProjetos", fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, color = TextPrimary)
                            }

                            Spacer(modifier = Modifier.height(16.dp))
                            LegendItem("Em andamento", ativos, WarningYellow)
                            LegendItem("Concluídos", concluidos, SuccessGreen)
                            LegendItem("Planejados", planejados, PrimaryBlue)
                            LegendItem("Cancelados", cancelados, DangerRed)
                        } else {
                            Text("Nenhum projeto cadastrado.", fontSize = 13.sp, color = TextSecondary)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun KpiCard(label: String, value: String, color: Color, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            Text(label, fontSize = 11.sp, color = TextSecondary)
            Spacer(modifier = Modifier.height(4.dp))
            Text(value, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = color)
        }
    }
}

@Composable
private fun DonutChart(ativos: Int, concluidos: Int, planejados: Int, cancelados: Int) {
    val total = (ativos + concluidos + planejados + cancelados).toFloat().coerceAtLeast(1f)
    val segments = listOf(
        Pair(ativos.toFloat(), WarningYellow),
        Pair(concluidos.toFloat(), SuccessGreen),
        Pair(planejados.toFloat(), PrimaryBlue),
        Pair(cancelados.toFloat(), DangerRed)
    )

    Canvas(modifier = Modifier.size(160.dp)) {
        val strokeWidth = 32f
        val radius = (size.minDimension - strokeWidth) / 2
        val topLeft = Offset((size.width - radius * 2) / 2, (size.height - radius * 2) / 2)
        val arcSize = Size(radius * 2, radius * 2)
        var startAngle = -90f

        segments.forEach { (value, color) ->
            if (value > 0) {
                val sweep = (value / total) * 360f
                drawArc(color = color, startAngle = startAngle, sweepAngle = sweep, useCenter = false, topLeft = topLeft, size = arcSize, style = Stroke(width = strokeWidth))
                startAngle += sweep
            }
        }
    }
}

@Composable
private fun LegendItem(label: String, count: Int, color: Color) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Box(modifier = Modifier.size(10.dp).background(color, CircleShape))
            Text(label, fontSize = 13.sp, color = TextPrimary)
        }
        Text("$count", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
    }
}

private fun formatCurrency(value: Double): String {
    return "R$ %,.2f".format(value)
}
