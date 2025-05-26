package es.tecnicalman.ui.screen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import es.tecnicalman.utils.TokenManager
import es.tecnicalman.viewmodel.AuthViewModel
import es.tecnicalman.viewmodel.NetworkViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SplashScreen(
    navController: NavController,
    authViewModel: AuthViewModel = viewModel(),
    networkViewModel: NetworkViewModel = viewModel()
) {
    val activityContext = LocalContext.current.applicationContext
    val isConnected by networkViewModel.isConnected.collectAsState()

    // Si no hay conexión, navega a la pantalla offline inmediatamente
    LaunchedEffect(isConnected) {
        if (!isConnected) {
            navController.navigate("offline") {
                popUpTo("splash") { inclusive = true }
            }
        }
    }

    // Solo ejecuta la lógica de login si hay conexión
    LaunchedEffect(isConnected) {
        if (isConnected) {
            // 1. Lee el token directamente desde DataStore y haz log
            val token = TokenManager.getToken(activityContext)
            android.util.Log.d("SplashDebug", "Token leído: $token")

            // 2. Valida
            authViewModel.validateToken(activityContext) { isValid ->
                android.util.Log.d("SplashDebug", "¿Token válido? $isValid")
                navController.navigate(if (isValid) "home" else "login") {
                    popUpTo("splash") { inclusive = true }
                }
            }
        }
    }

    Box(
        Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}