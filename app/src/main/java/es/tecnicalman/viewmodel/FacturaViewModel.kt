package es.tecnicalman.viewmodel

import android.content.Context
import android.os.Build
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import es.tecnicalman.model.Factura
import es.tecnicalman.model.LineaFactura
import es.tecnicalman.repository.FacturaRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

@RequiresApi(Build.VERSION_CODES.O)
class FacturaViewModel(
    private val repository: FacturaRepository = FacturaRepository()
) : ViewModel() {

    private val TAG = "FacturaViewModel"

    private val _facturas = MutableStateFlow<List<Factura>>(emptyList())
    val facturas: StateFlow<List<Factura>> get() = _facturas

    private val _lineasFactura = MutableStateFlow<List<LineaFactura>>(emptyList())
    val lineasFactura: StateFlow<List<LineaFactura>> get() = _lineasFactura

    private val _facturaDetail = MutableStateFlow<Factura?>(null)
    val facturaDetail: StateFlow<Factura?> get() = _facturaDetail

    private val _lineasFacturaDetail = MutableStateFlow<List<LineaFactura>>(emptyList())
    val lineasFacturaDetail: StateFlow<List<LineaFactura>> get() = _lineasFacturaDetail

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> get() = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> get() = _errorMessage

    private val gson = Gson()

    fun loadFacturas() {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = repository.getFacturas()
                if (response.isSuccessful) {
                    _facturas.value = response.body() ?: emptyList()
                } else {
                    _errorMessage.value = "Error al cargar facturas: ${response.message()}"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadLineasFactura() {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = repository.getLineasFactura()
                if (response.isSuccessful) {
                    _lineasFactura.value = response.body() ?: emptyList()
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

    fun createFactura(factura: Factura) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = repository.createFactura(factura)
                if (response.isSuccessful) {
                    loadFacturas()
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

    fun createFacturaWithLineas(
        factura: Factura,
        lineas: List<LineaFactura>
    ) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = repository.createFactura(factura)
                if (response.isSuccessful) {
                    val facturaCreado = response.body()
                    if (facturaCreado?.id != null) {
                        for (linea in lineas) {
                            val lineaConId = linea.copy(idFactura = facturaCreado.id)
                            val lineaResponse = repository.createLineaFactura(lineaConId)
                            if (!lineaResponse.isSuccessful) {
                                _errorMessage.value = "Error al crear línea: ${lineaResponse.message()}"
                                break
                            }
                        }
                        loadFacturas()
                        loadLineasFactura()
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

    fun updateFactura(id: Long, factura: Factura) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = repository.updateFactura(id, factura)
                if (response.isSuccessful) {
                    loadFacturas()
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

    fun deleteFactura(id: Long) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = repository.deleteFactura(id)
                if (response.isSuccessful) {
                    loadFacturas()
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

    fun createLineaFactura(lineaFactura: LineaFactura) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = repository.createLineaFactura(lineaFactura)
                if (response.isSuccessful) {
                    loadLineasFactura()
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

    fun updateLineaFactura(id: Long, lineaFactura: LineaFactura) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = repository.updateLineaFactura(id, lineaFactura)
                if (response.isSuccessful) {
                    loadLineasFactura()
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

    fun loadFacturaById(id: Long) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = repository.getFacturaById(id)
                if (response.isSuccessful) {
                    _facturaDetail.value = response.body()
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

    fun loadLineasByFacturaId(idFactura: Long) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = repository.getLineasFactura()
                if (response.isSuccessful) {
                    _lineasFacturaDetail.value = response.body()?.filter { it.idFactura == idFactura } ?: emptyList()
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

    fun deleteLineaFactura(id: Long) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = repository.deleteLineaFactura(id)
                if (response.isSuccessful) {
                    loadLineasFactura()
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

    // PDF
    fun descargarFacturaPdf(context: Context, facturaId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val responseBody = repository.downloadPdf(facturaId)
                val fileName = "factura_FAC${facturaId.toString().padStart(4, '0')}.pdf"
                val file = File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), fileName)
                val inputStream: InputStream = responseBody.byteStream()
                val outputStream = FileOutputStream(file)
                inputStream.copyTo(outputStream)
                outputStream.close()
                inputStream.close()
                viewModelScope.launch(Dispatchers.Main) {
                    Toast.makeText(context, "PDF descargado en: ${file.absolutePath}", Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                viewModelScope.launch(Dispatchers.Main) {
                    Toast.makeText(context, "Error al descargar el PDF", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}