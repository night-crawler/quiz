package fm.force.quiz.core.entity

import fm.force.quiz.security.entity.User
import javax.persistence.Entity
import javax.persistence.ManyToOne
import javax.persistence.Table

@Entity
@Table(name = "topics")
class Topic(
        @ManyToOne val owner: User,
        var title: String
) : BaseQuizEntity()
