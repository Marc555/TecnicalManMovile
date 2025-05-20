package es.tecnicalman.viewmodel

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import es.tecnicalman.model.Albaran
import es.tecnicalman.model.LineaAlbaran
import es.tecnicalman.repository.AlbaranRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
class AlbaranViewModel(
    private val repository: AlbaranRepository = AlbaranRepository()
) : ViewModel() {

    private val TAG = "AlbaranViewModel"

    private val _albaranes = MutableStateFlow<List<Albaran>>(emptyList())
    val albaranes: StateFlow<List<Albaran>> get() = _albaranes

    private val _lineasAlbaran = MutableStateFlow<List<LineaAlbaran>>(emptyList())
    val lineasAlbaran: StateFlow<List<LineaAlbaran>> get() = _lineasAlbaran

    private val _albaranDetail = MutableStateFlow<Albaran?>(null)
    val albaranDetail: StateFlow<Albaran?> get() = _albaranDetail

    private val _lineasAlbaranDetail = MutableStateFlow<List<LineaAlbaran>>(emptyList())
    val lineasAlbaranDetail: StateFlow<List<LineaAlbaran>> get() = _lineasAlbaranDetail

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> get() = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> get() = _errorMessage

    private val gson = Gson()

    fun loadAlbaranes() {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = repository.getAlbaranes()
                if (response.isSuccessful) {
                    _albaranes.value = response.body() ?: emptyList()
                } else {
                    _errorMessage.value = "Error al cargar albaranes: ${response.message()}"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadLineasAlbaran() {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = repository.getLineasAlbaran()
                if (response.isSuccessful) {
                    _lineasAlbaran.value = response.body() ?: emptyList()
                } else {
                    _errorMessage.value = "Error al cargar líneas de albarán: ${response.message()}"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun createAlbaran(albaran: Albaran) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = repository.createAlbaran(albaran)
                if (response.isSuccessful) {
                    loadAlbaranes()
                } else {
                    _errorMessage.value = "Error al crear albarán: ${response.message()}"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun createAlbaranWithLineas(
        albaran: Albaran,
        lineas: List<LineaAlbaran>
    ) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = repository.createAlbaran(albaran)
                if (response.isSuccessful) {
                    val albaranCreado = response.body()
                    if (albaranCreado?.id != null) {
                        for (linea in lineas) {
                            val lineaConId = linea.copy(idAlbaran = albaranCreado.id)
                            val lineaResponse = repository.createLineaAlbaran(lineaConId)
                            if (!lineaResponse.isSuccessful) {
                                _errorMessage.value = "Error al crear línea: ${lineaResponse.message()}"
                                break
                            }
                        }
                        loadAlbaranes()
                        loadLineasAlbaran()
                    } else {
                        _errorMessage.value = "El albarán creado no tiene id"
                    }
                } else {
                    _errorMessage.value = "Error al crear albarán: ${response.message()}"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Excepción: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateAlbaran(id: Long, albaran: Albaran) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = repository.updateAlbaran(id, albaran)
                if (response.isSuccessful) {
                    loadAlbaranes()
                } else {
                    _errorMessage.value = "Error al actualizar albarán: ${response.message()}"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deleteAlbaran(id: Long) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = repository.deleteAlbaran(id)
                if (response.isSuccessful) {
                    loadAlbaranes()
                } else {
                    _errorMessage.value = "Error al eliminar albarán: ${response.message()}"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun createLineaAlbaran(lineaAlbaran: LineaAlbaran) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = repository.createLineaAlbaran(lineaAlbaran)
                if (response.isSuccessful) {
                    loadLineasAlbaran()
                } else {
                    _errorMessage.value = "Error al crear línea de albarán: ${response.message()}"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateLineaAlbaran(id: Long, lineaAlbaran: LineaAlbaran) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = repository.updateLineaAlbaran(id, lineaAlbaran)
                if (response.isSuccessful) {
                    loadLineasAlbaran()
                } else {
                    _errorMessage.value = "Error al actualizar línea de albarán: ${response.message()}"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadAlbaranById(id: Long) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = repository.getAlbaranById(id)
                if (response.isSuccessful) {
                    _albaranDetail.value = response.body()
                } else {
                    _errorMessage.value = "Error al cargar albarán: ${response.message()}"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadLineasByAlbaranId(idAlbaran: Long) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = repository.getLineasAlbaran()
                if (response.isSuccessful) {
                    _lineasAlbaranDetail.value = response.body()?.filter { it.idAlbaran == idAlbaran } ?: emptyList()
                } else {
                    _errorMessage.value = "Error al cargar líneas de albarán: ${response.message()}"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deleteLineaAlbaran(id: Long) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = repository.deleteLineaAlbaran(id)
                if (response.isSuccessful) {
                    loadLineasAlbaran()
                } else {
                    _errorMessage.value = "Error al eliminar línea de albarán: ${response.message()}"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}