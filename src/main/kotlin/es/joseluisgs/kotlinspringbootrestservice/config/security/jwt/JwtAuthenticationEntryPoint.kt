package es.joseluisgs.kotlinspringbootrestservice.config.security.jwt

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.stereotype.Component
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
class JwtAuthenticationEntryPoint
@Autowired constructor(
    private val mapper: ObjectMapper
) : AuthenticationEntryPoint {

    override fun commence(
        request: HttpServletRequest, response: HttpServletResponse,
        authException: AuthenticationException
    ) {

        // Estado de la respuesta a no autorizado, pero como JSON
        response.status = HttpStatus.UNAUTHORIZED.value()
        response.contentType = "application/json"

        // Construimos nuestro error con el mensaje de la excepción
        val error = mapOf(HttpStatus.UNAUTHORIZED to authException.message!!)
        val strApiError = mapper.writeValueAsString(error)

        // Lo devolvemos
        val writer = response.writer
        writer.println(strApiError)
    }
}
