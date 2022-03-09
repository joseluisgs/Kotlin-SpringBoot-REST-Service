package es.joseluisgs.kotlinspringbootrestservice.config.swagger

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.EnableWebMvc
import springfox.documentation.builders.PathSelectors
import springfox.documentation.builders.RequestHandlerSelectors
import springfox.documentation.service.ApiInfo
import springfox.documentation.service.Contact
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spring.web.plugins.Docket
import springfox.documentation.swagger2.annotations.EnableSwagger2


// http://localhost:XXXX/swagger-ui/index.html
@EnableWebMvc // Importante con el nuevo Swagger3 y Spring 2.6.x
@EnableSwagger2
@Configuration
class SwaggerConfig {
    @Bean
    fun productApi(): Docket {
        return Docket(DocumentationType.SWAGGER_2)
            .select() //.apis(RequestHandlerSelectors.any())
            .paths(PathSelectors.any())
            .apis(RequestHandlerSelectors.basePackage("es.joseluisgs.kotlinspringbootrestservice.controllers")) //.paths(PathSelectors.ant("/controllers/*"))
            .build()
            .apiInfo(metaInfo())
    }

    private fun metaInfo(): ApiInfo {
        return ApiInfo(
            "API REST Kotlin Spring Boot 2022",
            "API de ejemplo del curso Desarrollo de un API REST Kotlin con Spring Boot. 2022",
            "1.1",
            "Terms of Service",
            Contact(
                "Jose Luis Gonzalez", "https://github.com/joseluisgs",
                "joseluis.gonzalez@iesluisvives.org"
            ),
            "MIT",
            "https://github.com/joseluisgs/SpringBoot-Productos-DAM-2021-2022/blob/master/LICENSE", ArrayList()
        )
    }
}