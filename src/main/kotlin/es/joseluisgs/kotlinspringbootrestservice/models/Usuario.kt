package es.joseluisgs.kotlinspringbootrestservice.models

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import java.time.LocalDateTime
import javax.persistence.*
import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank


@Entity
@EntityListeners(AuditingEntityListener::class)
@Table(name = "usuarios") // Si podemos cambiar el nombre de la tabla
data class Usuario(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: Long,
    @Column(unique = true)
    @NotBlank(message = "Username no puede estar vacío")
    // Username y password son vacíos para implementar las funciones sobreescritas
    private val username: String,
    @NotBlank(message = "FullName no puede estar vacío")
    val fullName: String,
    @NotBlank(message = "Password no puede estar vacío")
    private val password: String,
    val avatar: String?,
    @Email(regexp = ".*@.*\\..*", message = "Email debe ser válido")
    val email: String,
    @CreatedDate
    val createdAt: LocalDateTime,

    // Mi rol
    // Conjunto de permisos que tiene, lo obtenemos como una colección de permisos
    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING) // la persistimos por su nombre
    val roles: Set<UsuarioRol>,

    val lastPasswordChangeAt: LocalDateTime = LocalDateTime.now()
) : UserDetails {
    constructor(
        username: String,
        password: String,
        fullName: String,
        email: String,
        avatar: String?,
        roles: Set<UsuarioRol>
    ) : this(
        id = 0,
        username = username,
        password = password,
        fullName = fullName,
        email = email,
        avatar = avatar,
        roles = roles,
        createdAt = LocalDateTime.now()
    )

    override fun getAuthorities(): MutableCollection<out GrantedAuthority> {
        return roles.map { ur: UsuarioRol ->
            SimpleGrantedAuthority(
                "ROLE_" + ur.name
            )
        }.toMutableSet()
    }

    override fun getPassword(): String {
        return password
    }

    override fun getUsername(): String {
        return username
    }

    override fun isAccountNonExpired(): Boolean {
        return true
    }

    override fun isAccountNonLocked(): Boolean {
        return true
    }

    override fun isCredentialsNonExpired(): Boolean {
        return true
    }

    override fun isEnabled(): Boolean {
        return true
    }
}