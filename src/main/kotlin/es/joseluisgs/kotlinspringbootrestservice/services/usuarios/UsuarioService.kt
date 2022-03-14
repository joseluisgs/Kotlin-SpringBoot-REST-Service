package es.joseluisgs.kotlinspringbootrestservice.services.usuarios

import es.joseluisgs.kotlinspringbootrestservice.dto.usuarios.UsuarioCreateDTO
import es.joseluisgs.kotlinspringbootrestservice.errors.usuarios.NewUserWithDifferentPasswordsException
import es.joseluisgs.kotlinspringbootrestservice.models.Usuario
import es.joseluisgs.kotlinspringbootrestservice.models.UsuarioRol
import es.joseluisgs.kotlinspringbootrestservice.repositories.usuarios.UsuariosRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.http.HttpStatus
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import java.util.*


@Service
class UsuarioService
@Autowired constructor(
    private val usuariosRepository: UsuariosRepository,
    private val passwordEncoder: PasswordEncoder
) {

    fun findUserByUsername(username: String): Usuario? {
        return usuariosRepository.findByUsername(username)
    }

    fun findUserById(userId: Long): Optional<Usuario> {
        return usuariosRepository.findById(userId)
    }

    /**
     * Nos permite crear un nuevo Usuario con rol USER
     */
    fun nuevoUsuario(newUser: UsuarioCreateDTO): Usuario? {
        // System.out.println(passwordEncoder.encode(newUser.getPassword()));
        return if (newUser.password.contentEquals(newUser.password2)) {
            val usuario = Usuario(
                username = newUser.username,
                password = passwordEncoder.encode(newUser.password),
                fullName = newUser.fullName,
                email = newUser.email,
                avatar = newUser.avatar,
                roles = setOf(UsuarioRol.USER)
            )
            try {
                usuariosRepository.save(usuario)
            } catch (ex: DataIntegrityViolationException) {
                throw ResponseStatusException(HttpStatus.BAD_REQUEST, "El nombre de usuario ya existe")
            }
        } else {
            throw NewUserWithDifferentPasswordsException()
        }
    }
}