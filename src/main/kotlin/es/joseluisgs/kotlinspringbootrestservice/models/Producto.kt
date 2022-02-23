package es.joseluisgs.kotlinspringbootrestservice.models

import es.joseluisgs.kotlinspringbootrestservice.Extensions.toSlug
import javax.persistence.*

@Entity
data class Producto(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: Long,
    val nombre: String,
    val precio: Double,
    val imagen: String?,
    var slug: String = nombre.toSlug(),

    // Mis relaciones
    // Un producto tiene una categoria, una categoria tiene muchos productos, unidireccional P -> C
    @ManyToOne
    @JoinColumn(name = "categoria_id")
    val categoria: Categoria
)
