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
// @TestInstance(TestInstance.Lifecycle.PER_CLASS)
// @TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class CategoriasRepositoryTest
@Autowired constructor(
    private val entityManager: TestEntityManager,
    private val categoriasRepository: CategoriasRepository
) {
    /*@BeforeAll
    fun setup() {
        println(">> Setup")
    }

    @AfterAll
    fun teardown() {
        println(">> Tear down")
    }*/

    @Test
    // @Order(1)
    fun findByIdTest() {
        val categoria = Categoria(nombre = "Categoria 99")
        entityManager.persist(categoria)
        entityManager.flush()
        val res = categoriasRepository.findByIdOrNull(categoria.id)!!
        assertEquals(res, categoria)
    }

    @Test
    // @Order(2)
    fun saveTest() {
        val categoria = Categoria(nombre = "Categoria 99")
        //entityManager.persist(categoria)
        //entityManager.flush()
        val found = categoriasRepository.save(categoria)
        assertEquals(found, categoria)
    }

    @Test
    // @Order(3)
    fun updateTest() {
        val categoria = Categoria(nombre = "Categoria 99")
        entityManager.persist(categoria)
        entityManager.flush()
        var res = categoriasRepository.findByIdOrNull(categoria.id)!!
        res.nombre = "Categoria 100"
        res = categoriasRepository.save(categoria)
        assertEquals(res, categoria)
    }

    @Test
    // @Order(4)
    fun deleteTest() {
        val categoria = Categoria(nombre = "Categoria 99")
        entityManager.persist(categoria)
        entityManager.flush()
        val res = categoriasRepository.findByIdOrNull(categoria.id)!!
        categoriasRepository.delete(res)
        categoriasRepository.findByIdOrNull(categoria.id)?.let {
            assertEquals(it, null)
        }
    }

    @Test
    fun countByProductosTest() {
        val categoria = Categoria(nombre = "Categoria 99")
        val producto = Producto(nombre = "Producto 99", 99.9, categoria)
        entityManager.persist(categoria)
        entityManager.persist(producto)
        entityManager.flush()
        val cate = categoriasRepository.findByIdOrNull(categoria.id)!!
        val res = categoriasRepository.countByProductos(cate.id)
        assertEquals(res, 1)
    }

    @Test
    fun findProductosByCategoria() {
        val categoria = Categoria(nombre = "Categoria 99")
        val producto = Producto(nombre = "Producto 99", 99.9, categoria)
        entityManager.persist(categoria)
        entityManager.persist(producto)
        entityManager.flush()
        val res = categoriasRepository.findProductosByCategoria(categoria.id)
        assertEquals(res, listOf(producto))
    }
}
