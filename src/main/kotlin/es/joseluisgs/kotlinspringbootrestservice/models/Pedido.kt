package es.joseluisgs.kotlinspringbootrestservice.models

import com.fasterxml.jackson.annotation.JsonManagedReference
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime
import javax.persistence.*

@Entity
@EntityListeners(AuditingEntityListener::class)
data class Pedido(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: Long,

    // Un pedido tiene un usuario, pero un usuario tiene muchos pedidos, unidireccional P->U
    @ManyToOne
    @JoinColumn(name = "cliente_id")
    val cliente: Usuario,

    @CreatedDate
    val fecha: LocalDateTime,

    // Un pedido tiene muchas lineas de pedido P -> LP (Bidreccional)
    @JsonManagedReference // para romper la recursividad usamos @JsonManagedReference
    @OneToMany(mappedBy = "pedido", cascade = [CascadeType.ALL], orphanRemoval = true)
    val lineasPedido: MutableSet<LineaPedido> = mutableSetOf()
) {
    fun total() = lineasPedido.sumOf { it.subTotal() }

    // Helper para manejar la recursividad
    fun addLineaPedido(lineaPedido: LineaPedido) {
        lineasPedido.add(lineaPedido)
        lineaPedido.pedido = this
    }

    fun removeLineaPedido(lineaPedido: LineaPedido) {
        lineasPedido.remove(lineaPedido)
        lineaPedido.pedido = null
    }
}