package es.tecnicalman.viewmodel

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import es.tecnicalman.model.Presupuesto
import es.tecnicalman.model.LineaPresupuesto
import es.tecnicalman.repository.PresupuestoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
class PresupuestoViewModel(
    private val repository: PresupuestoRepository = PresupuestoRepository()
) : ViewModel() {

    private val TAG = "PresupuestoViewModel"

    // Flujos de estado para presupuestos y líneas de presupuesto
    private val _presupuestos = MutableStateFlow<List<Presupuesto>>(emptyList())
    val presupuestos: StateFlow<List<Presupuesto>> get() = _presupuestos

    private val _lineasPresupuesto = MutableStateFlow<List<LineaPresupuesto>>(emptyList())
    val lineasPresupuesto: StateFlow<List<LineaPresupuesto>> get() = _lineasPresupuesto

    private val _presupuestoDetail = MutableStateFlow<Presupuesto?>(null)
    val presupuestoDetail: StateFlow<Presupuesto?> get() = _presupuestoDetail

    private val _lineasPresupuestoDetail = MutableStateFlow<List<LineaPresupuesto>>(emptyList())
    val lineasPresupuestoDetail: StateFlow<List<LineaPresupuesto>> get() = _lineasPresupuestoDetail

    // Flujos para manejar errores y estados de carga
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> get() = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> get() = _errorMessage

    private val gson = Gson()

    // Cargar todos los presupuestos
    fun loadPresupuestos() {
        Log.d(TAG, "loadPresupuestos: Iniciando carga de presupuestos")
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = repository.getPresupuestos()
                Log.d(TAG, "loadPresupuestos: response = ${response.code()} - ${response.message()}")
                if (response.isSuccessful) {
                    _presupuestos.value = response.body() ?: emptyList()
                    Log.d(TAG, "loadPresupuestos: presupuestos = ${_presupuestos.value.size}")
                } else {
                    _errorMessage.value = "Error al cargar presupuestos: ${response.message()}"
                    Log.e(TAG, "loadPresupuestos: Error response = ${response.code()} - ${response.message()}")
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error: ${e.message}"
                Log.e(TAG, "loadPresupuestos: Exception", e)
            } finally {
                _isLoading.value = false
                Log.d(TAG, "loadPresupuestos: Finalizado")
            }
        }
    }

    // Cargar todas las líneas de presupuesto
    fun loadLineasPresupuesto() {
        Log.d(TAG, "loadLineasPresupuesto: Iniciando carga de líneas de presupuesto")
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = repository.getLineasPresupuesto()
                Log.d(TAG, "loadLineasPresupuesto: response = ${response.code()} - ${response.message()}")
                if (response.isSuccessful) {
                    _lineasPresupuesto.value = response.body() ?: emptyList()
                    Log.d(TAG, "loadLineasPresupuesto: lineas = ${_lineasPresupuesto.value.size}")
                } else {
                    _errorMessage.value = "Error al cargar líneas de presupuesto: ${response.message()}"
                    Log.e(TAG, "loadLineasPresupuesto: Error response = ${response.code()} - ${response.message()}")
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error: ${e.message}"
                Log.e(TAG, "loadLineasPresupuesto: Exception", e)
            } finally {
                _isLoading.value = false
                Log.d(TAG, "loadLineasPresupuesto: Finalizado")
            }
        }
    }

    // Crear un nuevo presupuesto
    fun createPresupuesto(presupuesto: Presupuesto) {
        Log.d(TAG, "createPresupuesto: Iniciando creación de presupuesto: $presupuesto")
        // Mostrar el JSON que se enviará
        try {
            val json = gson.toJson(presupuesto)
            Log.d(TAG, "createPresupuesto: JSON a enviar: $json")
        } catch (e: Exception) {
            Log.e(TAG, "createPresupuesto: Error serializando presupuesto a JSON", e)
        }
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = repository.createPresupuesto(presupuesto)
                Log.d(TAG, "createPresupuesto: response = ${response.code()} - ${response.message()}")
                if (response.isSuccessful) {
                    Log.d(TAG, "createPresupuesto: Presupuesto creado correctamente")
                    loadPresupuestos() // Recargar la lista de presupuestos
                } else {
                    _errorMessage.value = "Error al crear presupuesto: ${response.message()}"
                    Log.e(TAG, "createPresupuesto: Error response = ${response.code()} - ${response.message()}")
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error: ${e.message}"
                Log.e(TAG, "createPresupuesto: Exception", e)
            } finally {
                _isLoading.value = false
                Log.d(TAG, "createPresupuesto: Finalizado")
            }
        }
    }

    /**
     * Crea un presupuesto y, tras recibir el id real, crea todas las líneas asociadas.
     * Guarda logs en cada paso para poder depurar el flujo.
     */
    fun createPresupuestoWithLineas(
        presupuesto: Presupuesto,
        lineas: List<LineaPresupuesto>
    ) {
        Log.d(TAG, "createPresupuestoWithLineas: Creando presupuesto $presupuesto")
        // Mostrar el JSON que se enviará
        try {
            val json = gson.toJson(presupuesto)
            Log.d(TAG, "createPresupuestoWithLineas: JSON a enviar: $json")
        } catch (e: Exception) {
            Log.e(TAG, "createPresupuestoWithLineas: Error serializando presupuesto a JSON", e)
        }
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val presupuestoResponse = repository.createPresupuesto(presupuesto)
                Log.d(TAG, "createPresupuestoWithLineas: Respuesta presupuesto: ${presupuestoResponse.code()} - ${presupuestoResponse.message()}")

                if (presupuestoResponse.isSuccessful) {
                    val presupuestoCreado = presupuestoResponse.body()
                    if (presupuestoCreado?.id != null) {
                        Log.d(TAG, "createPresupuestoWithLineas: Presupuesto creado con id=${presupuestoCreado.id}")
                        for (linea in lineas) {
                            val lineaConId = linea.copy(idPresupuesto = presupuestoCreado.id)
                            try {
                                val lineaJson = gson.toJson(lineaConId)
                                Log.d(TAG, "createPresupuestoWithLineas: JSON línea a enviar: $lineaJson")
                            } catch (e: Exception) {
                                Log.e(TAG, "createPresupuestoWithLineas: Error serializando línea a JSON", e)
                            }
                            val lineaResponse = repository.createLineaPresupuesto(lineaConId)
                            if (!lineaResponse.isSuccessful) {
                                Log.e(TAG, "createPresupuestoWithLineas: Error al crear línea: ${lineaResponse.code()} - ${lineaResponse.message()}")
                                _errorMessage.value = "Error al crear línea: ${lineaResponse.message()}"
                                break
                            }
                        }
                        loadPresupuestos()
                        loadLineasPresupuesto()
                        Log.d(TAG, "createPresupuestoWithLineas: Finalizado correctamente")
                    } else {
                        Log.e(TAG, "createPresupuestoWithLineas: El presupuesto creado no tiene id")
                        _errorMessage.value = "El presupuesto creado no tiene id"
                    }
                } else {
                    Log.e(TAG, "createPresupuestoWithLineas: Error al crear presupuesto: ${presupuestoResponse.code()} - ${presupuestoResponse.message()}")
                    _errorMessage.value = "Error al crear presupuesto: ${presupuestoResponse.message()}"
                }
            } catch (e: Exception) {
                Log.e(TAG, "createPresupuestoWithLineas: Excepción: ${e.message}", e)
                _errorMessage.value = "Excepción: ${e.message}"
            } finally {
                _isLoading.value = false
                Log.d(TAG, "createPresupuestoWithLineas: Finalizado (finally)")
            }
        }
    }

    // Actualizar un presupuesto existente
    fun updatePresupuesto(id: Long, presupuesto: Presupuesto) {
        Log.d(TAG, "updatePresupuesto: Iniciando actualización de presupuesto id=$id: $presupuesto")
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = repository.updatePresupuesto(id, presupuesto)
                Log.d(TAG, "updatePresupuesto: response = ${response.code()} - ${response.message()}")
                if (response.isSuccessful) {
                    Log.d(TAG, "updatePresupuesto: Presupuesto actualizado correctamente")
                    loadPresupuestos() // Recargar la lista de presupuestos
                } else {
                    _errorMessage.value = "Error al actualizar presupuesto: ${response.message()}"
                    Log.e(TAG, "updatePresupuesto: Error response = ${response.code()} - ${response.message()}")
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error: ${e.message}"
                Log.e(TAG, "updatePresupuesto: Exception", e)
            } finally {
                _isLoading.value = false
                Log.d(TAG, "updatePresupuesto: Finalizado")
            }
        }
    }

    // Eliminar un presupuesto
    fun deletePresupuesto(id: Long) {
        Log.d(TAG, "deletePresupuesto: Iniciando eliminación de presupuesto id=$id")
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = repository.deletePresupuesto(id)
                Log.d(TAG, "deletePresupuesto: response = ${response.code()} - ${response.message()}")
                if (response.isSuccessful) {
                    Log.d(TAG, "deletePresupuesto: Presupuesto eliminado correctamente")
                    loadPresupuestos() // Recargar la lista de presupuestos
                } else {
                    _errorMessage.value = "Error al eliminar presupuesto: ${response.message()}"
                    Log.e(TAG, "deletePresupuesto: Error response = ${response.code()} - ${response.message()}")
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error: ${e.message}"
                Log.e(TAG, "deletePresupuesto: Exception", e)
            } finally {
                _isLoading.value = false
                Log.d(TAG, "deletePresupuesto: Finalizado")
            }
        }
    }

    // Crear una nueva línea de presupuesto
    fun createLineaPresupuesto(lineaPresupuesto: LineaPresupuesto) {
        Log.d(TAG, "createLineaPresupuesto: Iniciando creación de línea: $lineaPresupuesto")
        try {
            val lineaJson = gson.toJson(lineaPresupuesto)
            Log.d(TAG, "createLineaPresupuesto: JSON línea a enviar: $lineaJson")
        } catch (e: Exception) {
            Log.e(TAG, "createLineaPresupuesto: Error serializando línea a JSON", e)
        }
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = repository.createLineaPresupuesto(lineaPresupuesto)
                Log.d(TAG, "createLineaPresupuesto: response = ${response.code()} - ${response.message()}")
                if (response.isSuccessful) {
                    Log.d(TAG, "createLineaPresupuesto: Línea creada correctamente")
                    loadLineasPresupuesto() // Recargar la lista de líneas de presupuesto
                } else {
                    _errorMessage.value = "Error al crear línea de presupuesto: ${response.message()}"
                    Log.e(TAG, "createLineaPresupuesto: Error response = ${response.code()} - ${response.message()}")
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error: ${e.message}"
                Log.e(TAG, "createLineaPresupuesto: Exception", e)
            } finally {
                _isLoading.value = false
                Log.d(TAG, "createLineaPresupuesto: Finalizado")
            }
        }
    }

    // Actualizar una línea de presupuesto existente
    fun updateLineaPresupuesto(id: Long, lineaPresupuesto: LineaPresupuesto) {
        Log.d(TAG, "updateLineaPresupuesto: Iniciando actualización de línea id=$id: $lineaPresupuesto")
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = repository.updateLineaPresupuesto(id, lineaPresupuesto)
                Log.d(TAG, "updateLineaPresupuesto: response = ${response.code()} - ${response.message()}")
                if (response.isSuccessful) {
                    Log.d(TAG, "updateLineaPresupuesto: Línea actualizada correctamente")
                    loadLineasPresupuesto() // Recargar la lista de líneas de presupuesto
                } else {
                    _errorMessage.value = "Error al actualizar línea de presupuesto: ${response.message()}"
                    Log.e(TAG, "updateLineaPresupuesto: Error response = ${response.code()} - ${response.message()}")
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error: ${e.message}"
                Log.e(TAG, "updateLineaPresupuesto: Exception", e)
            } finally {
                _isLoading.value = false
                Log.d(TAG, "updateLineaPresupuesto: Finalizado")
            }
        }
    }

    fun loadPresupuestoById(id: Long) {
        Log.d(TAG, "loadPresupuestoById: Iniciando carga de presupuesto id=$id")
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = repository.getPresupuestoById(id)
                Log.d(TAG, "loadPresupuestoById: response = ${response.code()} - ${response.message()}")
                if (response.isSuccessful) {
                    _presupuestoDetail.value = response.body()
                    Log.d(TAG, "loadPresupuestoById: presupuestoDetail = ${_presupuestoDetail.value}")
                } else {
                    _errorMessage.value = "Error al cargar presupuesto: ${response.message()}"
                    Log.e(TAG, "loadPresupuestoById: Error response = ${response.code()} - ${response.message()}")
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error: ${e.message}"
                Log.e(TAG, "loadPresupuestoById: Exception", e)
            } finally {
                _isLoading.value = false
                Log.d(TAG, "loadPresupuestoById: Finalizado")
            }
        }
    }

    fun loadLineasByPresupuestoId(idPresupuesto: Long) {
        Log.d(TAG, "loadLineasByPresupuestoId: Iniciando carga de líneas para presupuesto id=$idPresupuesto")
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = repository.getLineasPresupuesto()
                Log.d(TAG, "loadLineasByPresupuestoId: response = ${response.code()} - ${response.message()}")
                if (response.isSuccessful) {
                    _lineasPresupuestoDetail.value = response.body()?.filter { it.idPresupuesto == idPresupuesto } ?: emptyList()
                    Log.d(TAG, "loadLineasByPresupuestoId: lineasFiltradas = ${_lineasPresupuestoDetail.value.size}")
                } else {
                    _errorMessage.value = "Error al cargar líneas de presupuesto: ${response.message()}"
                    Log.e(TAG, "loadLineasByPresupuestoId: Error response = ${response.code()} - ${response.message()}")
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error: ${e.message}"
                Log.e(TAG, "loadLineasByPresupuestoId: Exception", e)
            } finally {
                _isLoading.value = false
                Log.d(TAG, "loadLineasByPresupuestoId: Finalizado")
            }
        }
    }

    // Eliminar una línea de presupuesto
    fun deleteLineaPresupuesto(id: Long) {
        Log.d(TAG, "deleteLineaPresupuesto: Iniciando eliminación de línea id=$id")
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = repository.deleteLineaPresupuesto(id)
                Log.d(TAG, "deleteLineaPresupuesto: response = ${response.code()} - ${response.message()}")
                if (response.isSuccessful) {
                    Log.d(TAG, "deleteLineaPresupuesto: Línea eliminada correctamente")
                    loadLineasPresupuesto() // Recargar la lista de líneas de presupuesto
                } else {
                    _errorMessage.value = "Error al eliminar línea de presupuesto: ${response.message()}"
                    Log.e(TAG, "deleteLineaPresupuesto: Error response = ${response.code()} - ${response.message()}")
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error: ${e.message}"
                Log.e(TAG, "deleteLineaPresupuesto: Exception", e)
            } finally {
                _isLoading.value = false
                Log.d(TAG, "deleteLineaPresupuesto: Finalizado")
            }
        }
    }
}