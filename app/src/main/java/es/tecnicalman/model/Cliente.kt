package es.tecnicalman.model

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

data class Cliente(
    val id: Long? = null,
    val fechaCreacion: Instant? = null,
    val nombre: String,
    val nif: String,
    val direccion: String,
    val ciudad: String,
    val codigoPostal: String,
    val provincia: String,
    val pais: String,
    val email: String,
    val telefono: String
) {

    @RequiresApi(Build.VERSION_CODES.O)
    fun fechaCreacionFormateada(): String {
        return fechaCreacion?.atZone(ZoneId.systemDefault())
            ?.format(DateTimeFormatter.ofPattern("d MMMM yyyy, HH:mm", Locale("es", "ES")))
            ?: "Sin fecha"
    }
}