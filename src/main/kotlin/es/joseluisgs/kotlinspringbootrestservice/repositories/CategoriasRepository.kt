package es.joseluisgs.kotlinspringbootrestservice.repositories

import es.joseluisgs.kotlinspringbootrestservice.models.Categoria

import org.springframework.data.jpa.repository.JpaRepository


interface CategoriasRepository : JpaRepository<Categoria, Long>