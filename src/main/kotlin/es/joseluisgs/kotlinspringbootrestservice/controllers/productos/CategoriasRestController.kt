package es.joseluisgs.kotlinspringbootrestservice.controllers.productos

import es.joseluisgs.kotlinspringbootrestservice.config.APIConfig
import es.joseluisgs.kotlinspringbootrestservice.models.Categoria
import es.joseluisgs.kotlinspringbootrestservice.repositories.CategoriasRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping(APIConfig.API_PATH + "/categorias")
// Le aplico DI por constructor
class CategoriasRestController
@Autowired constructor(
    private val categoriasRepository: CategoriasRepository
) {

    @GetMapping("")
    fun getAll(): ResponseEntity<MutableList<Categoria>> {
        val res = categoriasRepository.findAll()
        return ResponseEntity.ok(res)
    }
}
