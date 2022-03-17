package es.joseluisgs.kotlinspringbootrestservice.errors.usuarios

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

// Nos permite devolver un estado cuando salta la excepción
@ResponseStatus(HttpStatus.FORBIDDEN)
class UsuarioBadRequestException(error: String) :
    RuntimeException(error)