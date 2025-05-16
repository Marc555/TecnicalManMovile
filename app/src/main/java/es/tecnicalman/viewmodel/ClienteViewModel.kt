package es.tecnicalman.viewmodel

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import es.tecnicalman.api.ClienteService
import es.tecnicalman.api.RetrofitInstance
import es.tecnicalman.model.Cliente
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
class ClienteViewModel : ViewModel() {

    private val clienteService: ClienteService = RetrofitInstance.clienteService

    private val _clientes = MutableStateFlow<List<Cliente>>(emptyList())
    val clientes: StateFlow<List<Cliente>> = _clientes

    private val _clienteSeleccionado = MutableStateFlow<Cliente?>(null)
    val clienteSeleccionado: StateFlow<Cliente?> = _clienteSeleccionado

    private val _isLoading = MutableStateFlow(false) // Nuevo indicador de carga
    val isLoading: StateFlow<Boolean> = _isLoading

    fun fetchClientes() {
        viewModelScope.launch {
            _isLoading.value = true // Iniciar indicador de carga
            val response = clienteService.getAllClientes()
            if (response.isSuccessful) {
                _clientes.value = response.body() ?: emptyList()
            }
            _isLoading.value = false // Detener indicador de carga
        }
    }

    fun fetchClienteById(id: Long?) {
        viewModelScope.launch {
            _isLoading.value = true // Iniciar indicador de carga
            val response = clienteService.getClienteById(id)
            if (response.isSuccessful) {
                _clienteSeleccionado.value = response.body()
            }
            _isLoading.value = false // Detener indicador de carga
        }
    }

    fun createCliente(cliente: Cliente) {
        viewModelScope.launch {
            _isLoading.value = true // Iniciar indicador de carga
            clienteService.createCliente(cliente)
            fetchClientes()
            _isLoading.value = false // Detener indicador de carga
        }
    }

    fun updateCliente(id: Long, cliente: Cliente) {
        viewModelScope.launch {
            _isLoading.value = true // Iniciar indicador de carga
            clienteService.updateCliente(id, cliente)
            fetchClientes()
            _isLoading.value = false // Detener indicador de carga
        }
    }

    fun deleteCliente(id: Long) {
        viewModelScope.launch {
            _isLoading.value = true // Iniciar indicador de carga
            clienteService.deleteCliente(id)
            fetchClientes()
            _isLoading.value = false // Detener indicador de carga
        }
    }
}