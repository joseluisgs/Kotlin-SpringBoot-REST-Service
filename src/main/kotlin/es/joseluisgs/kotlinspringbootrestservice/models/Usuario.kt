package es.joseluisgs.kotlinspringbootrestservice.models

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime
import javax.persistence.*

@Entity
@EntityListeners(AuditingEntityListener::class)
@Table(name = "usuarios") // Si podemos cambiar el nombre de la tabla
data class Usuario(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: Long,

    @Column(unique = true)
    val username: String,
    val fullName: String,
    val password: String,
    val avatar: String,
    val email: String,

    @CreatedDate
    val createdAt: LocalDateTime,

    // Mi rol
    // Conjunto de permisos que tiene, lo obtenemos como una colección de permisos
    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING) // la persistimos por su nombre
    val roles: Set<Rol>,

    val lastPasswordChangeAt: LocalDateTime = LocalDateTime.now()
)