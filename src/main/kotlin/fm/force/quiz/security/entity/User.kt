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

        @ManyToMany(targetEntity = Role::class, fetch = FetchType.EAGER)
        @JoinTable(
                name = "users__roles",
                joinColumns = [JoinColumn(name = "user_id")],
                inverseJoinColumns = [JoinColumn(name = "role_id")]
        )
        var roles: Set<Role> = setOf()

) : BaseEntity() {
    override fun toString() = "User(id=$id username=$email)"
}
