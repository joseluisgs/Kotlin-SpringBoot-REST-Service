package es.joseluisgs.kotlinspringbootrestservice.models

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity
data class Categoria(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: Long,
    val nombre: String
)
