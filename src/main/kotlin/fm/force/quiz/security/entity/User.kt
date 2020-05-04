package fm.force.quiz.security.entity

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.JoinColumn
import javax.persistence.JoinTable
import javax.persistence.ManyToMany
import javax.persistence.Table

@Entity
@Table(name = "users")
data class User(
    @Column(unique = true)
    val username: String = "",
    val firstName: String = "",
    val lastName: String = "",

    @Column(unique = true)
    val email: String,

    var password: String = "",

    var isActive: Boolean = false,

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
