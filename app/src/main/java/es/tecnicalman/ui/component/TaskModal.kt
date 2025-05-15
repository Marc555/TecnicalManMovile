package es.tecnicalman.ui.component

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import es.tecnicalman.model.Tarea
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.*

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TaskModal(
    date: LocalDate,
    tasks: List<Tarea>,
    onClose: () -> Unit,
    onEditTask: (Tarea) -> Unit,
    onDeleteTask: (Tarea) -> Unit
) {
    Dialog(onDismissRequest = onClose) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium,
            elevation = 8.dp
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Tareas para el ${date.dayOfMonth} " +
                            "${date.month.getDisplayName(TextStyle.FULL, Locale.getDefault())} " +
                            "${date.year}",
                    style = MaterialTheme.typography.h6
                )

                Spacer(modifier = Modifier.height(16.dp))

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f) // Asegura que el LazyColumn ocupe el espacio disponible para ser "scrolleable"
                ) {
                    if (tasks.isEmpty()) {
                        item {
                            Text("No hay tareas para este día", Modifier.padding(16.dp))
                        }
                    } else {
                        items(tasks) { task ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp)
                            ) {
                                Column(Modifier.padding(16.dp)) {
                                    Text(task.titulo, style = MaterialTheme.typography.h6)
                                    Text("Fecha: ${task.getFormattedFechaHora()}")
                                    Text("Descripcoon: ${task.descripcion}")
                                    Text("Encargado: ${task.encargado}")
                                    Text("Estado: ${task.estado}")
                                    Text("Dirección: ${task.direccion}")
                                    Row(horizontalArrangement = Arrangement.SpaceBetween) {
                                        TextButton(onClick = {
                                            onClose()
                                            onEditTask(task)
                                        }) {
                                            Text("Editar")
                                        }
                                        TextButton(onClick = { onDeleteTask(task) }) {
                                            Text("Eliminar", color = MaterialTheme.colors.error)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(onClick = onClose, Modifier.align(Alignment.CenterHorizontally)) {
                    Text("Cerrar")
                }
            }
        }
    }
}