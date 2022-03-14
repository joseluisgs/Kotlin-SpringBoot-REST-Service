package es.joseluisgs.kotlinspringbootrestservice.controllers

import es.joseluisgs.kotlinspringbootrestservice.config.APIConfig
import es.joseluisgs.kotlinspringbootrestservice.config.security.jwt.JwtTokenProvider
import es.joseluisgs.kotlinspringbootrestservice.config.security.jwt.model.JwtUserResponse
import es.joseluisgs.kotlinspringbootrestservice.config.security.jwt.model.LoginRequest
import es.joseluisgs.kotlinspringbootrestservice.dto.usuarios.UsuarioCreateDTO
import es.joseluisgs.kotlinspringbootrestservice.dto.usuarios.UsuarioGetDTO
import es.joseluisgs.kotlinspringbootrestservice.mappers.usuarios.UsuariosMapper
import es.joseluisgs.kotlinspringbootrestservice.models.Usuario
import es.joseluisgs.kotlinspringbootrestservice.models.UsuarioRol
import es.joseluisgs.kotlinspringbootrestservice.services.usuarios.UsuarioService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*
import javax.validation.Valid


@RestController // Cuidado que se necesia la barra al final porque la estamos poniendo en los verbos
@RequestMapping(APIConfig.API_PATH + "/usuarios") // Sigue escucnado en el directorio API
class UsuarioController
@Autowired constructor(
    private val usuarioService: UsuarioService,
    private val usuariosMapper: UsuariosMapper,
    private val authenticationManager: AuthenticationManager,
    private val tokenProvider: JwtTokenProvider

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
