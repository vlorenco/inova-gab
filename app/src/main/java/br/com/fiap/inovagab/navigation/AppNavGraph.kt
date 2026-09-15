package br.com.fiap.inovagab.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import br.com.fiap.inovagab.ui.gestor.DetalheIdeiaScreen
import br.com.fiap.inovagab.ui.gestor.GestorHomeScreen
import br.com.fiap.inovagab.ui.gestor.IdeiasGestorNewScreen
import br.com.fiap.inovagab.ui.gestor.NovoProjetoScreen
import br.com.fiap.inovagab.ui.gestor.ProjetosGestorNewScreen
import br.com.fiap.inovagab.ui.gestor.RelatoriosGestorScreen
import br.com.fiap.inovagab.ui.lideranca.IndicadoresScreen
import br.com.fiap.inovagab.ui.lideranca.LeaderProjectsScreen
import br.com.fiap.inovagab.ui.lideranca.LiderancaHomeScreen
import br.com.fiap.inovagab.ui.lideranca.StrategiesScreen
import br.com.fiap.inovagab.ui.login.LoginScreen
import br.com.fiap.inovagab.ui.operador.CadastroIdeiaNewScreen
import br.com.fiap.inovagab.ui.operador.MinhasIdeiasNewScreen
import br.com.fiap.inovagab.ui.operador.OperadorHomeScreen
import br.com.fiap.inovagab.ui.operador.OrientacoesOperadorScreen
import br.com.fiap.inovagab.ui.operador.RankingScreen
import br.com.fiap.inovagab.ui.profile.ProfileScreen
import br.com.fiap.inovagab.ui.splash.SplashScreen

