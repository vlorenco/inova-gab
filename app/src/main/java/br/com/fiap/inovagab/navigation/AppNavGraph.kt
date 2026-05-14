package br.com.fiap.inovagab.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import br.com.fiap.inovagab.ui.gestor.GestorHomeScreen
import br.com.fiap.inovagab.ui.lideranca.LiderancaHomeScreen
import br.com.fiap.inovagab.ui.login.LoginScreen
import br.com.fiap.inovagab.ui.operador.OperadorHomeScreen

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
            OperadorHomeScreen()
        }
        composable(Routes.GESTOR_HOME) {
            GestorHomeScreen()
        }
        composable(Routes.LIDERANCA_HOME) {
            LiderancaHomeScreen()
        }
    }
}
