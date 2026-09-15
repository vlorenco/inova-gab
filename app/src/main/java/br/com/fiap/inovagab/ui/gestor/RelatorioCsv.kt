package br.com.fiap.inovagab.ui.gestor

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import br.com.fiap.inovagab.data.model.Idea
import br.com.fiap.inovagab.data.model.Project
import br.com.fiap.inovagab.data.model.RankingEntry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Geração e compartilhamento dos relatórios em CSV.
 *
 * Separador ";" e não ",": o Excel em português abre o arquivo já dividido em
 * colunas com ponto e vírgula, enquanto com vírgula ele joga a linha inteira
 * na coluna A. O mesmo vale para o decimal, que sai com vírgula.
 */
object RelatorioCsv {

    private const val SEPARATOR = ";"

    /** BOM. Sem ele o Excel lê "Logística" como "LogÃ­stica". */
    private const val BOM = "﻿"

    private val localeBr = Locale.forLanguageTag("pt-BR")

    fun ideasCsv(ideas: List<Idea>): String = buildCsv(
        header = listOf(
            "Titulo", "Area", "Status", "Prioridade", "Operador",
            "Orientacao estrategica", "Virou projeto", "Nota IA", "Recomendacao IA",
            "Problema", "Solucao", "Beneficio"
        ),
        rows = ideas.map { idea ->
            listOf(
                idea.title,
                idea.area,
                idea.status,
                idea.priority,
                idea.operatorName,
                idea.strategyTitle,
                if (idea.convertedToProject) "Sim" else "Nao",
                idea.aiAnalysis?.score?.toString().orEmpty(),
                idea.aiAnalysis?.recommendation.orEmpty(),
                idea.problem,
                idea.solution,
                idea.benefit
            )
        }
    )

    fun projectsCsv(projects: List<Project>): String = buildCsv(
        header = listOf(
            "Nome", "Status", "Etapa atual", "Responsavel", "Orientacao estrategica",
            "Investimento", "Retorno financeiro", "Lucro", "ROI (%)",
            "Reducao de custos", "Ganho de produtividade (%)", "Prazo"
        ),
        rows = projects.map { project ->
            listOf(
                project.name,
                project.status,
                project.currentStage,
                project.responsible,
                project.strategyTitle,
                decimal(project.investment),
                decimal(project.financialReturn),
                decimal(project.financialReturn - project.investment),
                decimal(project.roi),
                decimal(project.costReduction),
                decimal(project.productivityGain),
                project.deadline
            )
        }
    )

    fun rankingCsv(entries: List<RankingEntry>): String = buildCsv(
        header = listOf("Posicao", "Operador", "Pontos"),
        rows = entries.map { entry ->
            listOf(entry.position.toString(), entry.name, entry.points.toString())
        }
    )

    /**
     * Grava o CSV no cache e devolve o Intent de compartilhamento.
     *
     * O arquivo vai para cacheDir/relatorios — o único caminho que o
     * FileProvider declarado no manifesto expõe.
     */
    suspend fun share(context: Context, baseName: String, content: String): Intent =
        withContext(Dispatchers.IO) {
            val dir = File(context.cacheDir, "relatorios").apply { mkdirs() }

            // Uma geração substitui a anterior do mesmo relatório em vez de
            // encher o cache: o carimbo de data vai no conteúdo, não no nome.
            val file = File(dir, "$baseName.csv")
            file.writeText(BOM + content, Charsets.UTF_8)

            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )

            Intent(Intent.ACTION_SEND).apply {
                type = "text/csv"
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_SUBJECT, "INOVA+ — $baseName")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
        }

    /** Carimbo para o nome do arquivo: relatorio-ideias-2026-09-15. */
    fun today(): String =
        SimpleDateFormat("yyyy-MM-dd", localeBr).format(Date())

    // ── Montagem ────────────────────────────────────────────────────────────

    private fun buildCsv(header: List<String>, rows: List<List<String>>): String {
        val sb = StringBuilder()
        sb.append(header.joinToString(SEPARATOR) { escape(it) }).append("\r\n")
        rows.forEach { row ->
            sb.append(row.joinToString(SEPARATOR) { escape(it) }).append("\r\n")
        }
        return sb.toString()
    }

    /**
     * Regra do CSV: um campo que contém separador, aspas ou quebra de linha
     * vai entre aspas, e cada aspas interna é duplicada. Sem isso uma descrição
     * com ponto e vírgula desalinha todas as colunas seguintes.
     */
    private fun escape(value: String): String {
        val clean = value.replace("\r\n", " ").replace("\n", " ").replace("\r", " ")
        return if (clean.contains(SEPARATOR) || clean.contains("\"")) {
            "\"" + clean.replace("\"", "\"\"") + "\""
        } else {
            clean
        }
    }

    private fun decimal(value: Double): String = String.format(localeBr, "%.2f", value)
}
