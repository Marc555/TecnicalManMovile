package es.tecnicalman.util

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

val Context.dataStore by preferencesDataStore(name = "auth")

object TokenManager {

    private val TOKEN_KEY = stringPreferencesKey("jwt_token")

    // Contexto de aplicación accesible desde cualquier parte
    lateinit var appContext: Context
        private set

    fun init(context: Context) {
        appContext = context.applicationContext

        // Cargar el token en memoria al iniciar
        CoroutineScope(Dispatchers.IO).launch {
            val token = getToken(appContext)
            TokenProvider.token = token
        }
    }

    suspend fun saveToken(context: Context, token: String) {
        context.dataStore.edit { preferences ->
            preferences[TOKEN_KEY] = token
        }
        // Actualizar el token en memoria
        TokenProvider.token = token
    }

    suspend fun getToken(context: Context): String? {
        return context.dataStore.data.map { it[TOKEN_KEY] }.first()
    }

    suspend fun clearToken(context: Context) {
        context.dataStore.edit { it.remove(TOKEN_KEY) }
        // Eliminar el token de la memoria
        TokenProvider.token = null
    }
}