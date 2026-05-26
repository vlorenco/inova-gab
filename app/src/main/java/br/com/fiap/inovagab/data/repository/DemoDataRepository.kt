package br.com.fiap.inovagab.data.repository

import br.com.fiap.inovagab.data.model.Idea
import br.com.fiap.inovagab.data.model.Project
import br.com.fiap.inovagab.data.model.Strategy
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class DemoDataRepository {

    private val db = FirebaseFirestore.getInstance()

    suspend fun seedDemoDataIfNeeded(): Result<String> = runCatching {
        val strategiesSnap = db.collection("strategies").limit(1).get().await()
        val ideasSnap = db.collection("ideas").limit(1).get().await()
        val projectsSnap = db.collection("projects").limit(1).get().await()

        if (strategiesSnap.documents.isNotEmpty() ||
            ideasSnap.documents.isNotEmpty() ||
            projectsSnap.documents.isNotEmpty()
        ) {
            return@runCatching "Dados de demonstração já existem."
        }

        // Strategies
        val strategies = listOf(
            Strategy(
                title = "Digitalização da operação logística",
                description = "Incentivar soluções que reduzam processos manuais e aumentem a eficiência operacional.",
                date = "01/01/2025",
                isActive = true
            ),
            Strategy(
                title = "Eficiência operacional e redução de custos",
                description = "Buscar ideias que reduzam desperdícios, retrabalho e tempo de execução.",
                date = "01/01/2025",
                isActive = true
            ),
            Strategy(
                title = "Segurança e experiência do colaborador",
                description = "Promover iniciativas que melhorem a segurança, comunicação e rotina dos operadores.",
                date = "01/01/2025",
                isActive = true
            ),
            Strategy(
                title = "Sustentabilidade nas unidades",
                description = "Reduzir consumo de papel, energia e recursos operacionais.",
                date = "01/01/2025",
                isActive = false
            )
        )
        strategies.forEach { db.collection("strategies").add(it).await() }

        // Ideas
        val ideas = listOf(
            Idea(
                title = "Reduzir tempo de conferência de cargas",
                problem = "A conferência manual gera filas, atrasos e retrabalho.",
                solution = "Criar checklist digital com leitura por QR Code.",
                area = "Logística",
                benefit = "Redução do tempo de conferência e aumento da produtividade.",
                status = "EM_ANALISE",
                priority = "NORMAL",
                operatorId = "demo_operator_1",
                operatorName = "Operador Demo"
            ),
            Idea(
                title = "Checklist digital para manutenção",
                problem = "As inspeções preventivas ainda são registradas manualmente.",
                solution = "Criar formulário digital para registrar itens de manutenção.",
                area = "Manutenção",
                benefit = "Melhor controle preventivo e redução de falhas operacionais.",
                status = "PRIORIZADA",
                priority = "ALTA",
                operatorId = "demo_operator_1",
                operatorName = "Operador Demo"
            ),
            Idea(
                title = "Comunicação rápida entre motoristas e operação",
                problem = "A comunicação sobre ocorrências demora para chegar ao gestor.",
                solution = "Criar canal de registro rápido dentro do app.",
                area = "Operações",
                benefit = "Resposta mais rápida e melhor tomada de decisão.",
                status = "APROVADA",
                priority = "ALTA",
                operatorId = "demo_operator_2",
                operatorName = "Operador 2",
                convertedToProject = true
            ),
            Idea(
                title = "Redução de papel nos processos internos",
                problem = "Muitos processos ainda usam formulários impressos.",
                solution = "Digitalizar registros operacionais recorrentes.",
                area = "Administrativo",
                benefit = "Redução de custo e ganho de rastreabilidade.",
                status = "REJEITADA",
                priority = "BAIXA",
                operatorId = "demo_operator_1",
                operatorName = "Operador Demo"
            ),
            Idea(
                title = "Otimização de rotas com dados históricos",
                problem = "Algumas rotas apresentam atrasos recorrentes.",
                solution = "Usar dados históricos para sugerir melhores horários e rotas.",
                area = "Transporte",
                benefit = "Redução de atrasos e melhor aproveitamento dos veículos.",
                status = "EM_ANALISE",
                priority = "NORMAL",
                operatorId = "demo_operator_2",
                operatorName = "Operador 2"
            )
        )
        ideas.forEach { db.collection("ideas").add(it).await() }

        // Projects
        val projects = listOf(
            Project(
                name = "Digitalização da conferência operacional",
                description = "Implantação de checklist digital para reduzir tempo de conferência.",
                status = "EM_ANDAMENTO",
                currentStage = "Piloto em unidade operacional",
                investment = 10000.0,
                financialReturn = 25000.0,
                costReduction = 5000.0,
                productivityGain = 18.0,
                deadline = "30/06/2026"
            ),
            Project(
                name = "Painel de produtividade por unidade",
                description = "Dashboard para acompanhar produtividade e gargalos operacionais.",
                status = "PLANEJADO",
                currentStage = "Planejamento",
                investment = 15000.0,
                financialReturn = 32000.0,
                costReduction = 7000.0,
                productivityGain = 22.0,
                deadline = "15/07/2026"
            ),
            Project(
                name = "Automação de alertas de manutenção",
                description = "Alertas preventivos para manutenção de veículos e equipamentos.",
                status = "CONCLUIDO",
                currentStage = "Concluído",
                investment = 8000.0,
                financialReturn = 21000.0,
                costReduction = 6000.0,
                productivityGain = 15.0,
                deadline = "01/06/2026"
            )
        )
        projects.forEach { db.collection("projects").add(it).await() }

        "Dados de demonstração criados com sucesso!"
    }
}
