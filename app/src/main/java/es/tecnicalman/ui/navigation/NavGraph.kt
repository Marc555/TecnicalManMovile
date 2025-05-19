package es.tecnicalman.ui.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import es.tecnicalman.ui.screen.CalendarScreen
import es.tecnicalman.ui.screen.ForgotPasswordScreen
import es.tecnicalman.ui.screen.HomeScreen
import es.tecnicalman.ui.screen.LoginScreen
import es.tecnicalman.ui.screen.SplashScreen
import es.tecnicalman.ui.screen.cliente.ClienteDetailScreen
import es.tecnicalman.ui.screen.cliente.ClienteFormScreen
import es.tecnicalman.ui.screen.cliente.ClienteListScreen
import es.tecnicalman.ui.screen.presupuesto.PresupuestoDetailScreen
import es.tecnicalman.ui.screen.presupuesto.PresupuestoFormScreen
import es.tecnicalman.ui.screen.presupuesto.PresupuestoListScreen

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "splash") {
        composable("splash") {
            SplashScreen(navController)
        }
        composable("login") {
            LoginScreen(navController)
        }
        composable("forgotPassword") {
            ForgotPasswordScreen(navController)
        }
        composable("home") {
            HomeScreen(navController)
        }

        // TAREAS
        composable("calendar") {
            CalendarScreen(navController)
        }
        composable("dayDetails/{date}") { backStackEntry ->
            val date = backStackEntry.arguments?.getString("date") ?: ""
            // Aquí implementarás la pantalla de detalles del día
            // DayDetailsScreen(date = LocalDate.parse(date))
        }
        composable("addTask") {
            // Aquí implementarás la pantalla para añadir una nueva tarea
            // AddTaskScreen()
        }

        // CLIENTES
        composable("clientes") {
            ClienteListScreen(navController, onClienteClick = { clienteId ->
                navController.navigate("clienteDetails/$clienteId")
            })
        }

        // Ruta para los detalles de un cliente
        composable("clienteDetails/{clienteId}") { backStackEntry ->
            val clienteId = backStackEntry.arguments?.getString("clienteId")?.toLong() ?: -1L
            ClienteDetailScreen(navController, clienteId = clienteId) {
                navController.popBackStack() // Regresar a la pantalla anterior
            }
        }

        // Ruta para crear un cliente
        composable("clienteForm") {
            ClienteFormScreen(navController, clienteId = null) {
                navController.popBackStack()
            }
        }

        // Ruta para editar un cliente existente
        composable("clienteForm/{clienteId}") { backStackEntry ->
            val clienteId = backStackEntry.arguments?.getString("clienteId")?.toLongOrNull()
            ClienteFormScreen(navController, clienteId = clienteId) {
                navController.popBackStack()
            }
        }


        // PRESUPUESTOS
        composable("presupuestos") {
            // Lista de presupuestos
            PresupuestoListScreen(navController, onPresupuestoClick = { presupuesto ->
                navController.navigate("presupuestoDetails/${presupuesto.id}")
            })
        }

        composable("presupuestoDetails/{presupuestoId}",
            arguments = listOf(navArgument("presupuestoId") { type = NavType.LongType })
        ) { backStackEntry ->
            val presupuestoId = backStackEntry.arguments?.getLong("presupuestoId") ?: 0L
            PresupuestoDetailScreen(navController = navController, presupuestoId = presupuestoId)
        }


        // Crear un nuevo presupuesto
        composable("presupuestoForm") {
            PresupuestoFormScreen(
                navController)
        }

        // Editar un presupuesto existente
        composable("presupuestoForm/edit/{presupuestoId}") {
            PresupuestoFormScreen(
                navController,
                presupuestoId = it.arguments?.getString("presupuestoId")?.toLongOrNull())
        }
    }
}