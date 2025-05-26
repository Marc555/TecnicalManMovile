package es.tecnicalman.ui.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import es.tecnicalman.ui.screen.*
import es.tecnicalman.ui.screen.cliente.*
import es.tecnicalman.ui.screen.presupuesto.*
import es.tecnicalman.ui.screen.albaran.*
import es.tecnicalman.ui.screen.factura.*
import es.tecnicalman.viewmodel.NetworkViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNavGraph(navController: NavHostController) {
    val networkViewModel: NetworkViewModel = viewModel()
    val isConnected by networkViewModel.isConnected.collectAsState()
    // Controla si ya navegamos a offline
    var sentToOffline by remember { mutableStateOf(false) }

    LaunchedEffect(isConnected) {
        if (!isConnected && !sentToOffline) {
            navController.navigate("offline") {
                popUpTo(0) { inclusive = true }
            }
            sentToOffline = true
        } else if (isConnected && sentToOffline) {
            navController.navigate("splash") {
                popUpTo(0) { inclusive = true }
            }
            sentToOffline = false
        }
    }

    NavHost(
        navController = navController,
        startDestination = "splash"
    ) {
        composable("offline") {
            OfflineScreen()
        }

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
        composable("clienteDetails/{clienteId}") { backStackEntry ->
            val clienteId = backStackEntry.arguments?.getString("clienteId")?.toLong() ?: -1L
            ClienteDetailScreen(navController, clienteId = clienteId) {
                navController.popBackStack()
            }
        }
        composable("clienteForm") {
            ClienteFormScreen(navController, clienteId = null) {
                navController.popBackStack()
            }
        }
        composable("clienteForm/{clienteId}") { backStackEntry ->
            val clienteId = backStackEntry.arguments?.getString("clienteId")?.toLongOrNull()
            ClienteFormScreen(navController, clienteId = clienteId) {
                navController.popBackStack()
            }
        }

        // PRESUPUESTOS
        composable("presupuestos") {
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
        composable("presupuestoForm") {
            PresupuestoFormScreen(navController)
        }
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
        composable("albaranForm") {
            AlbaranFormScreen(navController)
        }
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
        composable("facturaForm") {
            FacturaFormScreen(navController)
        }
        composable("facturaForm/edit/{facturaId}") {
            FacturaFormScreen(
                navController,
                facturaId = it.arguments?.getString("facturaId")?.toLongOrNull())
        }
    }
}