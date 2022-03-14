package es.joseluisgs.kotlinspringbootrestservice.config.security.jwt.model

import es.joseluisgs.kotlinspringbootrestservice.dto.usuarios.UsuarioGetDTO

class JwtUserResponse(
    username: String,
    fullName: String,
    email: String,
    roles: Set<String>,
    avatar: String?,
    val token: String
) : UsuarioGetDTO(username, fullName, email, roles, avatar)