package es.joseluisgs.kotlinspringbootrestservice.dto.usuarios


data class UsuarioCreateDTO(
    val username: String,
    val fullName: String,
    val email: String,
    val password: String,
    val password2: String,
    val avatar: String?
)
