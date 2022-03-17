package es.joseluisgs.kotlinspringbootrestservice.config.security.password

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

import org.springframework.security.crypto.password.PasswordEncoder


@Configuration // Indicamos que para toda la api cada vez que inyectemos o llamemos a PasswordEncoder este será del tipo BCryptPasswordEncoder
class PasswordEncoderConfig {
    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }
}