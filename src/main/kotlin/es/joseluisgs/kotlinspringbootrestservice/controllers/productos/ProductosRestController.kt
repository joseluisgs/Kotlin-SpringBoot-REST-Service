package es.joseluisgs.kotlinspringbootrestservice.controllers.productos

import es.joseluisgs.kotlinspringbootrestservice.config.APIConfig
import es.joseluisgs.kotlinspringbootrestservice.mappers.ProductosMapper
import es.joseluisgs.kotlinspringbootrestservice.models.Producto
import es.joseluisgs.kotlinspringbootrestservice.repositories.ProductosRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping(APIConfig.API_PATH + "/productos")
// Le aplico DI por constructor
class ProductosRestController @Autowired constructor(
    private val productosRepository: ProductosRepository,
    private val productosMapper: ProductosMapper
) {

    @GetMapping("")
    fun getAll(
        @RequestParam(required = false, name = "nombre") nombre: String?,
        @RequestParam(defaultValue = APIConfig.PAGINATION_INIT) page: Int,
        @RequestParam(defaultValue = APIConfig.PAGINATION_SIZE) size: Int,
        @RequestParam(defaultValue = APIConfig.PAGINATION_SORT) sort: String,
    ): ResponseEntity<MutableList<Producto?>> {
        // Consulto en base a las páginas
//        try {
        val paging: Pageable = PageRequest.of(page, size, Sort.Direction.ASC, sort)
        val pagedResult = if (nombre != null) {
            productosRepository.findByNombreContainsIgnoreCase(nombre, paging)
        } else {
            productosRepository.findAll(paging)
        }
        return ResponseEntity.ok(pagedResult.content)
//        } catch (e: Exception) {
//            throw GeneralBadRequestException("Selección de Datos", "Parámetros de consulta incorrectos")
//        }
    }
}