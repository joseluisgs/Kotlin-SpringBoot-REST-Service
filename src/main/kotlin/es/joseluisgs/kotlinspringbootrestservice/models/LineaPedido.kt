package es.joseluisgs.kotlinspringbootrestservice.models

import com.fasterxml.jackson.annotation.JsonBackReference
import javax.persistence.*

@Entity
data class LineaPedido(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: Long,

    // Relación muchos a uno. Una linea de pedido tiene un producto y un producto tiene una linea de pedido
    // LP -> Producto
    @ManyToOne
    @JoinColumn(name = "producto_id")
    val producto: Producto,

    val precio: Double,
    val cantidad: Int,

    // Una Línea de pedido pertenece a un pedido Bidireccional LP -> P
    @JsonBackReference //Evita la recursividad infinita
    @ManyToOne
    @JoinColumn(name = "pedido_id")
    var pedido: Pedido?


) {
    fun subTotal() = precio * cantidad
}