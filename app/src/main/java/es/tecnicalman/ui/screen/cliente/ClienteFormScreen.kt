package es.tecnicalman.ui.screen.cliente

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import es.tecnicalman.model.Cliente
import es.tecnicalman.viewmodel.ClienteViewModel

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ClienteFormScreen(
    navController: NavController,
    clienteId: Long?,
    viewModel: ClienteViewModel = viewModel(),
    onFormSubmit: () -> Unit
) {
    var nombre by remember { mutableStateOf("") }
    var nif by remember { mutableStateOf("") }
    var direccion by remember { mutableStateOf("") }
    var ciudad by remember { mutableStateOf("") }
    var codigoPostal by remember { mutableStateOf("") }
    var provincia by remember { mutableStateOf("") }
    var pais by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }

    var emailError by remember { mutableStateOf<String?>(null) }
    var nifError by remember { mutableStateOf<String?>(null) }
    var codigoPostalError by remember { mutableStateOf<String?>(null) }

    fun validateEmail(email: String): String? {
        return if (email.isNotBlank() && !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            "Formato de email inválido"
        } else null
    }

    fun validateNIF(nif: String): String? {
        return if (nif.isNotBlank() && !Regex("^[0-9]{8}[A-Za-z]$").matches(nif)) {
            "Formato de NIF inválido"
        } else null
    }

    fun validateCodigoPostal(cp: String): String? {
        return if (cp.isNotBlank() && !Regex("^\\d{5}$").matches(cp)) {
            "Código postal inválido"
        } else null
    }

    val isLoading by viewModel.isLoading.collectAsState()

    if (clienteId != null) {
        viewModel.fetchClienteById(clienteId)
    }

    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(if (clienteId == null) "Nuevo Cliente" else "Editar Cliente", fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                        }
                    }
                )
            }
        ) { paddingValues ->
            clienteId?.let {
                val cliente by viewModel.clienteSeleccionado.collectAsState()
                cliente?.let {
                    nombre = it.nombre
                    nif = it.nif
                    direccion = it.direccion
                    ciudad = it.ciudad
                    codigoPostal = it.codigoPostal
                    provincia = it.provincia
                    pais = it.pais
                    email = it.email
                    telefono = it.telefono
                }
            }

            val customColors = TextFieldDefaults.colors(
                focusedContainerColor = Color(0xFFF5F5F5),
                unfocusedContainerColor = Color(0xFFF5F5F5),
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
                focusedLabelColor = Color.Black,
                unfocusedLabelColor = Color.Gray,
                focusedIndicatorColor = Color(0xFFFFA500),
                unfocusedIndicatorColor = Color.LightGray,
                cursorColor = Color(0xFFFFA500),
                errorTextColor = Color.Black,
                errorLabelColor = Color.Black
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    OutlinedTextField(
                        value = nombre,
                        onValueChange = { nombre = it },
                        label = { Text("Nombre") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = customColors,
                        shape = MaterialTheme.shapes.medium,
                        singleLine = true
                    )
                }

                item {
                    OutlinedTextField(
                        value = nif,
                        onValueChange = {
                            nif = it
                            nifError = validateNIF(it)
                        },
                        label = { Text("NIF") },
                        isError = nifError != null,
                        supportingText = {
                            nifError?.let { Text(it, color = MaterialTheme.colorScheme.error) }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = customColors,
                        shape = MaterialTheme.shapes.medium,
                        singleLine = true
                    )
                }

                item {
                    OutlinedTextField(
                        value = direccion,
                        onValueChange = { direccion = it },
                        label = { Text("Dirección") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = customColors,
                        shape = MaterialTheme.shapes.medium,
                        singleLine = true
                    )
                }

                item {
                    OutlinedTextField(
                        value = ciudad,
                        onValueChange = { ciudad = it },
                        label = { Text("Ciudad") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = customColors,
                        shape = MaterialTheme.shapes.medium,
                        singleLine = true
                    )
                }

                item {
                    OutlinedTextField(
                        value = codigoPostal,
                        onValueChange = {
                            codigoPostal = it
                            codigoPostalError = validateCodigoPostal(it)
                        },
                        label = { Text("Código Postal") },
                        isError = codigoPostalError != null,
                        supportingText = {
                            codigoPostalError?.let { Text(it, color = MaterialTheme.colorScheme.error) }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = customColors,
                        shape = MaterialTheme.shapes.medium,
                        singleLine = true
                    )
                }

                item {
                    OutlinedTextField(
                        value = provincia,
                        onValueChange = { provincia = it },
                        label = { Text("Provincia") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = customColors,
                        shape = MaterialTheme.shapes.medium,
                        singleLine = true
                    )
                }

                item {
                    OutlinedTextField(
                        value = pais,
                        onValueChange = { pais = it },
                        label = { Text("País") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = customColors,
                        shape = MaterialTheme.shapes.medium,
                        singleLine = true
                    )
                }

                item {
                    OutlinedTextField(
                        value = email,
                        onValueChange = {
                            email = it
                            emailError = validateEmail(it)
                        },
                        label = { Text("Email") },
                        isError = emailError != null,
                        supportingText = {
                            emailError?.let { Text(it, color = MaterialTheme.colorScheme.error) }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = customColors,
                        shape = MaterialTheme.shapes.medium,
                        singleLine = true
                    )
                }

                item {
                    OutlinedTextField(
                        value = telefono,
                        onValueChange = { telefono = it },
                        label = { Text("Teléfono") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = customColors,
                        shape = MaterialTheme.shapes.medium,
                        singleLine = true
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = {
                            val emailValidation = validateEmail(email)
                            val nifValidation = validateNIF(nif)
                            val cpValidation = validateCodigoPostal(codigoPostal)

                            emailError = emailValidation
                            nifError = nifValidation
                            codigoPostalError = cpValidation

                            val isValid = listOf(emailValidation, nifValidation, cpValidation).all { it == null }

                            if (isValid) {
                                val cliente = Cliente(
                                    nombre = nombre,
                                    nif = nif,
                                    direccion = direccion,
                                    ciudad = ciudad,
                                    codigoPostal = codigoPostal,
                                    provincia = provincia,
                                    pais = pais,
                                    email = email,
                                    telefono = telefono
                                )
                                if (clienteId == null) {
                                    viewModel.createCliente(cliente)
                                } else {
                                    viewModel.updateCliente(clienteId, cliente)
                                }
                                onFormSubmit()
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFFFA500),
                            contentColor = Color.Black
                        )
                    ) {
                        Text(text = "Guardar")
                    }
                }
            }
        }
    }
}