package es.tecnicalman.viewmodel

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import es.tecnicalman.api.AuthService
import es.tecnicalman.api.RetrofitInstance
import es.tecnicalman.model.LoginRequest
import es.tecnicalman.utils.TokenManager
import es.tecnicalman.utils.TokenProvider
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@RequiresApi(Build.VERSION_CODES.O)
class AuthViewModel(private val authService: AuthService = RetrofitInstance.authService) : ViewModel() {

    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> = _loginState

    private val _forgotPasswordState = MutableStateFlow<PasswordState>(PasswordState.Idle)
    val forgotPasswordState: StateFlow<PasswordState> = _forgotPasswordState

    fun login(email: String, password: String, context: Context) {
        viewModelScope.launch {
            _loginState.value = LoginState.Loading
            try {
                val response = authService.login(LoginRequest(email, password))
                when (response.code()) {
                    200 -> {
                        val token = response.body()?.token
                        if (token != null) {
                            TokenManager.saveToken(context, token)
                            TokenProvider.token = token // <-- Importante
                            _loginState.value = LoginState.Success(token)
                        } else {
                            _loginState.value = LoginState.Error("Token no recibido")
                        }
                    }
                    403 -> {
                        _loginState.value = LoginState.Error("Credenciales incorrectas")
                    }
                    else -> {
                        _loginState.value = LoginState.Error("Error del servidor: ${response.code()}")
                    }
                }
            } catch (e: Exception) {
                _loginState.value = LoginState.Error("Error de conexión: ${e.message}")
            }
        }
    }

    fun validateToken(context: Context, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val token = TokenManager.getToken(context)
            if (!token.isNullOrEmpty()) {
                TokenProvider.token = token // <-- También se puede cargar aquí por seguridad
                try {
                    val response = authService.validateToken("Bearer $token")
                    onResult(response.isSuccessful)
                } catch (e: Exception) {
                    onResult(false)
                }
            } else {
                onResult(false)
            }
        }
    }



    fun forgotPassword(email: String) {
        viewModelScope.launch {
            _forgotPasswordState.value = PasswordState.Loading
            try {
                val response = authService.forgotPassword(mapOf("email" to email))
                if (response.isSuccessful) {
                    _forgotPasswordState.value = PasswordState.Success("Correo enviado con éxito")
                } else {
                    _forgotPasswordState.value = PasswordState.Error("Error: ${response.message()}")
                }
            } catch (e: Exception) {
                _forgotPasswordState.value = PasswordState.Error("Error de conexión: ${e.message}")
            }
        }
    }
}

sealed class PasswordState {
    object Idle : PasswordState()
    object Loading : PasswordState()
    data class Success(val message: String) : PasswordState()
    data class Error(val message: String) : PasswordState()
}

sealed class LoginState {
    object Idle : LoginState()
    object Loading : LoginState()
    data class Success(val token: String) : LoginState()
    data class Error(val message: String) : LoginState()
}
