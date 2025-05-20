package es.tecnicalman.ui.screen.factura

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
import es.tecnicalman.viewmodel.FacturaViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun FacturaFormScreen(
    navController: NavHostController,
    facturaId: Long? = null,
    facturaViewModel: FacturaViewModel = viewModel(),
    clienteViewModel: ClienteViewModel = viewModel()
) {
    val TAG = "FacturaFormScreen"

    var titulo by remember { mutableStateOf("") }
    var condiciones by remember { mutableStateOf("") }
    var estado by remember { mutableStateOf(Factura.EstadoFactura.BORRADOR) }
    var idCliente by remember { mutableStateOf<Long?>(null) }
    var lineas by remember { mutableStateOf<List<LineaFactura>>(emptyList()) }
    var lineasEliminadas by remember { mutableStateOf<List<LineaFactura>>(emptyList()) }

    var showLineaDialog by remember { mutableStateOf(false) }
    var lineaEditIndex by remember { mutableStateOf<Int?>(null) }
    var nuevaLinea by remember {
        mutableStateOf(
            LineaFactura(
                id = null,
                idFactura = 0,
                descripcion = "",
                cantidad = 1,
                precioUnitario = 0.0
            )
        )
    }

    val isLoadingFactura by facturaViewModel.isLoading.collectAsState()
    val isLoadingCliente by clienteViewModel.isLoading.collectAsState()

    if (facturaId != null) {
        LaunchedEffect(facturaId) {
            facturaViewModel.loadFacturaById(facturaId)
            facturaViewModel.loadLineasByFacturaId(facturaId)
        }
    }
    LaunchedEffect(Unit) {
        clienteViewModel.fetchClientes()
    }

    val factura by facturaViewModel.facturaDetail.collectAsState()
    val lineasViewModel by facturaViewModel.lineasFacturaDetail.collectAsState()
    val clientes by clienteViewModel.clientes.collectAsState()

    LaunchedEffect(factura) {
        factura?.let {
            titulo = it.titulo
            condiciones = it.condiciones
            estado = it.estado
            idCliente = it.idCliente
        }
    }
    LaunchedEffect(lineasViewModel) {
        if (facturaId != null) {
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

    if (isLoadingFactura || isLoadingCliente) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = if (facturaId == null) "Nueva Factura" else "Editar Factura",
                            color = MaterialTheme.colorScheme.background
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Volver", tint = Color.White
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

                    OutlinedTextField(
                        value = titulo,
                        onValueChange = { titulo = it },
                        label = { Text("Título", style = textStyle) },
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = textStyle,
                        colors = textFieldColors
                    )

                    Spacer(modifier = Modifier.height(16.dp))

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
                            Factura.EstadoFactura.values().forEach { estadoOption ->
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

                    Text("Líneas de factura",
                        style = MaterialTheme.typography.titleLarge.merge(textStyle))
                }

                items(lineas) { linea ->
                    LineaFacturaItem(
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

                    Button(
                        onClick = {
                            nuevaLinea = LineaFactura(
                                id = null,
                                idFactura = facturaId ?: 0,
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

                    Button(
                        onClick = {
                            val factura = Factura(
                                id = facturaId,
                                idCliente = idCliente ?: 0,
                                titulo = titulo,
                                condiciones = condiciones,
                                fechaEmitida = null,
                                fechaValidez = null,
                                estado = estado
                            )

                            scope.launch {
                                try {
                                    Log.d(TAG, "Iniciando guardado de factura y líneas")
                                    if (facturaId == null) {
                                        facturaViewModel.createFacturaWithLineas(
                                            factura = factura,
                                            lineas = lineas
                                        )
                                        Log.d(TAG, "Guardado de factura y líneas finalizado (nueva factura)")
                                    } else {
                                        factura.id?.let { id ->
                                            facturaViewModel.updateFactura(id, factura)
                                            lineas.forEach { linea ->
                                                if (linea.id == null) {
                                                    facturaViewModel.createLineaFactura(linea.copy(idFactura = id))
                                                } else {
                                                    facturaViewModel.updateLineaFactura(linea.id, linea)
                                                }
                                            }
                                            lineasEliminadas.forEach { linea ->
                                                linea.id?.let { lineaId ->
                                                    facturaViewModel.deleteLineaFactura(lineaId)
                                                }
                                            }
                                            Log.d(TAG, "Guardado de factura y líneas finalizado (edición)")
                                        }
                                    }
                                    navController.popBackStack()
                                } catch (e: Exception) {
                                    Log.e(TAG, "Error guardando factura y líneas", e)
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
                        Text("Guardar Factura", style = textStyle.copy(color = Color.White))
                    }
                }
            }
        }
    }

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
fun LineaFacturaItem(
    linea: LineaFactura,
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
                        tint = Color.Black
                    )
                }
                IconButton(onClick = onDelete) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Eliminar",
                        tint = Color.Red
                    )
                }
            }
        }
    }
}