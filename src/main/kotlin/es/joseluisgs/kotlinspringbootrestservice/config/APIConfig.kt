package es.joseluisgs.kotlinspringbootrestservice.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.data.jpa.repository.config.EnableJpaAuditing

@Configuration
@EnableJpaAuditing // Activamos la auditoria, esto por ejemplo nos permite no meter la fecha si no que la tome automáticamente

class APIConfig {
    companion object {
        // Versión de la Api y versión del path, tomados de application.properties
        @Value("\${api.path}")
        const val API_PATH = "/rest"

        @Value("\${api.version}")
        const val API_VERSION = "1.0"
    }

}