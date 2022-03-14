package es.joseluisgs.kotlinspringbootrestservice.config.security.jwt

import es.joseluisgs.kotlinspringbootrestservice.models.Usuario
import es.joseluisgs.kotlinspringbootrestservice.models.UsuarioRol
import io.jsonwebtoken.*
import io.jsonwebtoken.security.Keys
import io.jsonwebtoken.security.SignatureException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Component
import java.util.*

@Component
class JwtTokenProvider {

    var logger: Logger = LoggerFactory.getLogger(JwtTokenProvider::class.java)

    // Contraseña de la clase
    @Value("\${jwt.secret:EnUnLugarDeLaManchaDeCuyoNombreNoQuieroAcordarmeNoHaMuchoTiempoQueViviaUnHidalgo}")
    private var jwtSecreto: String? = null

    @Value("\${jwt.token-expiration:86400}")
    private var jwtDuracionTokenEnSegundos = 0

    // Genera el Token
    fun generateToken(authentication: Authentication): String {

        // Obtenemos el usuario
        val usuario = authentication.principal as Usuario

        // Creamos el timepo de vida del token, fecha en milisegunods (*1000) Fecha del sistema
        // Mas duración del token
        val tokenExpirationDate = Date(System.currentTimeMillis() + jwtDuracionTokenEnSegundos * 1000)

        // Construimos el token con sus datos y payload
        return Jwts.builder() // Lo firmamos con nuestro secreto HS512
            .signWith(Keys.hmacShaKeyFor(jwtSecreto!!.toByteArray()), SignatureAlgorithm.HS512) // Tipo de token
            .setHeaderParam("typ", TOKEN_TYPE) // Como Subject el ID del usuario
            .setSubject(usuario.id.toString()) // Fecha actual
            .setIssuedAt(Date()) // Fecha de expiración
            .setExpiration(tokenExpirationDate) // Payload o datos extra del token son claims
            // Nombre completo del usuario
            .claim("fullname", usuario.fullName) // Le añadimos los roles o lo que queramos como payload: claims
            .claim("roles", usuario.roles.joinToString(", ") { obj: UsuarioRol -> obj.name }
            )
            .compact()
    }

    // A partir de un token obetner el ID de usuario
    fun getUserIdFromJWT(token: String?): Long {
        // Obtenemos los claims del token
        val claims = Jwts.parserBuilder()
            .setSigningKey(Keys.hmacShaKeyFor(jwtSecreto!!.toByteArray()))
            .build()
            .parseClaimsJws(token)
            .body
        // Devolvemos el ID
        return claims.subject.toLong()
    }

    // Nos idica como validar el Token
    fun validateToken(authToken: String?): Boolean {
        try {
            // Jwts.parser().setSigningKey(jwtSecreto.getBytes()).parseClaimsJws(authToken);
            Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(jwtSecreto!!.toByteArray()))
                .build()
                .parseClaimsJws(authToken)
            return true
        } catch (ex: SignatureException) {
            logger.info("Error en la firma del token JWT: " + ex.message)
        } catch (ex: MalformedJwtException) {
            logger.info("Token malformado: " + ex.message)
        } catch (ex: ExpiredJwtException) {
            logger.info("El token ha expirado: " + ex.message)
        } catch (ex: UnsupportedJwtException) {
            logger.info("Token JWT no soportado: " + ex.message)
        } catch (ex: IllegalArgumentException) {
            logger.info("JWT claims vacío")
        }
        return false
    }

    // Variables de clase
    companion object {
        // Naturaleza del Token!!!
        const val TOKEN_HEADER = "Authorization" // Encabezado
        const val TOKEN_PREFIX = "Bearer " // Prefijo, importante este espacio
        const val TOKEN_TYPE = "JWT" // Tipo de Token
    }
}