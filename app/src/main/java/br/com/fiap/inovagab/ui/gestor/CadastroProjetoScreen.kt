package br.com.fiap.inovagab.ui.gestor

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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import br.com.fiap.inovagab.ui.components.AppTopBar
import br.com.fiap.inovagab.ui.gestor.model.Projeto
import br.com.fiap.inovagab.ui.theme.DangerRed
import br.com.fiap.inovagab.ui.theme.LightBackground
import br.com.fiap.inovagab.ui.theme.PrimaryBlue
import br.com.fiap.inovagab.ui.theme.TextSecondary

@Composable
fun CadastroProjetoScreen(
    viewModel: GestorViewModel = viewModel(),
    ideiaId: String = "",
    ideiaTitulo: String = "",
    onProjetoSalvo: () -> Unit = {}
) {
    var titulo by remember { mutableStateOf(ideiaTitulo) }
    var descricao by remember { mutableStateOf("") }
    var responsavel by remember { mutableStateOf("") }
    var roiEstimado by remember { mutableStateOf("") }
    var reducaoCusto by remember { mutableStateOf("") }
    var erroTitulo by remember { mutableStateOf(false) }
    var erroDescricao by remember { mutableStateOf(false) }
    var erroResponsavel by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Cadastrar projeto",
                subtitle = "Transforme uma ideia aprovada em projeto"
            )
        },
        containerColor = LightBackground
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(top = 20.dp, bottom = 32.dp)
        ) {
            item {
                CampoTexto(
                    label = "Título do projeto",
                    value = titulo,
                    onValueChange = { titulo = it; erroTitulo = false },
                    isError = erroTitulo,
                    errorText = "Título obrigatório"
                )
            }
            item {
                CampoTexto(
                    label = "Descrição",
                    value = descricao,
                    onValueChange = { descricao = it; erroDescricao = false },
                    isError = erroDescricao,
                    errorText = "Descrição obrigatória",
                    minLines = 3
                )
            }
            item {
                CampoTexto(
                    label = "Responsável",
                    value = responsavel,
                    onValueChange = { responsavel = it; erroResponsavel = false },
                    isError = erroResponsavel,
                    errorText = "Responsável obrigatório"
                )
            }
            item {
                CampoTexto(
                    label = "ROI estimado (R$)",
                    value = roiEstimado,
                    onValueChange = { roiEstimado = it },
                    keyboardType = KeyboardType.Decimal
                )
            }
            item {
                CampoTexto(
                    label = "Redução de custo estimada (R$)",
                    value = reducaoCusto,
                    onValueChange = { reducaoCusto = it },
                    keyboardType = KeyboardType.Decimal
                )
            }
            item {
                Button(
                    onClick = {
                        erroTitulo = titulo.isBlank()
                        erroDescricao = descricao.isBlank()
                        erroResponsavel = responsavel.isBlank()
                        if (erroTitulo || erroDescricao || erroResponsavel) return@Button

                        val projeto = Projeto(
                            ideiaId = ideiaId,
                            titulo = titulo.trim(),
                            descricao = descricao.trim(),
                            responsavel = responsavel.trim(),
                            status = "Criado",
                            roiEstimado = roiEstimado.toDoubleOrNull() ?: 0.0,
                            reducaoCustoEstimada = reducaoCusto.toDoubleOrNull() ?: 0.0
                        )
                        viewModel.salvarProjeto(projeto)
                        onProjetoSalvo()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PrimaryBlue,
                        contentColor = Color.White
                    )
                ) {
                    Text("Salvar projeto", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                }
            }
        }
    }
}

@Composable
private fun CampoTexto(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    isError: Boolean = false,
    errorText: String = "",
    minLines: Int = 1,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    Column {
        Text(
            text = label,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = if (isError) DangerRed else TextSecondary
        )
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            isError = isError,
            minLines = minLines,
            singleLine = minLines == 1,
            shape = RoundedCornerShape(12.dp),
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PrimaryBlue,
                unfocusedBorderColor = Color(0xFFE2E8F0),
                errorBorderColor = DangerRed
            )
        )
        if (isError && errorText.isNotBlank()) {
            Text(
                text = errorText,
                color = DangerRed,
                fontSize = 11.sp,
                modifier = Modifier.padding(start = 4.dp, top = 2.dp)
            )
        }
    }
}
