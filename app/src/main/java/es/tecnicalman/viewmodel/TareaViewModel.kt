package es.tecnicalman.viewmodel

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import es.tecnicalman.api.RetrofitInstance
import es.tecnicalman.model.Tarea
import es.tecnicalman.model.TareaCreate
import es.tecnicalman.utils.room.NetworkUtils
import es.tecnicalman.utils.room.TareaDao
import es.tecnicalman.utils.room.TareaEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

@RequiresApi(Build.VERSION_CODES.O)
class TareaViewModel(
    private val tareaDao: TareaDao,
    private val context: Context
) : ViewModel() {

    private val tareaService = RetrofitInstance.tareaService

    private val _tarea = MutableStateFlow<List<Tarea>>(emptyList())
    val tarea: StateFlow<List<Tarea>> get() = _tarea

    private val _tareasHoy = MutableStateFlow<List<TareaEntity>>(emptyList())
    val tareasHoy: StateFlow<List<TareaEntity>> = _tareasHoy

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> get() = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> get() = _error

    // Obtener todas las tareas, sincronizar y manejar offline
    fun fetchTasks() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            if (NetworkUtils.isNetworkAvailable(context)) {
                try {
                    val response = tareaService.getTasks()
                    if (response.isSuccessful) {
                        val tareas = response.body() ?: emptyList()
                        _tarea.value = tareas
                        // Guarda en Room (borrando todo primero)
                        tareaDao.clearAll()
                        tareaDao.insertAll(tareas.map { it.toTareaEntity() })
                    } else {
                        _error.value = "Error al obtener tareas: ${response.message()}"
                        val tareasHoy = getTareasDeHoyRoom()
                        _tarea.value = tareasHoy.map { it.toTarea() }
                        _tareasHoy.value = tareasHoy
                    }
                } catch (e: Exception) {
                    _error.value = "Excepción: ${e.message}"
                    val tareasHoy = getTareasDeHoyRoom()
                    _tarea.value = tareasHoy.map { it.toTarea() }
                    _tareasHoy.value = tareasHoy
                }
            } else {
                val tareasHoy = getTareasDeHoyRoom()
                _tarea.value = tareasHoy.map { it.toTarea() }
                _tareasHoy.value = tareasHoy
                _error.value = "No hay conexión. Mostrando tareas locales."
            }
            _isLoading.value = false
        }
    }

    fun fetchTareasDeHoy() {
        viewModelScope.launch {
            val tareasHoy = getTareasDeHoyRoom()
            _tareasHoy.value = tareasHoy
        }
    }

    private suspend fun getTareasDeHoyRoom(): List<TareaEntity> {
        val allTareas = tareaDao.getAll()
        val hoy = LocalDate.now()
        return allTareas.filter { tarea ->
            // fechaHora debe ser Double, Long, o String convertible a Long (timestamp en segundos)
            val ts = try {
                tarea.fechaHora.toLong()
            } catch (e: Exception) {
                tarea.fechaHora.toDouble().toLong()
            }
            val date = Instant.ofEpochSecond(ts)
                .atZone(ZoneId.systemDefault())
                .toLocalDate()
            date == hoy
        }
    }

    fun createTask(tarea: Tarea) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
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
                    fetchTasks()
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

    fun updateTask(id: Long, tarea: Tarea) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val response = tareaService.updateTask(id, tarea)
                if (response.isSuccessful) {
                    fetchTasks()
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

    fun deleteTask(id: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val response = tareaService.deleteTask(id)
                if (response.isSuccessful) {
                    fetchTasks()
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

// Extensiones para conversión entre modelos
@RequiresApi(Build.VERSION_CODES.O)
fun Tarea.toTareaEntity() = TareaEntity(
    id = id,
    titulo = titulo,
    descripcion = descripcion,
    encargado = encargado,
    direccion = direccion,
    estado = estado,
    fechaHora = fechaHora
)

fun TareaEntity.toTarea() = Tarea(
    id = id,
    titulo = titulo,
    descripcion = descripcion,
    encargado = encargado,
    direccion = direccion,
    estado = estado,
    fechaHora = fechaHora
)