package es.tecnicalman.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import es.tecnicalman.util.TokenManager
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(navController: NavController) {
    Box(modifier = Modifier.fillMaxSize()) {
        // Contenido principal
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp),
            verticalArrangement = Arrangement.Bottom
        ) {
            // Primera fila de botones
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                BotonHomeMenu(
                    text = "Clientes",
                    modifier = Modifier.weight(1f),
                    onClick = { }
                )
                BotonHomeMenu(
                    text = "Presupuestos",
                    modifier = Modifier.weight(1f),
                    onClick = { }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Segunda fila de botones
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                BotonHomeMenu(
                    text = "Albaranes",
                    modifier = Modifier.weight(1f),
                    onClick = { }
                )
                BotonHomeMenu(
                    text = "Facturas",
                    modifier = Modifier.weight(1f),
                    onClick = { }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Botón ancho inferior
            BotonHomeMenu(
                text = "Tareas",
                isWide = true,
                onClick = { navController.navigate("calendar") }
            )

            Spacer(modifier = Modifier.height(16.dp))
        }

        // Barra superior con botón de cerrar sesión
        TopBarHome(navController)
    }
}

@Composable
fun TopBarHome(navController: NavController) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(73.dp)
            .background(MaterialTheme.colorScheme.surface)
            .padding(16.dp),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.Bottom
    ) {
        Text(
            text = "Cerrar sesión",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier
                .clickable {
                    lifecycleOwner.lifecycleScope.launch {
                        TokenManager.clearToken(context)
                        navController.navigate("login") {
                            popUpTo("home") { inclusive = true }
                        }
                    }
                }
        )
    }
}

@Composable
fun BotonHomeMenu(
    text: String,
    isWide: Boolean = false,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val aspectRatio = if (isWide) 3.5f else 1f

    Card(
        modifier = modifier
            .aspectRatio(aspectRatio)
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}