package es.joseluisgs.kotlinspringbootrestservice.config.security

import es.joseluisgs.kotlinspringbootrestservice.config.APIConfig
import es.joseluisgs.kotlinspringbootrestservice.config.security.jwt.JwtAuthorizationFilter
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter


@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
class SecurityConfig
@Autowired constructor(
    private val userDetailsService: UserDetailsService,
    private val passwordEncoder: PasswordEncoder,
    private val jwtAuthenticationEntryPoint: AuthenticationEntryPoint,
    val jwtAuthorizationFilter: JwtAuthorizationFilter
) : WebSecurityConfigurerAdapter() {

    /*
    Mecanismos de autentificación
    Expone nuestro mecanismos de autentificación como un bean para que luego lo podamos usar en un filtro
     */
    @Bean
    override fun authenticationManagerBean(): AuthenticationManager {
        return super.authenticationManagerBean()
    }

    override fun configure(auth: AuthenticationManagerBuilder) {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder)
    }

    /*
    Configuración de autorización según verbos y rutas
     */
    override fun configure(http: HttpSecurity) {
        http
            .csrf()
            .disable()
            .exceptionHandling()
            .authenticationEntryPoint(jwtAuthenticationEntryPoint)
            .and() // Para el establecimiento de sesiones son estado, no usamos sesiones
            .sessionManagement()
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and() // Autorizamos con roles y acceso
            .authorizeRequests()

            // Permisos
            // Registrarse todos y loguearse todos. De esta manera podemos permitir las consultas a todas las rutas
            .antMatchers(HttpMethod.POST, APIConfig.API_PATH + "/usuarios/**").permitAll()
            .antMatchers(HttpMethod.GET, APIConfig.API_PATH + "/usuarios/**").hasAnyRole("USER", "ADMIN")
            // He dejado experimentos con rutas duplicando controladores en /auth
            // CRUD de Categorias, solo Admin
            .antMatchers(HttpMethod.GET, APIConfig.API_PATH + "/auth/categorias/**").hasAnyRole("USER", "ADMIN")
            .antMatchers(HttpMethod.POST, APIConfig.API_PATH + "/auth/categorias/**").hasAnyRole("ADMIN")
            .antMatchers(HttpMethod.PUT, APIConfig.API_PATH + "/auth/categorias/**").hasAnyRole("ADMIN")
            .antMatchers(HttpMethod.DELETE, APIConfig.API_PATH + "/auth/categorias/**").hasAnyRole("ADMIN")
            // CRUD de Productos, solo Admin...
            .antMatchers(HttpMethod.GET, APIConfig.API_PATH + "/auth/productos/**").hasAnyRole("USER", "ADMIN")
            .antMatchers(HttpMethod.POST, APIConfig.API_PATH + "/auth/productos/**").hasAnyRole("ADMIN")
            .antMatchers(HttpMethod.PUT, APIConfig.API_PATH + "/auth/productos/**").hasAnyRole("ADMIN")
            .antMatchers(HttpMethod.DELETE, APIConfig.API_PATH + "/auth/productos/**").hasAnyRole("ADMIN")
            // CRUD  de Pedidos solo administration
            .antMatchers(HttpMethod.GET, APIConfig.API_PATH + "/auth/pedidos/**").hasAnyRole("ADMIN")
            .antMatchers(HttpMethod.POST, APIConfig.API_PATH + "/auth/pedidos/**").hasAnyRole("ADMIN")
            .antMatchers(HttpMethod.PUT, APIConfig.API_PATH + "/auth/pedidos/**").hasAnyRole("ADMIN")
            .antMatchers(HttpMethod.DELETE, APIConfig.API_PATH + "/auth/pedidos/**").hasAnyRole("ADMIN")
            // Mis Pedidos CRUD, solo si son míos
            // CRUD  de Pedidos solo administration
            .antMatchers(HttpMethod.GET, APIConfig.API_PATH + "/auth/user/pedidos/**").hasAnyRole("USER")
            .antMatchers(HttpMethod.POST, APIConfig.API_PATH + "/auth/user/pedidos/**").hasAnyRole("USER")
            .antMatchers(HttpMethod.PUT, APIConfig.API_PATH + "/auth/user/pedidos/**").hasAnyRole("USER")
            .antMatchers(HttpMethod.DELETE, APIConfig.API_PATH + "/auth/user/pedidos/**").hasAnyRole("USER")
            // Dejamos todo abierto por ahora

            .anyRequest().not().authenticated()


        // Será el encargado de coger el token y si es válido lo dejaremos pasar...
        // Añadimos el filtro (jwtAuthorizationFilter).
        http.addFilterBefore(jwtAuthorizationFilter, UsernamePasswordAuthenticationFilter::class.java)
    }
}
