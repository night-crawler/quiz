package fm.force.quiz.core.entity

import fm.force.quiz.security.entity.User
import javax.persistence.*

@Entity
@Table(
        name = "tags",
        uniqueConstraints = [
            UniqueConstraint(columnNames = ["owner_id", "slug"]),
            UniqueConstraint(columnNames = ["owner_id", "name"])
        ]
)
data class Tag(
        @ManyToOne val owner: User,
        @Column(length = 100, unique = true) var name: String,
        @Column(length = 150, unique = true) var slug: String
) : BaseQuizEntity()
