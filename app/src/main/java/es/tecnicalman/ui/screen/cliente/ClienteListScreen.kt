package es.tecnicalman.ui.screen.cliente

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PlusOne
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import es.tecnicalman.viewmodel.ClienteViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ClienteListScreen(
    navController: NavController,
    onClienteClick: (Long) -> Unit,
    viewModel: ClienteViewModel = viewModel()
) {
    LaunchedEffect(Unit) {
        viewModel.fetchClientes()
    }

    val isLoading by viewModel.isLoading.collectAsState()
    val clientes by viewModel.clientes.collectAsState()

    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        Column(modifier = Modifier.fillMaxSize()) {

            TopAppBar(
                backgroundColor = androidx.compose.material.MaterialTheme.colors.surface,
                title = { androidx.compose.material.Text("Lista de cliente") },
                actions = {
                    // Botón para ir al Home
                    IconButton(onClick = { navController.navigate("home") }) {
                        Icon(Icons.Default.Home, contentDescription = "Ir al Home")
                    }
                    IconButton(onClick = { navController.navigate("clienteForm") }) {
                        Icon(Icons.Default.Add, contentDescription = "Crear cliente")
                    }
                }
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(start = 30.dp, end = 30.dp, top = 30.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "Nombre", color = Color.Black)
                Text(text = "NIF", color = Color.Black)
            }

            // Lista con scroll
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background),
                contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 20.dp, top = 10.dp)
            ) {
                items(clientes) { cliente ->
                    val colorBackground = if (cliente.nif.isBlank()) Color.Red else MaterialTheme.colorScheme.background

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(shape = MaterialTheme.shapes.medium)
                            .background(colorBackground)
                            .clickable { onClienteClick(cliente.id!!) }
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = cliente.nombre, color = Color.Black)
                            Text(text = cliente.direccion, color = Color.Gray)
                        }
                        Text(
                            text = if (cliente.nif.isBlank()) "Sin NIF" else cliente.nif,
                            color = Color.Black
                        )
                    }
                    Spacer(modifier = Modifier.height(5.dp))
                }
            }
        }
    }
}
