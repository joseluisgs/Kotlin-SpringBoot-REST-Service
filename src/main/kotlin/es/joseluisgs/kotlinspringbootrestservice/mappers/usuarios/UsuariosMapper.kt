package es.joseluisgs.kotlinspringbootrestservice.mappers.usuarios

import es.joseluisgs.kotlinspringbootrestservice.dto.usuarios.UsuarioGetDTO
import es.joseluisgs.kotlinspringbootrestservice.dto.usuarios.UsuarioPedidoDTO
import es.joseluisgs.kotlinspringbootrestservice.models.Usuario
import es.joseluisgs.kotlinspringbootrestservice.models.UsuarioRol
import org.springframework.stereotype.Component


@Component
class UsuariosMapper {
    fun toUsuarioPedidoDTO(usuario: Usuario): UsuarioPedidoDTO {
        return UsuarioPedidoDTO(
            usuario.username,
            usuario.fullName,
            usuario.email
        )
    }

    fun toDTO(user: Usuario): UsuarioGetDTO {
        return UsuarioGetDTO(
            username = user.username,
            fullName = user.fullName,
            email = user.email,
            roles = user.roles.map { obj: UsuarioRol -> obj.name }.toMutableSet(),
            avatar = user.avatar
        )
    }
}