@Composable
fun AppNavGraph() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Routes.SPLASH) {

        // ── Splash ─────────────────────────────────────────────────────────
        composable(Routes.SPLASH) {
            SplashScreen(
                onFinished = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.SPLASH) { inclusive = true }
                    }
                }
            )
        }

        // ── Login ──────────────────────────────────────────────────────────
        composable(Routes.LOGIN) {
            LoginScreen(
                onLoginSuccess = { role ->
                    val destination = when (role) {
                        "OPERADOR" -> Routes.OPERADOR_HOME
                        "GESTOR" -> Routes.GESTOR_HOME
                        "LIDERANCA" -> Routes.LIDERANCA_HOME
                        else -> {
                            android.util.Log.e("AppNavGraph", "Role desconhecido: '$role'")
                            return@LoginScreen
                        }
                    }
                    navController.navigate(destination) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                }
            )
        }

        // ── Operador ───────────────────────────────────────────────────────
        composable(Routes.OPERADOR_HOME) {
            OperadorHomeScreen(
                onProfileClick = { navController.navigate(Routes.PROFILE) },
                onCadastrarIdeiaClick = { navController.navigate(Routes.OPERADOR_CADASTRAR_IDEIA) },
                onMinhasIdeiasClick = { navController.navigate(Routes.OPERADOR_MINHAS_IDEIAS) },
                onOrientacoesClick = { navController.navigate(Routes.OPERADOR_ORIENTACOES) },
                onRankingClick = { navController.navigate(Routes.OPERADOR_RANKING) }
            )
        }
        composable(Routes.OPERADOR_CADASTRAR_IDEIA) {
            CadastroIdeiaNewScreen(onBack = { navController.popBackStack() })
        }
        composable(Routes.OPERADOR_MINHAS_IDEIAS) {
            MinhasIdeiasNewScreen(
                onBack = { navController.popBackStack() },
                onNovaIdeia = { navController.navigate(Routes.OPERADOR_CADASTRAR_IDEIA) }
            )
        }
        composable(Routes.OPERADOR_ORIENTACOES) {
            OrientacoesOperadorScreen(onBack = { navController.popBackStack() })
        }
        composable(Routes.OPERADOR_RANKING) {
            RankingScreen(onBack = { navController.popBackStack() })
        }

        // ── Gestor ─────────────────────────────────────────────────────────
        composable(Routes.GESTOR_HOME) {
            GestorHomeScreen(
                onProfileClick = { navController.navigate(Routes.PROFILE) },
                onIdeiasClick = { navController.navigate(Routes.GESTOR_IDEIAS) },
                onProjetosClick = { navController.navigate(Routes.GESTOR_PROJETOS) },
                onRelatoriosClick = { navController.navigate(Routes.GESTOR_RELATORIOS) }
            )
        }
        composable(Routes.GESTOR_IDEIAS) {
            IdeiasGestorNewScreen(
                onBack = { navController.popBackStack() },
                onIdeiaClick = { ideaId ->
                    navController.navigate("gestor_detalhe_ideia/$ideaId")
                }
            )
        }
        composable(
            route = Routes.GESTOR_DETALHE_IDEIA,
            arguments = listOf(navArgument("ideaId") { type = NavType.StringType })
        ) { backStackEntry ->
            val ideaId = backStackEntry.arguments?.getString("ideaId") ?: ""
            DetalheIdeiaScreen(
                ideaId = ideaId,
                onBack = { navController.popBackStack() },
                onCriarProjeto = { id ->
                    navController.navigate("gestor_novo_projeto/$id")
                }
            )
        }
        composable(Routes.GESTOR_PROJETOS) {
            ProjetosGestorNewScreen(
                onBack = { navController.popBackStack() },
                onNovoProjeto = { navController.navigate(Routes.GESTOR_NOVO_PROJETO) },
                onEditarProjeto = { projectId ->
                    navController.navigate("gestor_editar_projeto/$projectId")
                }
            )
        }
        composable(Routes.GESTOR_NOVO_PROJETO) {
            NovoProjetoScreen(onBack = { navController.popBackStack() })
        }
        composable(
            route = Routes.GESTOR_NOVO_PROJETO_IDEIA,
            arguments = listOf(navArgument("ideaId") { type = NavType.StringType })
        ) { backStackEntry ->
            val ideaId = backStackEntry.arguments?.getString("ideaId") ?: ""
            NovoProjetoScreen(ideaId = ideaId, onBack = { navController.popBackStack() })
        }
        composable(
            route = Routes.GESTOR_EDITAR_PROJETO,
            arguments = listOf(navArgument("projectId") { type = NavType.StringType })
        ) { backStackEntry ->
            val projectId = backStackEntry.arguments?.getString("projectId") ?: ""
            NovoProjetoScreen(projectId = projectId, onBack = { navController.popBackStack() })
        }

        composable(Routes.GESTOR_RELATORIOS) {
            RelatoriosGestorScreen(onBack = { navController.popBackStack() })
        }

        // ── Liderança ──────────────────────────────────────────────────────
        composable(Routes.LIDERANCA_HOME) {
            LiderancaHomeScreen(
                onProfileClick = { navController.navigate(Routes.PROFILE) },
                onStrategiesClick = { navController.navigate(Routes.LIDERANCA_STRATEGIES) },
                onProjectsClick = { navController.navigate(Routes.LIDERANCA_PROJECTS) },
                onIndicatorsClick = { navController.navigate(Routes.LIDERANCA_INDICADORES) }
            )
        }
        composable(Routes.LIDERANCA_INDICADORES) {
            IndicadoresScreen(onBack = { navController.popBackStack() })
        }
        composable(Routes.LIDERANCA_STRATEGIES) {
            StrategiesScreen(onBack = { navController.popBackStack() })
        }
        composable(Routes.LIDERANCA_PROJECTS) {
            LeaderProjectsScreen(onBackClick = { navController.popBackStack() })
        }

        // ── Perfil ─────────────────────────────────────────────────────────
        composable(Routes.PROFILE) {
            ProfileScreen(
                onLogout = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
    }
}
