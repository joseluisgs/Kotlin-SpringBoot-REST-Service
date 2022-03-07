package es.joseluisgs.kotlinspringbootrestservice.controllers.pedidos

import es.joseluisgs.kotlinspringbootrestservice.config.APIConfig
import es.joseluisgs.kotlinspringbootrestservice.dto.pedidos.PedidoDTO
import es.joseluisgs.kotlinspringbootrestservice.dto.pedidos.PedidoListDTO
import es.joseluisgs.kotlinspringbootrestservice.errors.pedidos.PedidoNotFoundException
import es.joseluisgs.kotlinspringbootrestservice.errors.pedidos.PedidosNotFoundException
import es.joseluisgs.kotlinspringbootrestservice.mappers.pedidos.PedidosMapper
import es.joseluisgs.kotlinspringbootrestservice.repositories.pedidos.PedidosRepository
import es.joseluisgs.kotlinspringbootrestservice.utils.pagination.PaginationLinks
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.util.UriComponentsBuilder
import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping(APIConfig.API_PATH + "/pedidos")
// Le aplico DI por constructor
class PedidosRestController
@Autowired constructor(
    private val pedidosRepository: PedidosRepository,
    private val pedidosMapper: PedidosMapper,
    private val paginationLinks: PaginationLinks
) {

    @RequestMapping("")
    fun getAll(
        // Otra forma de ponerlo sería
        // @PageableDefault(size = 10, page = 0) pageable: Pageable
        @RequestParam(defaultValue = APIConfig.PAGINATION_INIT) page: Int,
        @RequestParam(defaultValue = APIConfig.PAGINATION_SIZE) size: Int,
        @RequestParam(defaultValue = APIConfig.PAGINATION_SORT) sort: String,
        request: HttpServletRequest?
    ): ResponseEntity<PedidoListDTO> {

        val paging: Pageable = PageRequest.of(page, size, Sort.Direction.ASC, sort)

        val pagedResult = pedidosRepository.findAll(paging)

        // Si no hay nada, excepción de error
        if (pagedResult.isEmpty) {
            throw PedidosNotFoundException()
        }

        // si hay, vamos con la salida y con todos los links de paginaciones
        var links = ""
        if (request != null) {
            val uriBuilder = UriComponentsBuilder.fromHttpUrl(request.requestURL.toString())
            links = paginationLinks.createLinkHeader(pagedResult, uriBuilder)
        }

        val result = PedidoListDTO(
            data = pedidosMapper.toDTO(pagedResult.content),
            currentPage = pagedResult.number,
            totalPages = pagedResult.totalPages,
            totalElements = pagedResult.totalElements,
            sort = sort,
            links = links
        )

        return ResponseEntity
            .ok()
            .header("link", links)
            .body(result)
    }

    @GetMapping("/{id}")
    fun findById(@PathVariable id: Long): ResponseEntity<PedidoDTO> {
        val pedido = pedidosRepository.findById(id).orElseGet { throw PedidoNotFoundException(id) }
        return ResponseEntity.ok(pedidosMapper.toDTO(pedido))
    }
}