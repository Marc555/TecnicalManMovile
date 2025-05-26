package es.tecnicalman.ui.screen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import es.tecnicalman.utils.room.DatabaseInstance
import es.tecnicalman.viewmodel.TareaViewModel
import es.tecnicalman.viewmodel.TareaViewModelFactory

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun OfflineScreen() {
    val context = LocalContext.current
    val tareaDao = remember { DatabaseInstance.getDatabase(context).tareaDao() }
    val factory = remember { TareaViewModelFactory(tareaDao, context) }
    val tareaViewModel: TareaViewModel = viewModel(factory = factory)
    val tareasHoy by tareaViewModel.tareasHoy.collectAsState()

    // Cargar tareas del día al entrar en la pantalla
    LaunchedEffect(Unit) {
        tareaViewModel.fetchTareasDeHoy()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp)
    ) {
        Text(
            text = "Sin conexión a Internet",
            style = MaterialTheme.typography.headlineMedium,
            color = Color.Red,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        )

        Text(
            text = "Estas son tus tareas del día:",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        if (tareasHoy.isEmpty()) {
            Text(
                text = "No tienes tareas para hoy.",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.Gray,
                modifier = Modifier.padding(top = 12.dp)
            )
        } else {
            // Ordenar por hora (de más temprano a más tarde)
            val tareasOrdenadas = tareasHoy.sortedBy { it.fechaHora }
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                items(tareasOrdenadas) { tarea ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(text = tarea.titulo, style = MaterialTheme.typography.titleMedium)
                            if (tarea.descripcion.isNotBlank()) {
                                Text(
                                    text = tarea.descripcion,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.Gray
                                )
                            }
                            Text(
                                text = "Dirección: ${tarea.direccion}",
                                style = MaterialTheme.typography.labelMedium
                            )
                            Text(
                                text = "Encargado: ${tarea.encargado}",
                                style = MaterialTheme.typography.labelMedium
                            )
                            Text(
                                text = "Hora: ${tarea.getFormattedFechaHora()}",
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    }
                }
            }
        }
    }
}