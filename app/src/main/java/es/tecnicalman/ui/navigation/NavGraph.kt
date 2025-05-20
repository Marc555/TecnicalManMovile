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
import es.tecnicalman.ui.screen.albaran.AlbaranDetailScreen
import es.tecnicalman.ui.screen.albaran.AlbaranFormScreen
import es.tecnicalman.ui.screen.albaran.AlbaranListScreen
import es.tecnicalman.ui.screen.factura.FacturaDetailScreen
import es.tecnicalman.ui.screen.factura.FacturaFormScreen
import es.tecnicalman.ui.screen.factura.FacturaListScreen

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

        // ALBARANES
        composable("albaranes") {
            AlbaranListScreen(navController, onAlbaranClick = { albaran ->
                navController.navigate("albaranDetails/${albaran.id}")
            })
        }

        composable("albaranDetails/{albaranId}",
            arguments = listOf(navArgument("albaranId") { type = NavType.LongType })
        ) { backStackEntry ->
            val albaranId = backStackEntry.arguments?.getLong("albaranId") ?: 0L
            AlbaranDetailScreen(navController = navController, albaranId = albaranId)
        }

        // Crear un nuevo albarán
        composable("albaranForm") {
            AlbaranFormScreen(navController)
        }

        // Editar un albarán existente
        composable("albaranForm/edit/{albaranId}") {
            AlbaranFormScreen(
                navController,
                albaranId = it.arguments?.getString("albaranId")?.toLongOrNull())
        }

        // FACTURAS
        composable("facturas") {
            FacturaListScreen(navController, onFacturaClick = { factura ->
                navController.navigate("facturaDetails/${factura.id}")
            })
        }

        composable("facturaDetails/{facturaId}",
            arguments = listOf(navArgument("facturaId") { type = NavType.LongType })
        ) { backStackEntry ->
            val facturaId = backStackEntry.arguments?.getLong("facturaId") ?: 0L
            FacturaDetailScreen(navController = navController, facturaId = facturaId)
        }

        // Crear una nueva factura
        composable("facturaForm") {
            FacturaFormScreen(navController)
        }

        // Editar una factura existente
        composable("facturaForm/edit/{facturaId}") {
            FacturaFormScreen(
                navController,
                facturaId = it.arguments?.getString("facturaId")?.toLongOrNull())
        }
    }
}