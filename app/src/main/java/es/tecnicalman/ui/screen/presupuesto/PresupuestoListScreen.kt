package es.tecnicalman.ui.screen.presupuesto

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import es.tecnicalman.model.Presupuesto
import es.tecnicalman.viewmodel.PresupuestoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun PresupuestoListScreen(
    navController: NavController,
    onPresupuestoClick: (Presupuesto) -> Unit,
    viewModel: PresupuestoViewModel = viewModel()
) {
    viewModel.loadPresupuestos()
    val presupuestos by viewModel.presupuestos.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Lista de Presupuestos") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigate("home") }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        navController.navigate("presupuestoForm")
                    }) {
                        Icon(Icons.Filled.Add, contentDescription = "Crear")
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(presupuestos) { presupuesto ->
                PresupuestoCard(presupuesto = presupuesto, onClick = { onPresupuestoClick(presupuesto) })
            }
        }
    }
}

@Composable
fun PresupuestoCard(presupuesto: Presupuesto, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(6.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = presupuesto.titulo,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Fecha: ${presupuesto.fechaEmitida?.toString() ?: "No emitida"}",
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = "Estado: ${presupuesto.estado}",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}
