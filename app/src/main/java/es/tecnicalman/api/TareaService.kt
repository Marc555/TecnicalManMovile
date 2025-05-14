package es.tecnicalman.api

import es.tecnicalman.model.Tarea
import es.tecnicalman.model.TareaCreate
import retrofit2.Response
import retrofit2.http.*

interface TaskService {
    @GET("tareas")
    suspend fun getTasks(): Response<List<Tarea>>

    @POST("tareas")
    suspend fun createTask(@Body task: TareaCreate): Response<TareaCreate>

    @PUT("tareas/{id}")
    suspend fun updateTask(@Path("id") id: Long, @Body task: Tarea): Response<Tarea>

    @DELETE("tareas/{id}")
    suspend fun deleteTask(@Path("id") id: Long): Response<Unit>
}