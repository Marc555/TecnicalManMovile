package es.tecnicalman.api

import es.tecnicalman.model.Factura
import es.tecnicalman.model.LineaFactura
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface FacturaApiService {
    @GET("facturas")
    suspend fun getFacturas(): Response<List<Factura>>

    @GET("facturas/{id}")
    suspend fun getFacturaById(@Path("id") id: Long): Response<Factura>

    @POST("facturas")
    suspend fun createFactura(@Body factura: Factura): Response<Factura>

    @PUT("facturas/{id}")
    suspend fun updateFactura(@Path("id") id: Long, @Body factura: Factura): Response<Factura>

    @DELETE("facturas/{id}")
    suspend fun deleteFactura(@Path("id") id: Long): Response<Void>

    @GET("lineas-factura")
    suspend fun getLineasFactura(): Response<List<LineaFactura>>

    @GET("lineas-factura/{id}")
    suspend fun getLineaFacturaById(@Path("id") id: Long): Response<LineaFactura>

    @POST("lineas-factura")
    suspend fun createLineaFactura(@Body lineaFactura: LineaFactura): Response<LineaFactura>

    @PUT("lineas-factura/{id}")
    suspend fun updateLineaFactura(@Path("id") id: Long, @Body lineaFactura: LineaFactura): Response<LineaFactura>

    @DELETE("lineas-factura/{id}")
    suspend fun deleteLineaFactura(@Path("id") id: Long): Response<Void>

    // PDF
    @GET("facturapdf/{id}")
    suspend fun downloadPdf(@Path("id") id: Long): ResponseBody
}