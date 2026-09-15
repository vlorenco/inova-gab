package br.com.fiap.inovagab.ui.operador

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import br.com.fiap.inovagab.data.model.Strategy
import br.com.fiap.inovagab.data.repository.IdeaRepository
import br.com.fiap.inovagab.data.repository.StrategyRepository
import br.com.fiap.inovagab.ui.components.InovaCard
import br.com.fiap.inovagab.ui.components.InovaDivider
import br.com.fiap.inovagab.ui.components.InovaFormField
import br.com.fiap.inovagab.ui.components.InovaInlineMessage
import br.com.fiap.inovagab.ui.components.InovaPickerField
import br.com.fiap.inovagab.ui.components.InovaPrimaryButton
import br.com.fiap.inovagab.ui.components.InovaScreen
import br.com.fiap.inovagab.ui.components.InovaTopBar
import br.com.fiap.inovagab.ui.theme.InovaBlueLight
import br.com.fiap.inovagab.ui.theme.InovaDurationDefault
import br.com.fiap.inovagab.ui.theme.InovaTextPrimary
import br.com.fiap.inovagab.ui.theme.InovaType
import br.com.fiap.inovagab.ui.theme.inovaTween
import kotlinx.coroutines.launch

@Composable
fun CadastroIdeiaNewScreen(onBack: () -> Unit) {
    var titulo by remember { mutableStateOf("") }
    var problema by remember { mutableStateOf("") }
    var solucao by remember { mutableStateOf("") }
    var area by remember { mutableStateOf("") }
    var beneficio by remember { mutableStateOf("") }
    var estrategia by remember { mutableStateOf<Strategy?>(null) }
    var pickerOpen by remember { mutableStateOf(false) }

    var estrategias by remember { mutableStateOf<List<Strategy>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf<String?>(null) }
    var isError by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()
    val repository = remember { IdeaRepository() }
    val strategyRepository = remember { StrategyRepository() }

    // Só orientações vigentes podem ser vinculadas a uma nova ideia.
    LaunchedEffect(Unit) {
        strategyRepository.getStrategies(activeOnly = true)
            .onSuccess { estrategias = it }
    }

    InovaScreen(
        header = { InovaTopBar(title = "Nova Ideia", onBack = onBack) }
    ) {
        InovaCard(contentPadding = PaddingValues(18.dp)) {
            InovaFormField(
                label = "Título da ideia",
                value = titulo,
                onValueChange = { titulo = it }
            )
            Spacer(modifier = Modifier.height(20.dp))
            InovaFormField(
                label = "Problema encontrado",
                value = problema,
                onValueChange = { problema = it },
                singleLine = false,
                minLines = 3
            )
            Spacer(modifier = Modifier.height(20.dp))
            InovaFormField(
                label = "Solução sugerida",
                value = solucao,
                onValueChange = { solucao = it },
                singleLine = false,
                minLines = 3
            )
            Spacer(modifier = Modifier.height(20.dp))
            InovaFormField(
                label = "Área impactada",
                value = area,
                onValueChange = { area = it }
            )
            Spacer(modifier = Modifier.height(20.dp))
            InovaFormField(
                label = "Benefício esperado",
                value = beneficio,
                onValueChange = { beneficio = it },
                singleLine = false,
                minLines = 2
            )
        }

        InovaCard(contentPadding = PaddingValues(18.dp)) {
            InovaPickerField(
                label = "Orientação estratégica",
                value = estrategia?.title ?: "Selecione uma orientação",
                expanded = pickerOpen,
                onClick = { pickerOpen = !pickerOpen }
            )

            AnimatedVisibility(
                visible = pickerOpen,
                enter = fadeIn(inovaTween(InovaDurationDefault)) +
                    expandVertically(inovaTween(InovaDurationDefault)),
                exit = fadeOut(inovaTween(InovaDurationDefault)) +
                    shrinkVertically(inovaTween(InovaDurationDefault))
            ) {
                Column(modifier = Modifier.padding(top = 14.dp)) {
                    estrategias.forEachIndexed { index, option ->
                        if (index > 0) InovaDivider()
                        StrategyOption(
                            title = option.title,
                            selected = estrategia?.id == option.id,
                            onClick = { estrategia = option; pickerOpen = false }
                        )
                    }
                }
            }
        }

        if (message != null) {
            InovaInlineMessage(message = message!!, isError = isError)
        }

        InovaPrimaryButton(
            text = "Salvar Ideia",
            enabled = !isLoading,
            isLoading = isLoading,
            onClick = {
                if (titulo.isBlank() || problema.isBlank() || solucao.isBlank()) {
                    message = "Preencha título, problema e solução."
                    isError = true
                    return@InovaPrimaryButton
                }
                // Vínculo obrigatório com a orientação vigente — o backend também
                // recusa, mas avisar aqui evita uma ida ao servidor para nada.
                val estrategiaSelecionada = estrategia
                if (estrategiaSelecionada == null) {
                    message = "Selecione a orientação estratégica da ideia."
                    isError = true
                    return@InovaPrimaryButton
                }
                coroutineScope.launch {
                    isLoading = true
                    message = null
                    repository.createIdea(
                        title = titulo.trim(),
                        problem = problema.trim(),
                        solution = solucao.trim(),
                        area = area.trim(),
                        benefit = beneficio.trim(),
                        strategyId = estrategiaSelecionada.id
                    )
                        .onSuccess {
                            message = "Ideia cadastrada com sucesso! Você ganhou 10 pontos."
                            isError = false
                            titulo = ""; problema = ""; solucao = ""; area = ""; beneficio = ""
                            estrategia = null
                        }
                        .onFailure {
                            message = it.message ?: "Erro ao salvar a ideia."
                            isError = true
                        }
                    isLoading = false
                }
            }
        )

        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
private fun StrategyOption(
    title: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    androidx.compose.foundation.layout.Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .heightIn(min = 44.dp)
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = title,
            style = InovaType.body,
            color = InovaTextPrimary,
            modifier = Modifier.weight(1f)
        )
        if (selected) {
            androidx.compose.material3.Icon(
                imageVector = Icons.Outlined.Check,
                contentDescription = null,
                tint = InovaBlueLight,
                modifier = Modifier.height(16.dp)
            )
        }
    }
}
