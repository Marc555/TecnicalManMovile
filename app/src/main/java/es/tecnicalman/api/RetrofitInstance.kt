package es.tecnicalman.api

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.google.gson.*
import es.tecnicalman.utils.TokenProvider
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.lang.reflect.Type
import java.time.Instant
import java.util.concurrent.TimeUnit

// Serializador y deserializador para Instant
class InstantAdapter : JsonSerializer<Instant?>, JsonDeserializer<Instant?> {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun serialize(
        src: Instant?,
        typeOfSrc: Type?,
        context: JsonSerializationContext?
    ): JsonElement? {
        return src?.let { JsonPrimitive(it.toString()) } ?: JsonNull.INSTANCE
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): Instant? {
        return if (json == null || json.isJsonNull) null
        // Permite aceptar tanto string ISO como long epoch
        else if (json.isJsonPrimitive && json.asJsonPrimitive.isString)
            Instant.parse(json.asString)
        else if (json.isJsonPrimitive && json.asJsonPrimitive.isNumber)
            Instant.ofEpochMilli(json.asLong)
        else null
    }
}

@RequiresApi(Build.VERSION_CODES.O)
object RetrofitInstance {

    private const val BASE_URL = "http://10.0.2.2:8080/api/"

    // Usa el adaptador completo para serializar y deserializar Instant correctamente
    @RequiresApi(Build.VERSION_CODES.O)
    private val gson: Gson = GsonBuilder()
        .registerTypeAdapter(Instant::class.java, InstantAdapter())
        .create()

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })
        .addInterceptor(Interceptor { chain ->
            val original = chain.request()
            val requestBuilder = original.newBuilder()
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")

            val path = original.url.encodedPath
            Log.d("RetrofitInstance", "Request Path: $path")

            // Excluir la cabecera de autenticación para rutas específicas, como "/auth"
            if (!path.contains("/auth")) {
                val token = TokenProvider.token
                if (!token.isNullOrBlank()) {
                    requestBuilder.header("Authorization", "Bearer $token")
                    Log.d("RetrofitInstance", "Token añadido: $token")
                } else {
                    Log.w("RetrofitInstance", "Token no disponible")
                }
            } else {
                Log.d("RetrofitInstance", "Ruta excluida de autenticación: $path")
            }

            val request = requestBuilder.method(original.method, original.body).build()
            Log.d("RetrofitInstance", "Request: ${request.method} ${request.url}")
            val response = chain.proceed(request)
            Log.d("RetrofitInstance", "Response: ${response.code} ${response.message}")
            response
        })
        .build()

    val authService: AuthService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(AuthService::class.java)
    }

    val tareaService: TaskService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(TaskService::class.java)
    }

    val clienteService: ClienteService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(ClienteService::class.java)
    }

    val presupuestoApiService: PresupuestoApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(PresupuestoApiService::class.java)
    }

    val albaranApiService: AlbaranApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(AlbaranApiService::class.java)
    }

    val facturaApiService: FacturaApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(FacturaApiService::class.java)
    }
}