package es.tecnicalman.ui.screen.factura

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import es.tecnicalman.viewmodel.ClienteViewModel
import es.tecnicalman.viewmodel.FacturaViewModel
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FacturaDetailScreen(
    navController: NavHostController,
    facturaId: Long,
    facturaViewModel: FacturaViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    clienteViewModel: ClienteViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    LaunchedEffect(facturaId) {
        facturaViewModel.loadFacturaById(facturaId)
        facturaViewModel.loadLineasByFacturaId(facturaId)
    }

    val factura by facturaViewModel.facturaDetail.collectAsState()
    val lineas by facturaViewModel.lineasFacturaDetail.collectAsState()
    val isLoadingFactura by facturaViewModel.isLoading.collectAsState()
    val errorFactura by facturaViewModel.errorMessage.collectAsState()

    LaunchedEffect(factura?.idCliente) {
        factura?.idCliente?.let { clienteViewModel.fetchClienteById(it) }
    }
    val cliente by clienteViewModel.clienteSeleccionado.collectAsState()
    val isLoadingCliente by clienteViewModel.isLoading.collectAsState()

    var showDeleteDialog by remember { mutableStateOf(false) }

    fun deleteFacturaAndLineas(facturaId: Long) {
        lineas.forEach { linea ->
            facturaViewModel.deleteLineaFactura(linea.id ?: return@forEach)
        }
        facturaViewModel.deleteFactura(facturaId)
        navController.popBackStack()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle Factura") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Volver",
                            tint = Color.White)
                    }
                },
                actions = {
                    IconButton(onClick = {
                        factura?.id?.let {
                            navController.navigate("facturaForm/edit/$facturaId")
                        }
                    }) {
                        Icon(Icons.Filled.Edit, contentDescription = "Editar", tint = Color.White)
                    }
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
        if (isLoadingFactura || isLoadingCliente) {
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

        if (errorFactura != null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "Error: $errorFactura", color = MaterialTheme.colorScheme.error)
            }
            return@Scaffold
        }

        factura?.let { fac ->
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
                Text("Título: ${fac.titulo}", style = MaterialTheme.typography.bodyLarge)
                Text("Número: ${fac.id}", style = MaterialTheme.typography.bodyLarge)
                Text("Fecha emisión: ${formatDate(fac.fechaEmitida)}", style = MaterialTheme.typography.bodyMedium)
                Text("Validez hasta: ${formatDate(fac.fechaValidez)}", style = MaterialTheme.typography.bodyMedium)

                Spacer(Modifier.height(16.dp))

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

                Text("Líneas", style = MaterialTheme.typography.titleMedium)
                if (lineas.isEmpty()) {
                    Text("No hay líneas en esta factura.")
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

                Text("Condiciones", style = MaterialTheme.typography.titleMedium)
                Text(fac.condiciones.ifEmpty { "No hay condiciones específicas." })

                Spacer(Modifier.height(16.dp))

                Text("Totales", style = MaterialTheme.typography.titleMedium)
                Text("Total sin IVA: ${"%.2f €".format(totalSinIVA)}")
                Text("IVA (21%): ${"%.2f €".format(totalIVA)}")
                Text("Total con IVA: ${"%.2f €".format(totalConIVA)}")
            }
        }

        if (showDeleteDialog) {
            AlertDialog(
                onDismissRequest = { showDeleteDialog = false },
                title = { Text("Confirmar eliminación") },
                text = { Text("¿Estás seguro de que deseas eliminar esta factura y todas sus líneas? Esta acción no se puede deshacer.") },
                confirmButton = {
                    TextButton(onClick = {
                        factura?.id?.let { deleteId ->
                            deleteFacturaAndLineas(deleteId)
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