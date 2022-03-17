package es.joseluisgs.kotlinspringbootrestservice.dto.pedidos

import es.joseluisgs.kotlinspringbootrestservice.dto.usuarios.UsuarioPedidoDTO

data class PedidoDTO(
    val id: Long,
    val fecha: String,
    val cliente: UsuarioPedidoDTO,
    val total: Double,
    val lineasPedido: List<LineaPedidoDTO>
)