package es.tecnicalman.ui.screen.presupuesto

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import es.tecnicalman.model.*
import es.tecnicalman.viewmodel.ClienteViewModel
import es.tecnicalman.viewmodel.PresupuestoViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun PresupuestoFormScreen(
    navController: NavHostController,
    presupuestoId: Long? = null,
    presupuestoViewModel: PresupuestoViewModel = viewModel(),
    clienteViewModel: ClienteViewModel = viewModel()
) {
    val TAG = "PresupuestoFormScreen"

    // Form state
    var titulo by remember { mutableStateOf("") }
    var condiciones by remember { mutableStateOf("") }
    var estado by remember { mutableStateOf(Presupuesto.EstadoPresupuesto.BORRADOR) }
    var idCliente by remember { mutableStateOf<Long?>(null) }
    var lineas by remember { mutableStateOf<List<LineaPresupuesto>>(emptyList()) }
    var lineasEliminadas by remember { mutableStateOf<List<LineaPresupuesto>>(emptyList()) }

    // Línea dialog state
    var showLineaDialog by remember { mutableStateOf(false) }
    var lineaEditIndex by remember { mutableStateOf<Int?>(null) }
    var nuevaLinea by remember {
        mutableStateOf(
            LineaPresupuesto(
                id = null,
                idPresupuesto = 0,
                descripcion = "",
                cantidad = 1,
                precioUnitario = 0.0
            )
        )
    }

    // Cargar presupuesto y líneas solo si la ID no es null (solo edición)
    val isLoadingPresupuesto by presupuestoViewModel.isLoading.collectAsState()
    val isLoadingCliente by clienteViewModel.isLoading.collectAsState()

    if (presupuestoId != null) {
        LaunchedEffect(presupuestoId) {
            presupuestoViewModel.loadPresupuestoById(presupuestoId)
            presupuestoViewModel.loadLineasByPresupuestoId(presupuestoId)
        }
    }
    LaunchedEffect(Unit) {
        clienteViewModel.fetchClientes()
    }

    val presupuesto by presupuestoViewModel.presupuestoDetail.collectAsState()
    val lineasViewModel by presupuestoViewModel.lineasPresupuestoDetail.collectAsState()
    val clientes by clienteViewModel.clientes.collectAsState()

    // Solo actualiza los campos si hay datos de presupuesto y linea (evita sobrescribir en edición)
    LaunchedEffect(presupuesto) {
        presupuesto?.let {
            titulo = it.titulo
            condiciones = it.condiciones
            estado = it.estado
            idCliente = it.idCliente
        }
    }
    LaunchedEffect(lineasViewModel) {
        if (presupuestoId != null) {
            lineas = lineasViewModel
        }
    }

    val clienteSeleccionado = clientes.find { it.id == idCliente }

    val totalSinIVA = lineas.sumOf { it.cantidad * it.precioUnitario }
    val iva = totalSinIVA * 0.21
    val totalConIVA = totalSinIVA + iva

    val scope = rememberCoroutineScope()
    val textStyle = remember { TextStyle(color = Color.Black) }
    val textFieldColors = TextFieldDefaults.colors(
        focusedTextColor = Color.Black,
        unfocusedTextColor = Color.Black,
        focusedContainerColor = Color.Transparent,
        unfocusedContainerColor = Color.Transparent,
        focusedLabelColor = Color.Black,
        unfocusedLabelColor = Color.Black,
        cursorColor = Color.Black,
        focusedIndicatorColor = Color.Black.copy(alpha = 0.6f),
        unfocusedIndicatorColor = Color.Black.copy(alpha = 0.3f)
    )

    if (isLoadingPresupuesto || isLoadingCliente) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = if (presupuestoId == null) "Nuevo Presupuesto" else "Editar Presupuesto",
                            color = MaterialTheme.colorScheme.background
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Volver"
                            )
                        }
                    }
                )
            }
        ) { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .padding(paddingValues)
                    .padding(16.dp)
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
            ) {
                item {
                    // Selección de cliente con DropdownMenu
                    var clienteExpanded by remember { mutableStateOf(false) }
                    Column {
                        Text("Cliente", style = MaterialTheme.typography.titleMedium.merge(textStyle))
                        OutlinedTextField(
                            value = clienteSeleccionado?.nombre ?: "Seleccione un cliente",
                            onValueChange = {},
                            modifier = Modifier.fillMaxWidth(),
                            readOnly = true,
                            textStyle = textStyle,
                            colors = textFieldColors,
                            trailingIcon = {
                                IconButton(onClick = { clienteExpanded = !clienteExpanded }) {
                                    Icon(
                                        imageVector = if (clienteExpanded) Icons.Filled.ArrowDropUp else Icons.Filled.ArrowDropDown,
                                        contentDescription = "Desplegar clientes",
                                        tint = Color.Black
                                    )
                                }
                            }
                        )

                        DropdownMenu(
                            expanded = clienteExpanded,
                            onDismissRequest = { clienteExpanded = false },
                            modifier = Modifier
                                .fillMaxWidth(0.9f)
                                .background(Color.White)
                        ) {
                            if (clientes.isEmpty()) {
                                DropdownMenuItem(
                                    text = { Text("No hay clientes disponibles", style = textStyle) },
                                    onClick = { clienteExpanded = false }
                                )
                            } else {
                                clientes.forEach { cliente ->
                                    DropdownMenuItem(
                                        text = { Text(cliente.nombre, style = textStyle) },
                                        onClick = {
                                            idCliente = cliente.id
                                            clienteExpanded = false
                                        }
                                    )
                                }
                            }
                        }

                        // Detalles del cliente seleccionado
                        clienteSeleccionado?.let { cliente ->
                            Column(
                                modifier = Modifier
                                    .padding(8.dp)
                                    .fillMaxWidth()
                                    .background(
                                        color = Color.LightGray.copy(alpha = 0.2f),
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .padding(8.dp)
                            ) {
                                Text("Nombre: ${cliente.nombre}", style = textStyle)
                                Text("NIF: ${cliente.nif}", style = textStyle)
                                Text("Dirección: ${cliente.direccion}", style = textStyle)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Título del presupuesto
                    OutlinedTextField(
                        value = titulo,
                        onValueChange = { titulo = it },
                        label = { Text("Título", style = textStyle) },
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = textStyle,
                        colors = textFieldColors
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Condiciones
                    OutlinedTextField(
                        value = condiciones,
                        onValueChange = { condiciones = it },
                        label = { Text("Condiciones", style = textStyle) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp),
                        textStyle = textStyle,
                        colors = textFieldColors,
                        maxLines = 5
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Estado con DropdownMenu
                    var estadoExpanded by remember { mutableStateOf(false) }
                    Column {
                        Text("Estado", style = MaterialTheme.typography.titleMedium.merge(textStyle))
                        OutlinedTextField(
                            value = estado.toString(),
                            onValueChange = {},
                            modifier = Modifier.fillMaxWidth(),
                            readOnly = true,
                            textStyle = textStyle,
                            colors = textFieldColors,
                            trailingIcon = {
                                IconButton(onClick = { estadoExpanded = !estadoExpanded }) {
                                    Icon(
                                        imageVector = if (estadoExpanded) Icons.Filled.ArrowDropUp else Icons.Filled.ArrowDropDown,
                                        contentDescription = "Desplegar estados",
                                        tint = Color.Black
                                    )
                                }
                            }
                        )

                        DropdownMenu(
                            expanded = estadoExpanded,
                            onDismissRequest = { estadoExpanded = false },
                            modifier = Modifier
                                .fillMaxWidth(0.5f)
                                .background(Color.White)
                        ) {
                            Presupuesto.EstadoPresupuesto.values().forEach { estadoOption ->
                                DropdownMenuItem(
                                    text = { Text(estadoOption.toString(), style = textStyle) },
                                    onClick = {
                                        estado = estadoOption
                                        estadoExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Líneas de presupuesto
                    Text("Líneas de presupuesto",
                        style = MaterialTheme.typography.titleLarge.merge(textStyle))
                }

                // Lista de líneas
                items(lineas) { linea ->
                    LineaPresupuestoItem(
                        linea = linea,
                        onEdit = {
                            nuevaLinea = linea.copy()
                            lineaEditIndex = lineas.indexOf(linea)
                            showLineaDialog = true
                        },
                        onDelete = {
                            if (linea.id != null) {
                                lineasEliminadas = lineasEliminadas + linea
                            }
                            lineas = lineas.toMutableList().apply { remove(linea) }
                        },
                        textStyle = textStyle
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))

                    // Botón para añadir línea
                    Button(
                        onClick = {
                            nuevaLinea = LineaPresupuesto(
                                id = null,
                                idPresupuesto = presupuestoId ?: 0,
                                descripcion = "",
                                cantidad = 1,
                                precioUnitario = 0.0
                            )
                            lineaEditIndex = null
                            showLineaDialog = true
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White,
                            contentColor = Color.Black
                        ),
                        border = ButtonDefaults.outlinedButtonBorder
                    ) {
                        Text("Añadir línea", style = textStyle)
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Totales
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.End
                    ) {
                        Text("Total sin IVA: %.2f €".format(totalSinIVA), style = textStyle)
                        Text("IVA (21%%): %.2f €".format(iva), style = textStyle)
                        Text("Total con IVA: %.2f €".format(totalConIVA),
                            style = MaterialTheme.typography.titleMedium.merge(textStyle))
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Botón guardar
                    Button(
                        onClick = {
                            val presupuesto = Presupuesto(
                                id = presupuestoId,
                                idCliente = idCliente ?: 0,
                                titulo = titulo,
                                condiciones = condiciones,
                                fechaEmitida = null,
                                fechaValidez = null,
                                estado = estado
                            )

                            scope.launch {
                                try {
                                    Log.d(TAG, "Iniciando guardado de presupuesto y líneas")
                                    if (presupuestoId == null) {
                                        // Crear presupuesto y luego las líneas
                                        presupuestoViewModel.createPresupuestoWithLineas(
                                            presupuesto = presupuesto,
                                            lineas = lineas
                                        )
                                        Log.d(TAG, "Guardado de presupuesto y líneas finalizado (nuevo presupuesto)")
                                    } else {
                                        // Actualizar presupuesto y líneas
                                        presupuesto.id?.let { id ->
                                            presupuestoViewModel.updatePresupuesto(id, presupuesto)
                                            lineas.forEach { linea ->
                                                if (linea.id == null) {
                                                    presupuestoViewModel.createLineaPresupuesto(linea.copy(idPresupuesto = id))
                                                } else {
                                                    presupuestoViewModel.updateLineaPresupuesto(linea.id, linea)
                                                }
                                            }
                                            lineasEliminadas.forEach { linea ->
                                                linea.id?.let { lineaId ->
                                                    presupuestoViewModel.deleteLineaPresupuesto(lineaId)
                                                }
                                            }
                                            Log.d(TAG, "Guardado de presupuesto y líneas finalizado (edición)")
                                        }
                                    }
                                    navController.popBackStack()
                                } catch (e: Exception) {
                                    Log.e(TAG, "Error guardando presupuesto y líneas", e)
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = idCliente != null && titulo.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Black,
                            contentColor = Color.White
                        )
                    ) {
                        Text("Guardar Presupuesto", style = textStyle.copy(color = Color.White))
                    }
                }
            }
        }
    }

    // Diálogo para añadir/editar línea
    if (showLineaDialog) {
        AlertDialog(
            onDismissRequest = { showLineaDialog = false },
            title = {
                Text(
                    text = if (lineaEditIndex == null) "Nueva línea" else "Editar línea",
                    style = textStyle
                )
            },
            text = {
                Column {
                    OutlinedTextField(
                        value = nuevaLinea.descripcion,
                        onValueChange = { nuevaLinea = nuevaLinea.copy(descripcion = it) },
                        label = { Text("Descripción", style = textStyle) },
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = textStyle,
                        colors = textFieldColors
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = nuevaLinea.cantidad.toString(),
                        onValueChange = {
                            nuevaLinea = nuevaLinea.copy(cantidad = it.toLongOrNull() ?: 1)
                        },
                        label = { Text("Cantidad", style = textStyle) },
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.Number
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = textStyle,
                        colors = textFieldColors
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = nuevaLinea.precioUnitario.toString(),
                        onValueChange = {
                            nuevaLinea = nuevaLinea.copy(precioUnitario = it.toDoubleOrNull() ?: 0.0)
                        },
                        label = { Text("Precio Unitario", style = textStyle) },
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.Number
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = textStyle,
                        colors = textFieldColors
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (lineaEditIndex != null) {
                            lineas = lineas.toMutableList().apply {
                                set(lineaEditIndex!!, nuevaLinea)
                            }
                        } else {
                            lineas = lineas + nuevaLinea
                        }
                        showLineaDialog = false
                    },
                    enabled = nuevaLinea.descripcion.isNotBlank() &&
                            nuevaLinea.cantidad > 0 &&
                            nuevaLinea.precioUnitario >= 0,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Black,
                        contentColor = Color.White
                    )
                ) {
                    Text("Guardar", color = MaterialTheme.colorScheme.onBackground)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLineaDialog = false }) {
                    Text("Cancelar", style = textStyle)
                }
            },
            containerColor = Color.White
        )
    }
}

@Composable
fun LineaPresupuestoItem(
    linea: LineaPresupuesto,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    textStyle: TextStyle
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(linea.descripcion, style = MaterialTheme.typography.bodyLarge.merge(textStyle))
                Spacer(modifier = Modifier.height(4.dp))
                Text("Cantidad: ${linea.cantidad}", style = textStyle)
                Text("Precio: %.2f €".format(linea.precioUnitario), style = textStyle)
                Text("Total: %.2f €".format(linea.cantidad * linea.precioUnitario), style = textStyle)
            }

            Row {
                IconButton(onClick = onEdit) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "Editar",
                        tint = Color.White
                    )
                }
                IconButton(onClick = onDelete) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Eliminar",
                        tint = Color.White
                    )
                }
            }
        }
    }
}