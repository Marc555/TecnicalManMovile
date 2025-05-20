package es.tecnicalman.api

import es.tecnicalman.model.Albaran
import es.tecnicalman.model.LineaAlbaran
import retrofit2.Response
import retrofit2.http.*

interface AlbaranApiService {
    @GET("albarans")
    suspend fun getAlbaranes(): Response<List<Albaran>>

    @GET("albarans/{id}")
    suspend fun getAlbaranById(@Path("id") id: Long): Response<Albaran>

    @POST("albarans")
    suspend fun createAlbaran(@Body albaran: Albaran): Response<Albaran>

    @PUT("albarans/{id}")
    suspend fun updateAlbaran(@Path("id") id: Long, @Body albaran: Albaran): Response<Albaran>

    @DELETE("albarans/{id}")
    suspend fun deleteAlbaran(@Path("id") id: Long): Response<Void>

    @GET("lineas-albaran")
    suspend fun getLineasAlbaran(): Response<List<LineaAlbaran>>

    @GET("lineas-albaran/{id}")
    suspend fun getLineaAlbaranById(@Path("id") id: Long): Response<LineaAlbaran>

    @POST("lineas-albaran")
    suspend fun createLineaAlbaran(@Body lineaAlbaran: LineaAlbaran): Response<LineaAlbaran>

    @PUT("lineas-albaran/{id}")
    suspend fun updateLineaAlbaran(@Path("id") id: Long, @Body lineaAlbaran: LineaAlbaran): Response<LineaAlbaran>

    @DELETE("lineas-albaran/{id}")
    suspend fun deleteLineaAlbaran(@Path("id") id: Long): Response<Void>
}