package es.joseluisgs.kotlinspringbootrestservice.repositories

import es.joseluisgs.kotlinspringbootrestservice.models.Categoria
import es.joseluisgs.kotlinspringbootrestservice.models.Producto
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import org.springframework.data.repository.findByIdOrNull

@DataJpaTest
class ProductosRepositoryTest @Autowired constructor(
    private val entityManager: TestEntityManager,
    private val productosRepository: ProductosRepository
) {

    @Test
    fun findByIdTest() {
        val categoria = Categoria(nombre = "Categoria 99")
        val producto = Producto(
            nombre = "Producto 1",
            precio = 10.0,
            categoria = categoria
        )
        entityManager.persist(categoria)
        entityManager.persist(producto)
        entityManager.flush()
        val res = productosRepository.findByIdOrNull(producto.id)!!
        assertEquals(res, producto)
        assert(res.nombre == producto.nombre)
    }

    @Test
    fun saveTest() {
        val categoria = Categoria(nombre = "Categoria 99")
        val producto = Producto(
            nombre = "Producto 1",
            precio = 10.0,
            categoria = categoria
        )
        entityManager.persist(categoria)
        entityManager.flush()
        val res = productosRepository.save(producto)
        assertEquals(res, producto)
        assert(res.nombre == producto.nombre)
    }

    @Test
    fun updateTest() {
        val categoria = Categoria(nombre = "Categoria 99")
        val producto = Producto(
            nombre = "Producto 99",
            precio = 10.0,
            categoria = categoria
        )
        entityManager.persist(categoria)
        entityManager.persist(producto)
        entityManager.flush()
        var res = productosRepository.findByIdOrNull(producto.id)!!
        res.nombre = "Producto 100"
        res = productosRepository.save(res)
        assertEquals(res, producto)
        assert(res.nombre == producto.nombre)
    }

    @Test
    fun deleteTest() {
        val categoria = Categoria(nombre = "Categoria 99")
        val producto = Producto(
            nombre = "Producto 99",
            precio = 10.0,
            categoria = categoria
        )
        entityManager.persist(categoria)
        entityManager.persist(producto)
        entityManager.flush()
        productosRepository.delete(producto)
        productosRepository.findByIdOrNull(producto.id)?.let {
            assertEquals(it, null)
        }
    }
}