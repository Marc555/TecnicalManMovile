@file:OptIn(ExperimentalMaterial3Api::class)

package es.tecnicalman.ui.screen.cliente

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import es.tecnicalman.viewmodel.ClienteViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ClienteDetailScreen(
    navController: NavController,
    clienteId: Long,
    viewModel: ClienteViewModel = viewModel(),
    onBack: () -> Unit
) {
    var showDeleteConfirmation by remember { mutableStateOf(false) }

    viewModel.fetchClienteById(clienteId)
    val isLoading by viewModel.isLoading.collectAsState()
    val cliente by viewModel.clienteSeleccionado.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {

        TopAppBar(
            title = { Text("Detalles de cliente", fontWeight = FontWeight.Bold) },
            navigationIcon = {
                IconButton(onClick = { navController.navigate("clientes") }) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Volver a clientes", tint = Color.White
                    )
                }
            },
            actions = {
                IconButton(onClick = { navController.navigate("clienteForm/$clienteId") }) {
                    Icon(Icons.Default.Edit, contentDescription = "Editar cliente", tint = Color.White)
                }
                IconButton(onClick = { showDeleteConfirmation = true }) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Eliminar cliente",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        )

        Column(modifier = Modifier.padding(start = 16.dp, end = 16.dp)) {

            Spacer(modifier = Modifier.height(16.dp))

            if (isLoading) {
                CircularProgressIndicator()
            } else {
                cliente?.let {
                    InfoSection("Información Principal") {
                        InfoRow("ID", it.id.toString())
                        InfoRow("Nombre", it.nombre)
                        InfoRow("NIF", it.nif)
                        InfoRow("Fecha de Creación", it.fechaCreacionFormateada())
                    }
                    InfoSection("Dirección") {
                        InfoRow("Dirección", it.direccion)
                        InfoRow("Ciudad", it.ciudad)
                        InfoRow("Código Postal", it.codigoPostal)
                        InfoRow("Provincia", it.provincia)
                    }
                    InfoSection("Contacto") {
                        InfoRow("Email", it.email)
                        InfoRow("Teléfono", it.telefono)
                    }
                }
            }

            if (showDeleteConfirmation) {
                AlertDialog(
                    onDismissRequest = { showDeleteConfirmation = false },
                    title = { Text("Confirmar eliminación") },
                    text = { Text("¿Estás seguro de que deseas eliminar este cliente? Esta acción no se puede deshacer.") },
                    confirmButton = {
                        TextButton(onClick = {
                            showDeleteConfirmation = false
                            viewModel.deleteCliente(clienteId)
                            navController.navigate("clientes")
                        }) {
                            Text("Eliminar", color = Color.Red)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDeleteConfirmation = false }) {
                            Text("Cancelar")
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun InfoSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column(modifier = Modifier.padding(vertical = 12.dp)) {
        Text(
            text = title,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        content()
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    Row(modifier = Modifier.padding(vertical = 2.dp)) {
        Text(
            text = "$label: ",
            fontWeight = FontWeight.Bold,
            modifier = Modifier.width(140.dp)
        )
        Text(
            text = value,
            color = Color.Gray
        )
    }
}