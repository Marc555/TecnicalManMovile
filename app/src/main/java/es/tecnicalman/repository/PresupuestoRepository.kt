package es.tecnicalman.repository

import android.os.Build
import androidx.annotation.RequiresApi
import es.tecnicalman.api.PresupuestoApiService
import es.tecnicalman.api.RetrofitInstance
import es.tecnicalman.model.Presupuesto
import es.tecnicalman.model.LineaPresupuesto

@RequiresApi(Build.VERSION_CODES.O)
class PresupuestoRepository(private val apiService: PresupuestoApiService = RetrofitInstance.presupuestoApiService) {
    suspend fun getPresupuestos() = apiService.getPresupuestos()
    suspend fun getPresupuestoById(id: Long) = apiService.getPresupuestoById(id)
    suspend fun createPresupuesto(presupuesto: Presupuesto) = apiService.createPresupuesto(presupuesto)
    suspend fun updatePresupuesto(id: Long, presupuesto: Presupuesto) = apiService.updatePresupuesto(id, presupuesto)
    suspend fun deletePresupuesto(id: Long) = apiService.deletePresupuesto(id)

    suspend fun getLineasPresupuesto() = apiService.getLineasPresupuesto()
    suspend fun getLineaPresupuestoById(id: Long) = apiService.getLineaPresupuestoById(id)
    suspend fun createLineaPresupuesto(lineaPresupuesto: LineaPresupuesto) = apiService.createLineaPresupuesto(lineaPresupuesto)
    suspend fun updateLineaPresupuesto(id: Long, lineaPresupuesto: LineaPresupuesto) = apiService.updateLineaPresupuesto(id, lineaPresupuesto)
    suspend fun deleteLineaPresupuesto(id: Long) = apiService.deleteLineaPresupuesto(id)
}