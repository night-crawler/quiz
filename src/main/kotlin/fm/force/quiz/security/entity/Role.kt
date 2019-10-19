package fm.force.quiz.security.entity

import javax.persistence.*

@Entity
@Table(name = "roles")
data class Role(
        @Column(length = 16)
        val name: String,

        @ManyToMany(mappedBy = "roles", fetch = FetchType.LAZY)
        val users: List<User> = listOf()
) : BaseEntity() {
    enum class PredefinedRoles {
        ADMIN, STUDENT, TEACHER
    }

    companion object {
        val predefinedRoleNames get() = PredefinedRoles.values().map { it.name }
    }

    override fun toString(): String {
        return "<Role $id $name>"
    }
}
