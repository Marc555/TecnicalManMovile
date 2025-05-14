package es.tecnicalman.model

data class TareaCreate(
    val titulo: String,
    val descripcion: String,
    val encargado: String, // JAIME, PABLO, AMBOS
    val direccion: String,
    val estado: String, // PENDIENTE, EN_PROGRESO, COMPLETADA, CANCELADA
    val fechaHora: Double // Ahora es Double en lugar de Instant
)