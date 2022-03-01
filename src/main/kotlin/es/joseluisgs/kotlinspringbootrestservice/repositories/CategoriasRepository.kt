package es.joseluisgs.kotlinspringbootrestservice.repositories

import es.joseluisgs.kotlinspringbootrestservice.models.Categoria
import es.joseluisgs.kotlinspringbootrestservice.models.Producto

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query


interface CategoriasRepository : JpaRepository<Categoria, Long> {
    @Query("select count(p.id) from Producto p WHERE p.categoria.id= :id")
    fun countByProductos(id: Long): Long

    @Query("select p from Producto p WHERE p.categoria.id= :id")
    fun findProductosByCategoria(id: Long): List<Producto>

}