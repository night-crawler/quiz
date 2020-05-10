package fm.force.quiz.core.entity

import fm.force.quiz.security.entity.User
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.ManyToOne
import javax.persistence.Table
import javax.persistence.UniqueConstraint

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
    var name: String,
    var slug: String
) : BaseQuizEntity()
