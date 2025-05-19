package es.tecnicalman.api

import es.tecnicalman.model.Presupuesto
import es.tecnicalman.model.LineaPresupuesto
import retrofit2.Response
import retrofit2.http.*

interface PresupuestoApiService {
    @GET("presupuestos")
    suspend fun getPresupuestos(): Response<List<Presupuesto>>

    @GET("presupuestos/{id}")
    suspend fun getPresupuestoById(@Path("id") id: Long): Response<Presupuesto>

    @POST("presupuestos")
    suspend fun createPresupuesto(@Body presupuesto: Presupuesto): Response<Presupuesto>

    @PUT("presupuestos/{id}")
    suspend fun updatePresupuesto(@Path("id") id: Long, @Body presupuesto: Presupuesto): Response<Presupuesto>

    @DELETE("presupuestos/{id}")
    suspend fun deletePresupuesto(@Path("id") id: Long): Response<Void>

    @GET("lineas-presupuesto")
    suspend fun getLineasPresupuesto(): Response<List<LineaPresupuesto>>

    @GET("lineas-presupuesto/{id}")
    suspend fun getLineaPresupuestoById(@Path("id") id: Long): Response<LineaPresupuesto>

    @POST("lineas-presupuesto")
    suspend fun createLineaPresupuesto(@Body lineaPresupuesto: LineaPresupuesto): Response<LineaPresupuesto>

    @PUT("lineas-presupuesto/{id}")
    suspend fun updateLineaPresupuesto(@Path("id") id: Long, @Body lineaPresupuesto: LineaPresupuesto): Response<LineaPresupuesto>

    @DELETE("lineas-presupuesto/{id}")
    suspend fun deleteLineaPresupuesto(@Path("id") id: Long): Response<Void>
}