package es.tecnicalman.ui.screen.albaran

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import es.tecnicalman.viewmodel.ClienteViewModel
import es.tecnicalman.viewmodel.AlbaranViewModel
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlbaranDetailScreen(
    navController: NavHostController,
    albaranId: Long,
    albaranViewModel: AlbaranViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    clienteViewModel: ClienteViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    LaunchedEffect(albaranId) {
        albaranViewModel.loadAlbaranById(albaranId)
        albaranViewModel.loadLineasByAlbaranId(albaranId)
    }

    val albaran by albaranViewModel.albaranDetail.collectAsState()
    val lineas by albaranViewModel.lineasAlbaranDetail.collectAsState()
    val isLoadingAlbaran by albaranViewModel.isLoading.collectAsState()
    val errorAlbaran by albaranViewModel.errorMessage.collectAsState()

    LaunchedEffect(albaran?.idCliente) {
        albaran?.idCliente?.let { clienteViewModel.fetchClienteById(it) }
    }
    val cliente by clienteViewModel.clienteSeleccionado.collectAsState()
    val isLoadingCliente by clienteViewModel.isLoading.collectAsState()

    var showDeleteDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    fun deleteAlbaranAndLineas(albaranId: Long) {
        lineas.forEach { linea ->
            albaranViewModel.deleteLineaAlbaran(linea.id ?: return@forEach)
        }
        albaranViewModel.deleteAlbaran(albaranId)
        navController.popBackStack()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle Albarán") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Volver", tint = Color.White)
                    }
                },
                actions = {
                    // Botón descargar PDF
                    IconButton(
                        onClick = {
                            albaran?.id?.let {
                                albaranViewModel.descargarAlbaranPdf(context, it)
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.FileDownload,
                            contentDescription = "Descargar PDF",
                            tint = Color.White
                        )
                    }
                    // Botón editar
                    IconButton(onClick = {
                        albaran?.id?.let {
                            navController.navigate("albaranForm/edit/$albaranId")
                        }
                    }) {
                        Icon(Icons.Filled.Edit, contentDescription = "Editar", tint = Color.White)
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
        if (isLoadingAlbaran || isLoadingCliente) {
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

        if (errorAlbaran != null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "Error: $errorAlbaran", color = MaterialTheme.colorScheme.error)
            }
            return@Scaffold
        }

        albaran?.let { alb ->
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
                Text("Título: ${alb.titulo}", style = MaterialTheme.typography.bodyLarge)
                Text("Número: ${alb.id}", style = MaterialTheme.typography.bodyLarge)
                Text("Fecha emisión: ${formatDate(alb.fechaEmitida)}", style = MaterialTheme.typography.bodyMedium)
                Text("Validez hasta: ${formatDate(alb.fechaValidez)}", style = MaterialTheme.typography.bodyMedium)

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
                    Text("No hay líneas en este albarán.")
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
                Text(alb.condiciones.ifEmpty { "No hay condiciones específicas." })

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
                text = { Text("¿Estás seguro de que deseas eliminar este albarán y todas sus líneas? Esta acción no se puede deshacer.") },
                confirmButton = {
                    TextButton(onClick = {
                        albaran?.id?.let { deleteId ->
                            deleteAlbaranAndLineas(deleteId)
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