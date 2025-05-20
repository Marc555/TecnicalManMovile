package es.tecnicalman.model

import java.time.Instant

data class Albaran(
    val id: Long? = null,
    val idCliente: Long,
    val titulo: String,
    val condiciones: String,
    val fechaEmitida: Instant? = null,
    val fechaValidez: Instant? = null,
    val estado: EstadoAlbaran
) {
    enum class EstadoAlbaran {
        BORRADOR, ACEPTADO, RECHAZADO
    }
}

data class LineaAlbaran(
    val id: Long? = null,
    val idAlbaran: Long,
    val descripcion: String,
    val cantidad: Long,
    val precioUnitario: Double
)