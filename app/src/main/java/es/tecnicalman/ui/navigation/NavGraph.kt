package es.tecnicalman.ui.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import es.tecnicalman.ui.screen.CalendarScreen
import es.tecnicalman.ui.screen.HomeScreen
import es.tecnicalman.ui.screen.LoginScreen
import es.tecnicalman.ui.screen.SplashScreen
import es.tecnicalman.viewmodel.TareaViewModel
import java.time.LocalDate

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNavGraph(navController: NavHostController, tareaViewModel: TareaViewModel = viewModel()) {
    NavHost(navController = navController, startDestination = "splash") {
        composable("splash") {
            SplashScreen(navController)
        }
        composable("login") {
            LoginScreen(navController)
        }
        composable("home") {
            HomeScreen(navController)
        }
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
    }
}