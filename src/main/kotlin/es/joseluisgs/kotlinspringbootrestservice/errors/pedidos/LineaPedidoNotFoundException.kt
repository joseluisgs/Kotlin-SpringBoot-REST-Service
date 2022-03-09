package es.joseluisgs.kotlinspringbootrestservice.errors.pedidos

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(HttpStatus.NOT_FOUND)
class LineaPedidoNotFoundException(id: Long) :
    RuntimeException("No se puede encontrar el la Linea de Pedido con ID: $id")
