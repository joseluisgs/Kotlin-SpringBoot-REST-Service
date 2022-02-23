package es.joseluisgs.kotlinspringbootrestservice.mappers

import es.joseluisgs.kotlinspringbootrestservice.dto.productos.ProductoDTO
import es.joseluisgs.kotlinspringbootrestservice.models.Producto
import org.springframework.stereotype.Component

@Component
class ProductosMapper {
    fun toDTO(producto: Producto): ProductoDTO {
        return ProductoDTO(
            producto.id,
            producto.nombre,
            producto.precio,
            producto.imagen,
            producto.createdAt.toString(),
            producto.slug,
            producto.categoria.nombre
        )
    }

    fun toDTO(productos: List<Producto?>): List<ProductoDTO> {
        return productos.map { toDTO(it!!) }
    }
}