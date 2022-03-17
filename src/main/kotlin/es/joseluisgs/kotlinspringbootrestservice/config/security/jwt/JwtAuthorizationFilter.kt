package es.joseluisgs.kotlinspringbootrestservice.config.security.jwt

import es.joseluisgs.kotlinspringbootrestservice.services.usuarios.CustomUserDetailsService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetails
import org.springframework.stereotype.Component
import org.springframework.util.StringUtils
import org.springframework.web.filter.OncePerRequestFilter
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
class JwtAuthorizationFilter
@Autowired constructor(
    private val tokenProvider: JwtTokenProvider,
    private val userDetailsService: CustomUserDetailsService
) : OncePerRequestFilter() {

    var logger: Logger = LoggerFactory.getLogger(JwtAuthorizationFilter::class.java)

    // Comprueba la autorización a través del token
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        try {
            // Sacamos el token
            val token = getJwtFromRequest(request)
            // Si el token existe y es válido
            if (StringUtils.hasText(token) && tokenProvider.validateToken(token)) {
                // Obtenemos su ID
                val userId: Long = tokenProvider.getUserIdFromJWT(token)
                // Lo buscamos
                val user = userDetailsService.loadUserById(userId)
                // Obtenemos la auteticación encapsulada del token: usuario, roles, y las autorizaciones.
                val authentication = UsernamePasswordAuthenticationToken(
                    user,
                    user?.roles, user?.authorities
                )
                // le vamos a pasar información detro del contexto: dirección remota, session ID, etc.
                authentication.details = WebAuthenticationDetails(request)
                // Guardamos este objeto autetificación en elcontexto de seguridad.
                SecurityContextHolder.getContext().authentication = authentication
            }
        } catch (ex: Exception) {
            logger.info("No se ha podido establecer la autenticación de usuario en el contexto de seguridad")
        }
        filterChain.doFilter(request, response)
    }

    // Procesamos el Token del Request
    private fun getJwtFromRequest(request: HttpServletRequest): String? {
        // Tomamos la cabecera
        val bearerToken = request.getHeader(JwtTokenProvider.TOKEN_HEADER)
        // Si tiene el prefijo y es de la logitud indicada
        return if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(JwtTokenProvider.TOKEN_PREFIX)) {
            bearerToken.substring(JwtTokenProvider.TOKEN_PREFIX.length)
        } else null
    }
}
