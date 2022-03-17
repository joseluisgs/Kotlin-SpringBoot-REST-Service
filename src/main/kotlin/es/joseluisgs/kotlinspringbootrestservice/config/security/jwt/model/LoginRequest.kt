package es.joseluisgs.kotlinspringbootrestservice.config.security.jwt.model


data class LoginRequest(
    val username: String,
    val password: String
)
