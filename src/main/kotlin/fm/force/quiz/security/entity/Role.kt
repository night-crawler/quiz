package fm.force.quiz.security.entity

import javax.persistence.*

@Entity
@Table(name = "roles")
data class Role(
        @Column(length = 16)
        val name: String,

        @ManyToMany(mappedBy = "roles", fetch = FetchType.LAZY)
        val users: List<User>
) : BaseEntity() {
    override fun toString(): String {
        return "<Role $id $name>"
    }
}
