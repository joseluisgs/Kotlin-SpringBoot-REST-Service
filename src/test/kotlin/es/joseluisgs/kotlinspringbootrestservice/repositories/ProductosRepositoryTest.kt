package es.joseluisgs.kotlinspringbootrestservice.repositories

import es.joseluisgs.kotlinspringbootrestservice.models.Categoria
import es.joseluisgs.kotlinspringbootrestservice.models.Producto
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
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
        assertAll(
            { assertEquals(res, producto) },
            { assert(res.nombre == producto.nombre) }
        )
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

    @Test
    fun findByNombreContainsIgnoreCaseTest() {
        val categoria = Categoria(nombre = "Categoria 99")
        val producto = Producto(
            nombre = "Producto 99",
            precio = 10.0,
            categoria = categoria
        )
        entityManager.persist(categoria)
        entityManager.persist(producto)
        entityManager.flush()
        val paging: Pageable = PageRequest.of(0, 10, Sort.Direction.ASC, "id")
        val res = productosRepository.findByNombreContainsIgnoreCase("producto", paging)
        assertAll(
            { assertTrue(res.content.contains(producto)) },
            { assertEquals(res.totalElements, 1) }
        )
    }
}