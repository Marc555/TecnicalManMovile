package es.tecnicalman.model

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

data class Tarea(
    val id: Long,
    val titulo: String,
    val descripcion: String,
    val encargado: String, // JAIME, PABLO, AMBOS
    val direccion: String,
    val estado: String, // PENDIENTE, EN_PROGRESO, COMPLETADA, CANCELADA
    val fechaHora: Double // Ahora es Double en lugar de Instant
) {
    // Metodo para convertir el timestamp a Instant
    @RequiresApi(Build.VERSION_CODES.O)
    fun getFechaHoraAsInstant(): Instant {
        return Instant.ofEpochSecond(fechaHora.toLong())
    }

    // Metodo para obtener fechaHora en formato legible
    @RequiresApi(Build.VERSION_CODES.O)
    fun getFormattedFechaHora(): String {
        val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")
            .withZone(ZoneId.systemDefault()) // Usa la zona horaria del sistema
        return formatter.format(getFechaHoraAsInstant())
    }
}