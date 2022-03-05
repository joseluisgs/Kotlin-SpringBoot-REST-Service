package es.joseluisgs.kotlinspringbootrestservice.models

import com.fasterxml.jackson.annotation.JsonBackReference
import javax.persistence.*

@Entity
data class LineaPedido(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: Long,
    val precio: Double,
    val cantidad: Int,

    // Mi relaciones

    // Relación muchos a uno. Una linea de pedido tiene un producto y un producto tiene una linea de pedido
    // LP -> Producto
    @ManyToOne
    @JoinColumn(name = "producto_id")
    val producto: Producto,

    // Una Línea de pedido pertenece a un pedido Bidireccional LP -> P
    @ManyToOne
    @JoinColumn(name = "pedido_id")
    @JsonBackReference // Evitamos recursividad
    var pedido: Pedido?
) {
    /// En vez de una función creo una propiedad claculada, es decir cuando quieran adquirir el getter
    val subTotal
        get() = this.precio * this.cantidad
}