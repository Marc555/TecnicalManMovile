package es.tecnicalman.repository

import android.os.Build
import androidx.annotation.RequiresApi
import es.tecnicalman.api.AlbaranApiService
import es.tecnicalman.api.RetrofitInstance
import es.tecnicalman.model.Albaran
import es.tecnicalman.model.LineaAlbaran

@RequiresApi(Build.VERSION_CODES.O)
class AlbaranRepository(private val apiService: AlbaranApiService = RetrofitInstance.albaranApiService) {
    suspend fun getAlbaranes() = apiService.getAlbaranes()
    suspend fun getAlbaranById(id: Long) = apiService.getAlbaranById(id)
    suspend fun createAlbaran(albaran: Albaran) = apiService.createAlbaran(albaran)
    suspend fun updateAlbaran(id: Long, albaran: Albaran) = apiService.updateAlbaran(id, albaran)
    suspend fun deleteAlbaran(id: Long) = apiService.deleteAlbaran(id)

    suspend fun getLineasAlbaran() = apiService.getLineasAlbaran()
    suspend fun getLineaAlbaranById(id: Long) = apiService.getLineaAlbaranById(id)
    suspend fun createLineaAlbaran(lineaAlbaran: LineaAlbaran) = apiService.createLineaAlbaran(lineaAlbaran)
    suspend fun updateLineaAlbaran(id: Long, lineaAlbaran: LineaAlbaran) = apiService.updateLineaAlbaran(id, lineaAlbaran)
    suspend fun deleteLineaAlbaran(id: Long) = apiService.deleteLineaAlbaran(id)
}