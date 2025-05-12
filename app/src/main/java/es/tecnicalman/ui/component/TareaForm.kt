package es.tecnicalman.ui.component

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import es.tecnicalman.model.Tarea
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TareaForm(
    tarea: Tarea?,
    onClose: () -> Unit,
    onSave: (Tarea) -> Unit
) {
    // Campos del formulario
    var titulo by remember { mutableStateOf(tarea?.titulo ?: "") }
    var descripcion by remember { mutableStateOf(tarea?.descripcion ?: "") }
    var encargado by remember { mutableStateOf(tarea?.encargado ?: "AMBOS") }
    var direccion by remember { mutableStateOf(tarea?.direccion ?: "") }
    var estado by remember { mutableStateOf(tarea?.estado ?: "PENDIENTE") }
    var fechaHora by remember {
        mutableStateOf(
            tarea?.fechaHora?.let {
                LocalDateTime.ofEpochSecond(it.toLong(), 0, ZoneId.systemDefault().rules.getOffset(LocalDateTime.now()))
            } ?: LocalDateTime.now()
        )
    }

    // Opciones para "Encargado" y "Estado"
    val encargadoOptions = listOf("JAIME", "PABLO", "AMBOS")
    val estadoOptions = listOf("PENDIENTE", "EN_PROGRESO", "COMPLETADA", "CANCELADA")

    // Convertidores para mostrar fecha y hora
    val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

    // Estados para mostrar los diálogos
    val context = LocalContext.current
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }

    // Estados para los menús desplegables
    var encargadoExpanded by remember { mutableStateOf(false) }
    var estadoExpanded by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xAA000000)), // Fondo semitransparente para evitar interacción con el fondo
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .padding(16.dp),
            backgroundColor = MaterialTheme.colors.surface,
            elevation = 8.dp
        ) {
            Column(Modifier.padding(16.dp)) {
                TextField(
                    value = titulo,
                    onValueChange = { titulo = it },
                    label = { Text("Título") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                TextField(
                    value = descripcion,
                    onValueChange = { descripcion = it },
                    label = { Text("Descripción") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                // Menú desplegable para "Encargado"
                Box {
                    OutlinedTextField(
                        value = encargado,
                        onValueChange = {},
                        label = { Text("Encargado") },
                        modifier = Modifier.fillMaxWidth(),
                        readOnly = true,
                        trailingIcon = {
                            IconButton(onClick = { encargadoExpanded = true }) {
                                Icon(Icons.Default.ArrowDropDown, contentDescription = "Abrir menú")
                            }
                        }
                    )
                    DropdownMenu(
                        expanded = encargadoExpanded,
                        onDismissRequest = { encargadoExpanded = false }
                    ) {
                        encargadoOptions.forEach { option ->
                            DropdownMenuItem(onClick = {
                                encargado = option
                                encargadoExpanded = false
                            }) {
                                Text(option)
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))

                TextField(
                    value = direccion,
                    onValueChange = { direccion = it },
                    label = { Text("Dirección") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                // Menú desplegable para "Estado"
                Box {
                    OutlinedTextField(
                        value = estado,
                        onValueChange = {},
                        label = { Text("Estado") },
                        modifier = Modifier.fillMaxWidth(),
                        readOnly = true,
                        trailingIcon = {
                            IconButton(onClick = { estadoExpanded = true }) {
                                Icon(Icons.Default.ArrowDropDown, contentDescription = "Abrir menú")
                            }
                        }
                    )
                    DropdownMenu(
                        expanded = estadoExpanded,
                        onDismissRequest = { estadoExpanded = false }
                    ) {
                        estadoOptions.forEach { option ->
                            DropdownMenuItem(onClick = {
                                estado = option
                                estadoExpanded = false
                            }) {
                                Text(option)
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))

                // Selector de Fecha
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.Start
                ) {
                    Button(onClick = { showDatePicker = true }) {
                        Text("Fecha: ${fechaHora.format(dateFormatter)}")
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = { showTimePicker = true }) {
                        Text("Hora: ${fechaHora.format(timeFormatter)}")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(onClick = onClose) {
                        Text("Cancelar")
                    }
                    Button(
                        onClick = {
                            onSave(
                                Tarea(
                                    id = tarea?.id ?: 0L,
                                    titulo = titulo,
                                    descripcion = descripcion,
                                    encargado = encargado,
                                    direccion = direccion,
                                    estado = estado,
                                    fechaHora = fechaHora.atZone(ZoneId.systemDefault()).toEpochSecond().toDouble()
                                )
                            )
                        }
                    ) {
                        Text("Guardar")
                    }
                }
            }
        }
    }

    // Mostrar el DatePickerDialog
    if (showDatePicker) {
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                fechaHora = fechaHora.withYear(year).withMonth(month + 1).withDayOfMonth(dayOfMonth)
                showDatePicker = false
            },
            fechaHora.year,
            fechaHora.monthValue - 1,
            fechaHora.dayOfMonth
        ).show()
    }

    // Mostrar el TimePickerDialog
    if (showTimePicker) {
        TimePickerDialog(
            context,
            { _, hour, minute ->
                fechaHora = fechaHora.withHour(hour).withMinute(minute)
                showTimePicker = false
            },
            fechaHora.hour,
            fechaHora.minute,
            true // formato 24 horas
        ).show()
    }
}