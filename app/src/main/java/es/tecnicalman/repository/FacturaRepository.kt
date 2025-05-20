package es.tecnicalman.repository

import android.os.Build
import androidx.annotation.RequiresApi
import es.tecnicalman.api.FacturaApiService
import es.tecnicalman.api.RetrofitInstance
import es.tecnicalman.model.Factura
import es.tecnicalman.model.LineaFactura

@RequiresApi(Build.VERSION_CODES.O)
class FacturaRepository(private val apiService: FacturaApiService = RetrofitInstance.facturaApiService) {
    suspend fun getFacturas() = apiService.getFacturas()
    suspend fun getFacturaById(id: Long) = apiService.getFacturaById(id)
    suspend fun createFactura(factura: Factura) = apiService.createFactura(factura)
    suspend fun updateFactura(id: Long, factura: Factura) = apiService.updateFactura(id, factura)
    suspend fun deleteFactura(id: Long) = apiService.deleteFactura(id)

    suspend fun getLineasFactura() = apiService.getLineasFactura()
    suspend fun getLineaFacturaById(id: Long) = apiService.getLineaFacturaById(id)
    suspend fun createLineaFactura(lineaFactura: LineaFactura) = apiService.createLineaFactura(lineaFactura)
    suspend fun updateLineaFactura(id: Long, lineaFactura: LineaFactura) = apiService.updateLineaFactura(id, lineaFactura)
    suspend fun deleteLineaFactura(id: Long) = apiService.deleteLineaFactura(id)
}