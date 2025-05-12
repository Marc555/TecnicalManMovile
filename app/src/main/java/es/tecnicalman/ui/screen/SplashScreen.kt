package es.tecnicalman.ui.screen

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
import es.tecnicalman.util.TokenManager
import es.tecnicalman.viewmodel.AuthViewModel

@Composable
fun SplashScreen(
    navController: NavController,
    authViewModel: AuthViewModel = viewModel()
) {
    val activityContext = LocalContext.current.applicationContext

    LaunchedEffect(Unit) {
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

    Box(
        Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}