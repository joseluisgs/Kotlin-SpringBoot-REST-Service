package es.joseluisgs.kotlinspringbootrestservice.services.usuarios

import es.joseluisgs.kotlinspringbootrestservice.models.Usuario
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service("userDetailsService")
class CustomUserDetailsService
@Autowired constructor(
    private val usuarioService: UsuarioService,
) : UserDetailsService {

    override fun loadUserByUsername(username: String): UserDetails {
        return usuarioService.findUserByUsername(username) ?: throw UsernameNotFoundException("$username no encontrado")

    }

    fun loadUserById(userId: Long): Usuario? {
        return usuarioService.findUserById(userId).orElseThrow {
            UsernameNotFoundException("Usuario con id: $userId no encontrado")
        }
    }
}