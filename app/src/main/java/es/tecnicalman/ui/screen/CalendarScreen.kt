package es.tecnicalman.ui.screen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Home
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import es.tecnicalman.model.Tarea
import es.tecnicalman.ui.component.TareaForm
import es.tecnicalman.ui.component.TaskModal
import es.tecnicalman.viewmodel.TareaViewModel
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.*

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CalendarScreen(navController: NavController, viewModel: TareaViewModel = viewModel()) {
    var currentMonth by remember { mutableStateOf(YearMonth.now()) }
    val tarea by viewModel.tarea.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }
    var isFormOpen by remember { mutableStateOf(false) }
    var tareaToEdit by remember { mutableStateOf<Tarea?>(null) }

    // Carga inicial de las tareas
    LaunchedEffect(Unit) {
        viewModel.fetchTasks()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                backgroundColor = MaterialTheme.colors.surface,
                title = { Text("Calendario de Tareas") },
                actions = {
                    // Botón para ir al Home
                    IconButton(onClick = { navController.navigate("home") }) {
                        Icon(Icons.Default.Home, contentDescription = "Ir al Home")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                tareaToEdit = null
                isFormOpen = true
            }) {
                Text("+")
            }
        },
        content = { padding ->
            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (error != null) {
                Text(
                    text = "Error: $error",
                    color = MaterialTheme.colors.error,
                    modifier = Modifier.padding(16.dp)
                )
            } else {
                Column(Modifier.padding(padding)) {
                    // Header with Month and Navigation Buttons
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = {
                                currentMonth = currentMonth.minusMonths(1)
                            },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Mes Anterior")
                        }
                        Text(
                            text = "${currentMonth.month.getDisplayName(java.time.format.TextStyle.FULL, Locale.getDefault())} ${currentMonth.year}",
                            style = MaterialTheme.typography.h6.copy(fontWeight = FontWeight.Bold)
                        )
                        IconButton(
                            onClick = {
                                currentMonth = currentMonth.plusMonths(1)
                            },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(Icons.Default.ArrowForward, contentDescription = "Mes Siguiente")
                        }
                    }

                    // Generación del calendario mensual
                    val daysInMonth = currentMonth.lengthOfMonth()
                    val firstDayOfMonth = currentMonth.atDay(1)
                    val firstDayOfWeek = firstDayOfMonth.dayOfWeek.value % 7
                    val totalDays = daysInMonth + firstDayOfWeek
                    val weeks = (0 until totalDays step 7).map { weekStart ->
                        (0..6).map { day ->
                            val dayOfMonth = weekStart + day - firstDayOfWeek + 1
                            if (dayOfMonth in 1..daysInMonth) currentMonth.atDay(dayOfMonth) else null
                        }
                    }

                    // Renderizado de encabezados de días
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                        listOf("Dom", "Lun", "Mar", "Mié", "Jue", "Vie", "Sáb").forEach { day ->
                            Text(day, style = MaterialTheme.typography.body2, modifier = Modifier.weight(1f))
                        }
                    }

                    // Renderizado de semanas
                    weeks.forEach { week ->
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                            week.forEach { date ->
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .aspectRatio(1f)
                                        .background(
                                            if (date == selectedDate) Color.Transparent else MaterialTheme.colors.surface
                                        )
                                        .clickable { if (date != null) selectedDate = date },
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (date != null) {
                                        val dayTareaCount = tarea.count {
                                            val taskDate = it.fechaHora.let { ts ->
                                                java.time.Instant.ofEpochSecond(ts.toLong())
                                                    .atZone(java.time.ZoneId.systemDefault())
                                                    .toLocalDate()
                                            }
                                            taskDate.isEqual(date)
                                        }
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Text(
                                                text = date.dayOfMonth.toString(),
                                                style = MaterialTheme.typography.body1
                                            )
                                            if (dayTareaCount > 0) {
                                                Text(
                                                    text = "$dayTareaCount tareas",
                                                    style = MaterialTheme.typography.caption,
                                                    color = MaterialTheme.colors.secondary
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    selectedDate?.let { date ->
                        val filteredTasks = tarea.filter {
                            try {
                                val taskDate = it.fechaHora.let { ts ->
                                    java.time.Instant.ofEpochSecond(ts.toLong())
                                        .atZone(java.time.ZoneId.systemDefault())
                                        .toLocalDate()
                                }
                                taskDate.isEqual(date)
                            } catch (e: Exception) {
                                false
                            }
                        }
                        TaskModal(
                            date = date,
                            tasks = filteredTasks,
                            onClose = { selectedDate = null },
                            onEditTask = { tarea ->
                                tareaToEdit = tarea
                                isFormOpen = true
                            },
                            onDeleteTask = { tarea -> viewModel.deleteTask(tarea.id) }
                        )
                    }
                }
            }
        }
    )

    if (isFormOpen) {
        TareaForm(
            tarea = tareaToEdit,
            onClose = { isFormOpen = false },
            onSave = { tarea ->
                if (tarea.id != 0L) {
                    viewModel.updateTask(tarea.id, tarea)
                } else {
                    viewModel.createTask(tarea)
                }
                isFormOpen = false
            }
        )
    }
}