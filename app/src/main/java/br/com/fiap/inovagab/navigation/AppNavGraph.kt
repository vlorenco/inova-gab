package br.com.fiap.inovagab.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import br.com.fiap.inovagab.ui.gestor.GestorHomeScreen
import br.com.fiap.inovagab.ui.lideranca.DashboardScreen
import br.com.fiap.inovagab.ui.lideranca.LeaderProjectsScreen
import br.com.fiap.inovagab.ui.lideranca.LiderancaHomeScreen
import br.com.fiap.inovagab.ui.lideranca.StrategiesScreen
import br.com.fiap.inovagab.ui.login.LoginScreen
import br.com.fiap.inovagab.ui.operador.OperadorHomeScreen
import br.com.fiap.inovagab.ui.profile.ProfileScreen

@Composable
fun AppNavGraph() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Routes.LOGIN) {
        composable(Routes.LOGIN) {
            LoginScreen(
                onLoginSuccess = { role ->
                    val destination = when (role) {
                        "OPERADOR" -> Routes.OPERADOR_HOME
                        "GESTOR" -> Routes.GESTOR_HOME
                        "LIDERANCA" -> Routes.LIDERANCA_HOME
                        else -> {
                            android.util.Log.e("AppNavGraph", "Role desconhecido recebido: '$role'")
                            return@LoginScreen
                        }
                    }
                    navController.navigate(destination) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                }
            )
        }
        composable(Routes.OPERADOR_HOME) {
            OperadorHomeScreen(
                onProfileClick = { navController.navigate(Routes.PROFILE) }
            )
        }
        composable(Routes.GESTOR_HOME) {
            GestorHomeScreen(
                onProfileClick = { navController.navigate(Routes.PROFILE) }
            )
        }
        composable(Routes.LIDERANCA_HOME) {
            LiderancaHomeScreen(
                onProfileClick = { navController.navigate(Routes.PROFILE) },
                onStrategiesClick = { navController.navigate(Routes.LIDERANCA_STRATEGIES) },
                onDashboardClick = { navController.navigate(Routes.LIDERANCA_DASHBOARD) },
                onProjectsClick = { navController.navigate(Routes.LIDERANCA_PROJECTS) }
            )
        }
        composable(Routes.LIDERANCA_STRATEGIES) {
            StrategiesScreen(
                onBack = { navController.popBackStack() }
            )
        }
        composable(Routes.LIDERANCA_DASHBOARD) {
            DashboardScreen(
                onBack = { navController.popBackStack() }
            )
        }
        composable(Routes.LIDERANCA_PROJECTS) {
            LeaderProjectsScreen(
                onBackClick = { navController.popBackStack() }
            )
        }
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
