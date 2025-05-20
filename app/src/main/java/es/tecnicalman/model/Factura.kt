package es.tecnicalman.model

import java.time.Instant

data class Factura(
    val id: Long? = null,
    val idCliente: Long,
    val titulo: String,
    val condiciones: String,
    val fechaEmitida: Instant? = null,
    val fechaValidez: Instant? = null,
    val estado: EstadoFactura
) {
    enum class EstadoFactura {
        BORRADOR, ACEPTADO, RECHAZADO
    }
}

data class LineaFactura(
    val id: Long? = null,
    val idFactura: Long,
    val descripcion: String,
    val cantidad: Long,
    val precioUnitario: Double
)