package fm.force.quiz.security.entity

import javax.persistence.*

@Entity
@Table(name = "users")
data class User(
        @Column(unique = true)
        val username: String = "",
        val firstName: String = "",
        val lastName: String = "",

        @Column(unique = true)
        val email: String,

        val password: String = "",

        val isActive: Boolean = false,

        @ManyToMany(fetch = FetchType.EAGER)
        val roles: List<Role> = listOf()

) : BaseEntity() {
    override fun toString() = "User(id=$id username=$email)"
}
