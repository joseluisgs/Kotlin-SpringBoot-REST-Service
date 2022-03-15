package es.joseluisgs.kotlinspringbootrestservice.repositories.pedidos

import es.joseluisgs.kotlinspringbootrestservice.models.Pedido
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface PedidosRepository : JpaRepository<Pedido, Long> {
    fun findByClienteId(userId: Long, pageable: Pageable?): Page<Pedido?>
    fun findByIdAndClienteId(id: Long, userId: Long): Pedido?
}
