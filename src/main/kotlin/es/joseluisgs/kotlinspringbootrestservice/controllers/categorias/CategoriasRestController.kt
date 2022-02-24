package es.joseluisgs.kotlinspringbootrestservice.controllers.categorias

import es.joseluisgs.kotlinspringbootrestservice.config.APIConfig
import es.joseluisgs.kotlinspringbootrestservice.dto.categorias.CategoriaCreateDTO
import es.joseluisgs.kotlinspringbootrestservice.errors.GeneralBadRequestException
import es.joseluisgs.kotlinspringbootrestservice.errors.categorias.CategoriaNotFoundException
import es.joseluisgs.kotlinspringbootrestservice.errors.productos.ProductoBadRequestException
import es.joseluisgs.kotlinspringbootrestservice.models.Categoria
import es.joseluisgs.kotlinspringbootrestservice.repositories.CategoriasRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping(APIConfig.API_PATH + "/categorias")
// Le aplico DI por constructor
class CategoriasRestController
@Autowired constructor(
    private val categoriasRepository: CategoriasRepository
) {

    @GetMapping("")
    fun getAll(): ResponseEntity<MutableList<Categoria>> {
        val categorias = categoriasRepository.findAll()
        return ResponseEntity.ok(categorias)
    }

    @GetMapping("/{id}")
    fun getById(@PathVariable id: Long): ResponseEntity<Categoria> {
        try {
            val categoria = categoriasRepository.findById(id).get()
            return ResponseEntity.ok(categoria)
        } catch (e: Exception) {
            throw CategoriaNotFoundException(id)
        }
    }

    @PostMapping("")
    fun create(@RequestBody categoria: CategoriaCreateDTO): ResponseEntity<Categoria> {
        try {
            if (!checkCategoriaData(categoria.nombre)) {
                throw ProductoBadRequestException(
                    "Datos incorrectos",
                    "El nombre de la categoria no es correcto"
                )
            }
            val newCategoria = Categoria(categoria.nombre)
            return ResponseEntity.ok(categoriasRepository.save(newCategoria))
        } catch (e: Exception) {
            throw GeneralBadRequestException(
                "Error: Insertar Categoria",
                "Campos incorrectos o nombre existente. ${e.message}"
            )
        }
    }

    @PutMapping("/{id}")
    fun update(@RequestBody categoria: CategoriaCreateDTO, @PathVariable id: Long): ResponseEntity<Categoria> {
        try {
            if (!checkCategoriaData(categoria.nombre)) {
                throw ProductoBadRequestException(
                    "Datos incorrectos",
                    "El nombre de la categoria no es correcto"
                )
            }
            val updateCategoria = categoriasRepository.findById(id).orElseGet { throw CategoriaNotFoundException(id) }
            updateCategoria.nombre = categoria.nombre
            return ResponseEntity.ok(categoriasRepository.save(updateCategoria))
        } catch (e: Exception) {
            throw GeneralBadRequestException(
                "Error: Insertar Categoria",
                "Campos incorrectos o nombre existente. ${e.message}"
            )
        }
    }

    private fun checkCategoriaData(nombre: String): Boolean {
        if (nombre.trim().isBlank()) {
            throw ProductoBadRequestException("Nombre", "El nombre es obligatorio")
        }
        return true
    }
}
