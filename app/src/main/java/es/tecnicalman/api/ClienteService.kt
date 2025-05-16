package es.tecnicalman.api

import es.tecnicalman.model.Cliente
import retrofit2.Response
import retrofit2.http.*

interface ClienteService {
    @GET("clientes")
    suspend fun getAllClientes(): Response<List<Cliente>>

    @GET("clientes/{id}")
    suspend fun getClienteById(@Path("id") id: Long?): Response<Cliente>

    @POST("clientes")
    suspend fun createCliente(@Body cliente: Cliente): Response<Cliente>

    @PUT("clientes/{id}")
    suspend fun updateCliente(@Path("id") id: Long, @Body cliente: Cliente): Response<Cliente>

    @DELETE("clientes/{id}")
    suspend fun deleteCliente(@Path("id") id: Long): Response<Void>
}