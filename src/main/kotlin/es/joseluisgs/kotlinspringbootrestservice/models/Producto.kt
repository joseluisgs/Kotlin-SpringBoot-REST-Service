package es.joseluisgs.kotlinspringbootrestservice.models

import es.joseluisgs.kotlinspringbootrestservice.Extensions.toSlug
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime
import javax.persistence.*

@Entity
@EntityListeners(AuditingEntityListener::class)
data class Producto(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: Long,
    val nombre: String,
    val precio: Double,
    val imagen: String?,
    @CreatedDate
    val createdAt: LocalDateTime,


    // Mis relaciones
    // Un producto tiene una categoria, una categoria tiene muchos productos, unidireccional P -> C
    @ManyToOne
    @JoinColumn(name = "categoria_id")
    val categoria: Categoria
) {
    val slug: String
        get() = nombre.toSlug()
}
