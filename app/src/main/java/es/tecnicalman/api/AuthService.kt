package es.tecnicalman.api

import es.tecnicalman.model.LoginRequest
import es.tecnicalman.model.LoginResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface AuthService {
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @GET("auth/validate")
    suspend fun validateToken(@Header("Authorization") token: String): Response<String>

    @POST("auth/forgot-password")
    suspend fun forgotPassword(@Body request: Map<String, String>): Response<Map<String, String>>
}