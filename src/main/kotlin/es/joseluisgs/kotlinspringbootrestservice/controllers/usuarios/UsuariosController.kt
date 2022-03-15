package es.joseluisgs.kotlinspringbootrestservice.controllers

import es.joseluisgs.kotlinspringbootrestservice.config.APIConfig
import es.joseluisgs.kotlinspringbootrestservice.config.security.jwt.JwtTokenProvider
import es.joseluisgs.kotlinspringbootrestservice.config.security.jwt.model.JwtUserResponse
import es.joseluisgs.kotlinspringbootrestservice.config.security.jwt.model.LoginRequest
import es.joseluisgs.kotlinspringbootrestservice.dto.pedidos.PedidoListDTO
import es.joseluisgs.kotlinspringbootrestservice.dto.usuarios.UsuarioCreateDTO
import es.joseluisgs.kotlinspringbootrestservice.dto.usuarios.UsuarioGetDTO
import es.joseluisgs.kotlinspringbootrestservice.errors.pedidos.PedidosNotFoundException
import es.joseluisgs.kotlinspringbootrestservice.mappers.pedidos.PedidosMapper
import es.joseluisgs.kotlinspringbootrestservice.mappers.usuarios.UsuariosMapper
import es.joseluisgs.kotlinspringbootrestservice.models.Usuario
import es.joseluisgs.kotlinspringbootrestservice.models.UsuarioRol
import es.joseluisgs.kotlinspringbootrestservice.repositories.pedidos.PedidosRepository
import es.joseluisgs.kotlinspringbootrestservice.services.usuarios.UsuarioService
import es.joseluisgs.kotlinspringbootrestservice.utils.pagination.PaginationLinks
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*
import org.springframework.web.util.UriComponentsBuilder
import javax.servlet.http.HttpServletRequest
import javax.validation.Valid


@RestController // Cuidado que se necesia la barra al final porque la estamos poniendo en los verbos
@RequestMapping(APIConfig.API_PATH + "/usuarios") // Sigue escucnado en el directorio API
class UsuarioController
@Autowired constructor(
    private val usuarioService: UsuarioService,
    private val usuariosMapper: UsuariosMapper,
    private val authenticationManager: AuthenticationManager,
    private val tokenProvider: JwtTokenProvider,
    private val pedidosRepository: PedidosRepository,
    private val pedidosMapper: PedidosMapper,
    private val paginationLinks: PaginationLinks,

    ) {

    @PostMapping("/")
    fun nuevoUsuario(@RequestBody newUser: UsuarioCreateDTO): UsuarioGetDTO {
        return usuariosMapper.toDTO(usuarioService.nuevoUsuario(newUser))
    }

    @GetMapping("/me")
    fun me(@AuthenticationPrincipal user: Usuario): UsuarioGetDTO {
        return usuariosMapper.toDTO(user)
    }

    @PostMapping("/login")
    fun login(@RequestBody loginRequest: @Valid LoginRequest): JwtUserResponse {
        val authentication: Authentication = authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken(
                loginRequest.username,
                loginRequest.password
            )
        )
        // Autenticamos al usuario, si lo es nos lo devuelve
        SecurityContextHolder.getContext().authentication = authentication

        // Devolvemos al usuario autenticado
        val user = authentication.principal as Usuario

        // Generamos el token
        val jwtToken = tokenProvider.generateToken(authentication)

        // La respuesta que queremos
        return convertUserEntityAndTokenToJwtUserResponse(user, jwtToken)
    }

    @RequestMapping("/me/pedidos")
    fun getAll(
        // Otra forma de ponerlo sería
        // @PageableDefault(size = 10, page = 0) pageable: Pageable
        @AuthenticationPrincipal user: Usuario,
        @RequestParam(defaultValue = APIConfig.PAGINATION_INIT) page: Int,
        @RequestParam(defaultValue = APIConfig.PAGINATION_SIZE) size: Int,
        @RequestParam(defaultValue = APIConfig.PAGINATION_SORT) sort: String,
        request: HttpServletRequest?
    ): ResponseEntity<PedidoListDTO> {

        val paging: Pageable = PageRequest.of(page, size, Sort.Direction.ASC, sort)

        val pagedResult = pedidosRepository.findByClienteId(user.id, paging)

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


    /**
     * Método que convierte un usuario y un token a una respuesta de usuario
     *
     * @param user     Usuario
     * @param jwtToken Token
     * @return JwtUserResponse con el usuario y el token
     */
    private fun convertUserEntityAndTokenToJwtUserResponse(user: Usuario, jwtToken: String): JwtUserResponse {
        return JwtUserResponse(
            fullName = user.fullName,
            email = user.email,
            username = user.username,
            avatar = user.avatar,
            roles = user.roles.map { obj: UsuarioRol -> obj.name }.toMutableSet(),
            token = jwtToken
        )
    }
}
