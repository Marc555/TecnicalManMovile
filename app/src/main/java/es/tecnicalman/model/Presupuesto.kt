package es.tecnicalman.model

import java.time.Instant

data class Presupuesto(
    val id: Long? = null,
    val idCliente: Long,
    val titulo: String,
    val condiciones: String,
    val fechaEmitida: Instant? = null,
    val fechaValidez: Instant? = null,
    val estado: EstadoPresupuesto
) {
    enum class EstadoPresupuesto {
        BORRADOR, ACEPTADO, RECHAZADO
    }
}

data class LineaPresupuesto(
    val id: Long? = null,
    val idPresupuesto: Long,
    val descripcion: String,
    val cantidad: Long,
    val precioUnitario: Double
)