package es.tecnicalman.viewmodel

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import es.tecnicalman.api.RetrofitInstance
import es.tecnicalman.model.Tarea
import es.tecnicalman.model.TareaCreate
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
class TareaViewModel : ViewModel() {

    // Usa el servicio de tareas desde RetrofitInstance
    private val tareaService = RetrofitInstance.tareaService

    // Estados observables
    private val _tarea = MutableStateFlow<List<Tarea>>(emptyList())
    val tarea: StateFlow<List<Tarea>> get() = _tarea

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> get() = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> get() = _error

    // Obtener todas las tareas
    fun fetchTasks() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val response = tareaService.getTasks()
                if (response.isSuccessful) {
                    _tarea.value = response.body() ?: emptyList()
                } else {
                    _error.value = "Error al obtener tareas: ${response.message()}"
                }
            } catch (e: Exception) {
                _error.value = "Excepción: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Crear una nueva tarea
    fun createTask(tarea: Tarea) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                // Transformar el objeto Tarea en un objeto TareaCreate
                val tareaCreate = TareaCreate(
                    titulo = tarea.titulo,
                    descripcion = tarea.descripcion,
                    encargado = tarea.encargado,
                    direccion = tarea.direccion,
                    estado = tarea.estado,
                    fechaHora = tarea.fechaHora
                )

                val response = tareaService.createTask(tareaCreate)
                if (response.isSuccessful) {
                    fetchTasks() // Actualiza la lista después de crear
                } else {
                    _error.value = "Error al crear tarea: ${response.message()}"
                    Log.e("TareaViewModel", "Error al crear tarea: ${response.message()}")
                    Log.e("TareaViewModel", "Código de respuesta: ${response.code()}")
                    Log.e("TareaViewModel", "Cuerpo del objeto: ${tareaCreate.toString()}")
                }
            } catch (e: Exception) {
                _error.value = "Excepción: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Actualizar una tarea existente
    fun updateTask(id: Long, tarea: Tarea) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val response = tareaService.updateTask(id, tarea)
                if (response.isSuccessful) {
                    fetchTasks() // Actualiza la lista después de actualizar
                } else {
                    _error.value = "Error al actualizar tarea: ${response.message()}"
                }
            } catch (e: Exception) {
                _error.value = "Excepción: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Eliminar una tarea
    fun deleteTask(id: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val response = tareaService.deleteTask(id)
                if (response.isSuccessful) {
                    fetchTasks() // Actualiza la lista después de eliminar
                } else {
                    _error.value = "Error al eliminar tarea: ${response.message()}"
                }
            } catch (e: Exception) {
                _error.value = "Excepción: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}