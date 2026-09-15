package br.com.fiap.inovagab.ui.gestor

import android.content.Intent
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.EmojiEvents
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material.icons.outlined.AccountTree
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Text
import br.com.fiap.inovagab.data.repository.IdeaRepository
import br.com.fiap.inovagab.data.repository.ProjectRepository
import br.com.fiap.inovagab.data.repository.RankingRepository
import br.com.fiap.inovagab.ui.components.InovaInlineMessage
import br.com.fiap.inovagab.ui.components.InovaPanel
import br.com.fiap.inovagab.ui.components.InovaScreen
import br.com.fiap.inovagab.ui.components.InovaTopBar
import br.com.fiap.inovagab.ui.components.InovaWideActionCard
import br.com.fiap.inovagab.ui.components.SectionHeader
import br.com.fiap.inovagab.ui.theme.InovaTextSecondary
import br.com.fiap.inovagab.ui.theme.InovaType
import kotlinx.coroutines.launch

/**
 * Exportação de dados em CSV.
 *
 * Cada item gera o arquivo na hora, a partir da API, e abre o menu de
 * compartilhamento do Android — o gestor escolhe e-mail, Drive, WhatsApp.
 * Não há download silencioso: o arquivo só sai do app se alguém mandar.
 */
@Composable
fun RelatoriosGestorScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val ideaRepository = remember { IdeaRepository() }
    val projectRepository = remember { ProjectRepository() }
    val rankingRepository = remember { RankingRepository() }

    var busy by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf<String?>(null) }
    var isError by remember { mutableStateOf(false) }

    /** Gera, grava e dispara o menu de compartilhamento. */
    fun exportar(baseName: String, build: suspend () -> Result<String>) {
        if (busy) return
        busy = true
        message = null
        scope.launch {
            build()
                .onSuccess { csv ->
                    val nome = "$baseName-${RelatorioCsv.today()}"
                    val intent = RelatorioCsv.share(context, nome, csv)
                    context.startActivity(Intent.createChooser(intent, "Enviar relatório"))
                    isError = false
                    message = "Relatório $nome.csv gerado."
                }
                .onFailure {
                    isError = true
                    message = it.message ?: "Não foi possível gerar o relatório."
                }
            busy = false
        }
    }

    InovaScreen(
        header = { InovaTopBar(title = "Relatórios", onBack = onBack) }
    ) {
        InovaPanel {
            Text(
                text = "Os arquivos saem em CSV com ponto e vírgula e acentuação " +
                        "UTF-8, prontos para abrir no Excel em português.",
                style = InovaType.bodySmall,
                color = InovaTextSecondary
            )
        }

        SectionHeader(title = "Exportar dados", trailing = "03")

        InovaWideActionCard(
            icon = Icons.Outlined.Lightbulb,
            title = "Ideias",
            subtitle = "Situação, área, operador e nota da IA",
            primary = true,
            onClick = {
                exportar("ideias") {
                    ideaRepository.getAllIdeas().map { RelatorioCsv.ideasCsv(it) }
                }
            }
        )

        InovaWideActionCard(
            icon = Icons.Outlined.AccountTree,
            title = "Projetos",
            subtitle = "Investimento, retorno, ROI e prazo",
            primary = false,
            onClick = {
                exportar("projetos") {
                    projectRepository.getProjects().map { RelatorioCsv.projectsCsv(it) }
                }
            }
        )

        InovaWideActionCard(
            icon = Icons.Outlined.EmojiEvents,
            title = "Ranking de inovadores",
            subtitle = "Posição e pontuação por operador",
            primary = false,
            onClick = {
                exportar("ranking") {
                    rankingRepository.getRanking().map { RelatorioCsv.rankingCsv(it) }
                }
            }
        )

        if (message != null) {
            Spacer(modifier = Modifier.height(4.dp))
            InovaInlineMessage(message = message!!, isError = isError)
        }

        Spacer(modifier = Modifier.height(8.dp))
    }
}
