package es.tecnicalman.model

data class LoginRequest(
    val email: String,
    val password: String
)