package br.com.fiap.inovagab.ui.operador.ideias

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import br.com.fiap.inovagab.ui.operador.model.Ideia
import br.com.fiap.inovagab.ui.viewmodeloperador.IdeiasViewModel

@Composable
fun CadastroIdeiaScreen(
    viewModel: IdeiasViewModel
) {

    CadastroIdeiaContent(
        onSalvarIdeia = { ideia ->
            viewModel.salvarIdeia(ideia)
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CadastroIdeiaContent(
    onSalvarIdeia: (Ideia) -> Unit
) {

    var titulo by remember { mutableStateOf("") }

    var problema by remember { mutableStateOf("") }

    var solucao by remember { mutableStateOf("") }

    var area by remember { mutableStateOf("") }

    var beneficio by remember { mutableStateOf("") }

    var categoria by remember { mutableStateOf("") }

    Scaffold(

        containerColor = Color(0xFFF5F5F5),

        topBar = {

            TopAppBar(

                title = {

                    Text(
                        text = "Nova Ideia",
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold
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
        },

        bottomBar = {

            Surface(
                tonalElevation = 8.dp,
                shadowElevation = 8.dp
            ) {

                Button(

                    onClick = {

                        val novaIdeia = Ideia(
                            titulo = titulo,
                            problema = problema,
                            solucao = solucao,
                            area = area,
                            beneficio = beneficio,
                            categoria = categoria,
                            status = "Em análise"
                        )

                        onSalvarIdeia(novaIdeia)
                    },

                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .height(56.dp),

                    shape = RoundedCornerShape(14.dp),

                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF003DA5)
                    )
                ) {

                    Text(
                        text = "Adicionar Ideia",
                        color = Color.White,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        }

    ) { paddingValues ->

        LazyColumn(

            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),

            verticalArrangement = Arrangement.spacedBy(16.dp),

            contentPadding = PaddingValues(
                top = 16.dp,
                bottom = 140.dp
            )
        ) {

            item {

                CadastroTextField(
                    value = titulo,
                    onValueChange = { titulo = it },
                    titulo = "Título da ideia",
                    placeholder = "Digite o título da sua ideia"
                )
            }

            item {

                CadastroTextField(
                    value = problema,
                    onValueChange = { problema = it },
                    titulo = "Problema encontrado",
                    placeholder = "Descreva o problema identificado",
                    minLines = 3
                )
            }

            item {

                CadastroTextField(
                    value = solucao,
                    onValueChange = { solucao = it },
                    titulo = "Solução sugerida",
                    placeholder = "Explique sua solução",
                    minLines = 3
                )
            }

            item {

                CadastroTextField(
                    value = area,
                    onValueChange = { area = it },
                    titulo = "Área impactada",
                    placeholder = "Ex: Operações, RH, Financeiro"
                )
            }

            item {

                CadastroTextField(
                    value = beneficio,
                    onValueChange = { beneficio = it },
                    titulo = "Benefício esperado",
                    placeholder = "Descreva os benefícios da ideia",
                    minLines = 2
                )
            }

            item {

                CadastroTextField(
                    value = categoria,
                    onValueChange = { categoria = it },
                    titulo = "Categoria",
                    placeholder = "Ex: Inovação, Processos"
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CadastroTextField(
    value: String,
    onValueChange: (String) -> Unit,
    titulo: String,
    placeholder: String,
    minLines: Int = 1
) {

    Column {

        Text(
            text = titulo,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF1C1C1C)
        )

        Spacer(modifier = Modifier.height(6.dp))

        OutlinedTextField(

            value = value,

            onValueChange = onValueChange,

            placeholder = {

                Text(
                    text = placeholder,
                    color = Color.Gray
                )
            },

            modifier = Modifier.fillMaxWidth(),

            minLines = minLines,

            shape = RoundedCornerShape(12.dp),

            colors = OutlinedTextFieldDefaults.colors(

                focusedBorderColor = Color(0xFF003DA5),

                unfocusedBorderColor = Color.LightGray,

                cursorColor = Color(0xFF003DA5)
            )
        )
    }
}

@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
fun CadastroIdeiaPreview() {

    MaterialTheme {

        Surface {

            CadastroIdeiaContent(
                onSalvarIdeia = {}
            )
        }
    }
}