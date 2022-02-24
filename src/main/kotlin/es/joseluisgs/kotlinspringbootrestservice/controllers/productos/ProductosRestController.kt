package es.joseluisgs.kotlinspringbootrestservice.controllers.productos

import es.joseluisgs.kotlinspringbootrestservice.config.APIConfig
import es.joseluisgs.kotlinspringbootrestservice.dto.productos.ProductoDTO
import es.joseluisgs.kotlinspringbootrestservice.dto.productos.ProductoListDTO
import es.joseluisgs.kotlinspringbootrestservice.errors.GeneralBadRequestException
import es.joseluisgs.kotlinspringbootrestservice.errors.productos.ProductoNotFoundException
import es.joseluisgs.kotlinspringbootrestservice.mappers.ProductosMapper
import es.joseluisgs.kotlinspringbootrestservice.repositories.ProductosRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*


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
    ): ResponseEntity<ProductoListDTO> {
        // Consulto en base a las páginas
        try {
            val paging: Pageable = PageRequest.of(page, size, Sort.Direction.ASC, sort)
            val pagedResult = if (nombre != null) {
                productosRepository.findByNombreContainsIgnoreCase(nombre, paging)
            } else {
                productosRepository.findAll(paging)
            }
            val result = ProductoListDTO(
                data = productosMapper.toDTO(pagedResult.content),
                currentPage = pagedResult.number,
                totalPages = pagedResult.totalPages,
                totalElements = pagedResult.totalElements,
                sort = sort
            )
            return ResponseEntity.ok(result)
        } catch (e: Exception) {
            throw GeneralBadRequestException("Selección de Datos", "Parámetros de consulta incorrectos")
        }
    }

    @GetMapping("/{id}")
    fun findById(@PathVariable id: Long): ResponseEntity<ProductoDTO> {
        try {
            val producto = productosRepository.findById(id)
            if (producto.isPresent) {
                return ResponseEntity.ok(productosMapper.toDTO(producto.get()))
            } else {
                throw ProductoNotFoundException(id)
            }
        } catch (e: Exception) {
            throw GeneralBadRequestException(
                "Selección de Datos",
                "Parámetros de consulta incorrectos o id en formato incorrecto"
            )
        }
    }
}