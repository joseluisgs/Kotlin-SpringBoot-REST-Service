package es.joseluisgs.kotlinspringbootrestservice.dto.usuarios

// Si es open se le quita el data class y se puede heredar
open class UsuarioGetDTO(
    val username: String,
    val fullName: String,
    val email: String,
    val roles: Set<String>,
    val avatar: String?,
)