package br.com.fiap.inovagab.navigation

object Routes {
    const val SPLASH = "splash"
    const val LOGIN = "login"
    const val OPERADOR_HOME = "operador_home"
    const val GESTOR_HOME = "gestor_home"
    const val LIDERANCA_HOME = "lideranca_home"
    const val PROFILE = "profile"

    // Fluxo Operador
    const val OPERADOR_CADASTRAR_IDEIA = "operador_cadastrar_ideia"
    const val OPERADOR_MINHAS_IDEIAS = "operador_minhas_ideias"
    const val OPERADOR_ORIENTACOES = "operador_orientacoes"
    const val OPERADOR_RANKING = "operador_ranking"

    // Fluxo Gestor
    const val GESTOR_IDEIAS = "gestor_ideias"
    const val GESTOR_DETALHE_IDEIA = "gestor_detalhe_ideia/{ideaId}"
    const val GESTOR_PROJETOS = "gestor_projetos"
    const val GESTOR_NOVO_PROJETO = "gestor_novo_projeto"
    const val GESTOR_NOVO_PROJETO_IDEIA = "gestor_novo_projeto/{ideaId}"
    const val GESTOR_EDITAR_PROJETO = "gestor_editar_projeto/{projectId}"
    const val GESTOR_RELATORIOS = "gestor_relatorios"

    // Fluxo Liderança
    const val LIDERANCA_STRATEGIES = "lideranca_strategies"
    const val LIDERANCA_PROJECTS = "lideranca_projects"
    const val LIDERANCA_INDICADORES = "lideranca_indicadores"
}
