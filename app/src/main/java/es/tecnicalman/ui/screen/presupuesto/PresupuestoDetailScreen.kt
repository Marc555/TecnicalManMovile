package es.tecnicalman.ui.screen.presupuesto

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import es.tecnicalman.viewmodel.ClienteViewModel
import es.tecnicalman.viewmodel.PresupuestoViewModel
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PresupuestoDetailScreen(
    navController: NavHostController,
    presupuestoId: Long,
    presupuestoViewModel: PresupuestoViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    clienteViewModel: ClienteViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    LaunchedEffect(presupuestoId) {
        presupuestoViewModel.loadPresupuestoById(presupuestoId)
        presupuestoViewModel.loadLineasByPresupuestoId(presupuestoId)
    }

    val presupuesto by presupuestoViewModel.presupuestoDetail.collectAsState()
    val lineas by presupuestoViewModel.lineasPresupuestoDetail.collectAsState()
    val isLoadingPresupuesto by presupuestoViewModel.isLoading.collectAsState()
    val errorPresupuesto by presupuestoViewModel.errorMessage.collectAsState()

    LaunchedEffect(presupuesto?.idCliente) {
        presupuesto?.idCliente?.let { clienteViewModel.fetchClienteById(it) }
    }
    val cliente by clienteViewModel.clienteSeleccionado.collectAsState()
    val isLoadingCliente by clienteViewModel.isLoading.collectAsState()

    // Estado para el diálogo de confirmación de borrado
    var showDeleteDialog by remember { mutableStateOf(false) }

    // Acción de borrado: elimina líneas y después el presupuesto
    fun deletePresupuestoAndLineas(presupuestoId: Long) {
        // Primero elimina todas las líneas del presupuesto
        lineas.forEach { linea ->
            presupuestoViewModel.deleteLineaPresupuesto(linea.id ?: return@forEach)
        }
        // Después elimina el propio presupuesto (puedes mejorar esto con un callback/coroutine chain si quieres esperar confirmación)
        presupuestoViewModel.deletePresupuesto(presupuestoId)
        // Vuelve atrás tras borrar
        navController.popBackStack()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle Presupuesto") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    // Botón editar
                    IconButton(onClick = {
                        presupuesto?.id?.let {
                            navController.navigate("presupuestoForm/edit/$presupuestoId")
                        }
                    }) {
                        Icon(Icons.Filled.Edit, contentDescription = "Editar")
                    }
                    // Botón borrar
                    IconButton(onClick = {
                        showDeleteDialog = true
                    }) {
                        Icon(
                            imageVector = Icons.Filled.Delete,
                            contentDescription = "Borrar",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        if (isLoadingPresupuesto || isLoadingCliente) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
            return@Scaffold
        }

        if (errorPresupuesto != null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "Error: $errorPresupuesto", color = MaterialTheme.colorScheme.error)
            }
            return@Scaffold
        }

        presupuesto?.let { pres ->
            val totalSinIVA = lineas.sumOf { it.cantidad * it.precioUnitario }
            val iva = 0.21
            val totalIVA = totalSinIVA * iva
            val totalConIVA = totalSinIVA + totalIVA

            val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale("es", "ES"))
            fun formatDate(instant: java.time.Instant?) =
                instant?.atZone(ZoneId.systemDefault())?.format(dateFormatter) ?: "N/A"

            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
                    .fillMaxWidth()
            ) {
                // Información presupuesto
                Text("Título: ${pres.titulo}", style = MaterialTheme.typography.bodyLarge)
                Text("Número: ${pres.id}", style = MaterialTheme.typography.bodyLarge)
                Text("Fecha emisión: ${formatDate(pres.fechaEmitida)}", style = MaterialTheme.typography.bodyMedium)
                Text("Validez hasta: ${formatDate(pres.fechaValidez)}", style = MaterialTheme.typography.bodyMedium)

                Spacer(Modifier.height(16.dp))

                // Datos cliente
                Text("Cliente", style = MaterialTheme.typography.titleMedium)
                if (cliente != null) {
                    Text("Nombre: ${cliente?.nombre}")
                    Text("NIF: ${cliente?.nif}")
                    Text("Dirección: ${cliente?.direccion}")
                    Text("Teléfono: ${cliente?.telefono}")
                    Text("Email: ${cliente?.email}")
                } else {
                    Text("Cargando datos del cliente...")
                }

                Spacer(Modifier.height(16.dp))

                // Líneas del presupuesto
                Text("Líneas", style = MaterialTheme.typography.titleMedium)
                if (lineas.isEmpty()) {
                    Text("No hay líneas en este presupuesto.")
                } else {
                    lineas.forEach { linea ->
                        Column(modifier = Modifier.padding(vertical = 8.dp)) {
                            Text("Descripción: ${linea.descripcion}")
                            Text("Cantidad: ${linea.cantidad}")
                            Text("Precio unitario: ${"%.2f €".format(linea.precioUnitario)}")
                            Text("Total línea: ${"%.2f €".format(linea.cantidad * linea.precioUnitario)}")
                        }
                        Divider()
                    }
                }

                Spacer(Modifier.height(16.dp))

                // Condiciones
                Text("Condiciones", style = MaterialTheme.typography.titleMedium)
                Text(pres.condiciones.ifEmpty { "No hay condiciones específicas." })

                Spacer(Modifier.height(16.dp))

                // Totales
                Text("Totales", style = MaterialTheme.typography.titleMedium)
                Text("Total sin IVA: ${"%.2f €".format(totalSinIVA)}")
                Text("IVA (21%): ${"%.2f €".format(totalIVA)}")
                Text("Total con IVA: ${"%.2f €".format(totalConIVA)}")
            }
        }

        // Diálogo de confirmación de borrado
        if (showDeleteDialog) {
            AlertDialog(
                onDismissRequest = { showDeleteDialog = false },
                title = { Text("Confirmar eliminación") },
                text = { Text("¿Estás seguro de que deseas eliminar este presupuesto y todas sus líneas? Esta acción no se puede deshacer.") },
                confirmButton = {
                    TextButton(onClick = {
                        presupuesto?.id?.let { deleteId ->
                            deletePresupuestoAndLineas(deleteId)
                        }
                        showDeleteDialog = false
                    }) {
                        Text("Eliminar", color = MaterialTheme.colorScheme.error)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteDialog = false }) {
                        Text("Cancelar")
                    }
                }
            )
        }
    }
}