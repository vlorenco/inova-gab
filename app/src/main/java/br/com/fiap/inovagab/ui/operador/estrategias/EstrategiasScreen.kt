package br.com.fiap.inovagab.ui.operador.estrategias

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import br.com.fiap.inovagab.ui.operador.estrategias.model.Estrategia

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrientacoesEstrategicasScreen() {

    var busca by remember { mutableStateOf("") }

    var abaSelecionada by remember { mutableStateOf("Todas") }

    val orientacoes = listOf(

        Estrategia(
            titulo = "Redução de custos operacionais",
            descricao = "Ideias voltadas para redução de desperdícios e melhoria operacional.",
            categoria = "Operações",
            status = "Ativa"
        ),

        Estrategia(
            titulo = "Digitalização de processos",
            descricao = "Sugestões para automatizar tarefas manuais e otimizar processos.",
            categoria = "Tecnologia",
            status = "Ativa"
        ),

        Estrategia(
            titulo = "Sustentabilidade corporativa",
            descricao = "Projetos sustentáveis para redução de impacto ambiental.",
            categoria = "Sustentabilidade",
            status = "Inativa"
        ),

        Estrategia(
            titulo = "Experiência do colaborador",
            descricao = "Melhorias relacionadas ao ambiente corporativo.",
            categoria = "RH",
            status = "Ativa"
        )
    )

    val orientacoesFiltradas = orientacoes.filter {

        val correspondeBusca =
            it.titulo.contains(busca, ignoreCase = true)

        val correspondeStatus = when (abaSelecionada) {

            "Ativas" -> it.status == "Ativa"

            "Inativas" -> it.status == "Inativa"

            else -> true
        }

        correspondeBusca && correspondeStatus
    }

    Scaffold(

        containerColor = Color(0xFFF5F5F5),

        topBar = {

            TopAppBar(

                title = {

                    Text(
                        text = "Orientações Estratégicas",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                },

                navigationIcon = {

                    IconButton(onClick = {}) {

                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Voltar",
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

            OutlinedTextField(

                value = busca,

                onValueChange = {
                    busca = it
                },

                modifier = Modifier.fillMaxWidth(),

                placeholder = {
                    Text("Buscar orientação")
                },

                leadingIcon = {

                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null
                    )
                },

                shape = RoundedCornerShape(14.dp),

                colors = OutlinedTextFieldDefaults.colors(

                    focusedBorderColor = Color(0xFF003DA5),

                    unfocusedBorderColor = Color.LightGray,

                    cursorColor = Color(0xFF003DA5)
                )
            )

            Spacer(modifier = Modifier.height(18.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {

                AbaFiltro(
                    titulo = "Todas",
                    selecionada = abaSelecionada == "Todas",
                    onClick = {
                        abaSelecionada = "Todas"
                    }
                )

                AbaFiltro(
                    titulo = "Ativas",
                    selecionada = abaSelecionada == "Ativas",
                    onClick = {
                        abaSelecionada = "Ativas"
                    }
                )

                AbaFiltro(
                    titulo = "Inativas",
                    selecionada = abaSelecionada == "Inativas",
                    onClick = {
                        abaSelecionada = "Inativas"
                    }
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            LazyColumn(

                verticalArrangement = Arrangement.spacedBy(14.dp),

                contentPadding = PaddingValues(bottom = 20.dp)
            ) {

                items(orientacoesFiltradas) { estrategia ->

                    OrientacaoCard(
                        estrategia = estrategia
                    )
                }
            }
        }
    }
}

@Composable
fun AbaFiltro(
    titulo: String,
    selecionada: Boolean,
    onClick: () -> Unit
) {

    Surface(

        modifier = Modifier.clickable {
            onClick()
        },

        shape = RoundedCornerShape(20.dp),

        color = if (selecionada)
            Color(0xFF003DA5)
        else
            Color.White,

        shadowElevation = 2.dp
    ) {

        Text(

            text = titulo,

            modifier = Modifier.padding(
                horizontal = 18.dp,
                vertical = 10.dp
            ),

            color = if (selecionada)
                Color.White
            else
                Color.Gray,

            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun OrientacaoCard(
    estrategia: Estrategia
) {

    val corStatus = if (estrategia.status == "Ativa") {
        Color(0xFFDFF5E3)
    } else {
        Color(0xFFFFE0E0)
    }

    val textoStatus = if (estrategia.status == "Ativa") {
        Color(0xFF1E8E3E)
    } else {
        Color(0xFFC62828)
    }

    Card(

        modifier = Modifier.fillMaxWidth(),

        shape = RoundedCornerShape(18.dp),

        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),

        elevation = CardDefaults.cardElevation(
            defaultElevation = 3.dp
        )
    ) {

        Column(

            modifier = Modifier.padding(18.dp)
        ) {

            Row(

                modifier = Modifier.fillMaxWidth(),

                horizontalArrangement = Arrangement.SpaceBetween,

                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    text = estrategia.categoria,
                    color = Color(0xFF003DA5),
                    fontWeight = FontWeight.Bold
                )

                Surface(

                    shape = RoundedCornerShape(20.dp),

                    color = corStatus
                ) {

                    Text(

                        text = estrategia.status,

                        modifier = Modifier.padding(
                            horizontal = 12.dp,
                            vertical = 6.dp
                        ),

                        color = textoStatus,

                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = estrategia.titulo,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = estrategia.descricao,
                color = Color.Gray,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
fun OrientacoesEstrategicasScreenPreview() {

    MaterialTheme {

        Surface {

            OrientacoesEstrategicasScreen()
        }
    }
}