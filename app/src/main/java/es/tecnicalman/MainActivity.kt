package es.tecnicalman

import android.content.pm.ActivityInfo
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.navigation.compose.rememberNavController
import es.tecnicalman.ui.navigation.AppNavGraph
import es.tecnicalman.ui.theme.TecnicalmanTheme
import es.tecnicalman.util.TokenManager // <- Asegúrate de importar esto

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        TokenManager.init(this)

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        setContent {
            TecnicalmanTheme(darkTheme = false) {
                val navController = rememberNavController()
                AppNavGraph(navController = navController)
            }
        }
    }
}
