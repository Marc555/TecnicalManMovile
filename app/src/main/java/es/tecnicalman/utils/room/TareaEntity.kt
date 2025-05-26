package es.tecnicalman.utils.room

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Entity(tableName = "tareas")
data class TareaEntity(
    @PrimaryKey val id: Long,
    val titulo: String,
    val descripcion: String,
    val encargado: String,
    val direccion: String,
    val estado: String,
    val fechaHora: Double
) {
    // Metodo para convertir el timestamp a Instant
    @RequiresApi(Build.VERSION_CODES.O)
    fun getFechaHoraAsInstant(): Instant {
        return Instant.ofEpochSecond(fechaHora.toLong())
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getFormattedFechaHora(): String {
        val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")
            .withZone(ZoneId.systemDefault()) // Usa la zona horaria del sistema
        return formatter.format(getFechaHoraAsInstant())
    }
}