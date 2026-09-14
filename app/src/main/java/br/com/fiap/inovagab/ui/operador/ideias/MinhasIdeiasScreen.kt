package br.com.fiap.inovagab.ui.operador.ideias

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import br.com.fiap.inovagab.ui.operador.model.Ideia
import br.com.fiap.inovagab.ui.viewmodeloperador.IdeiasViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MinhasIdeiasScreen(
    viewModel: IdeiasViewModel = viewModel()
) {

    LaunchedEffect(Unit) {
        viewModel.carregarIdeias()
    }

    MinhasIdeiasContent(
        ideias = viewModel.ideias
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MinhasIdeiasContent(
    ideias: List<Ideia>
) {

    Scaffold(

        containerColor = Color(0xFFF5F5F5),

        topBar = {

            TopAppBar(

                title = {

                    Text(
                        text = "Minhas Ideias",
                        color = Color.White
                    )
                },

                navigationIcon = {

                    IconButton(onClick = {}) {

                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = null,
                            tint = Color.White
                        )
                    }
                },

                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF003DA5)
                )
            )
        }

    ) { paddingValues ->

        Column(

            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                StatusTab("Todas", true)
                StatusTab("Em análise", false)
                StatusTab("Aprovadas", false)
                StatusTab("Rejeitadas", false)
            }

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                items(ideias) { ideia ->

                    IdeaCard(ideia)
                }
            }
        }
    }
}

@Composable
fun StatusTab(
    text: String,
    selected: Boolean
) {

    Surface(

        color = if (selected)
            Color(0xFF003DA5)
        else
            Color.Transparent,

        shape = RoundedCornerShape(8.dp)
    ) {

        Text(

            text = text,

            modifier = Modifier.padding(
                horizontal = 12.dp,
                vertical = 8.dp
            ),

            color = if (selected)
                Color.White
            else
                Color.Gray,

            fontSize = 12.sp
        )
    }
}

@Composable
fun IdeaCard(
    ideia: Ideia
) {

    val statusColor = when (ideia.status) {

        "Aprovada" -> Color(0xFFDDF7E3)

        "Rejeitada" -> Color(0xFFFADDDD)

        else -> Color(0xFFFFF1D6)
    }

    Card(

        modifier = Modifier.fillMaxWidth(),

        shape = RoundedCornerShape(12.dp),

        elevation = CardDefaults.cardElevation(
            defaultElevation = 3.dp
        )
    ) {

        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            Row(

                modifier = Modifier.fillMaxWidth(),

                horizontalArrangement = Arrangement.SpaceBetween,

                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(

                    text = ideia.titulo,

                    fontWeight = FontWeight.Bold,

                    fontSize = 16.sp,

                    modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Surface(

                    color = statusColor,

                    shape = RoundedCornerShape(20.dp)
                ) {

                    Text(

                        text = ideia.status,

                        modifier = Modifier.padding(
                            horizontal = 12.dp,
                            vertical = 6.dp
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Área: ${ideia.area}",
                color = Color.Gray
            )
        }
    }
}

@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
fun MinhasIdeiasPreview() {

    val ideiasFake = listOf(

        Ideia(
            titulo = "Reduzir tempo no embarque",
            area = "Operações",
            status = "Em análise"
        ),

        Ideia(
            titulo = "Checklist digital para manutenção",
            area = "Manutenção",
            status = "Aprovada"
        ),

        Ideia(
            titulo = "Padronizar limpeza dos ônibus",
            area = "Manutenção",
            status = "Rejeitada"
        )
    )

    MinhasIdeiasContent(
        ideias = ideiasFake
    )
}