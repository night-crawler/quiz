package fm.force.quiz.core.entity

import fm.force.quiz.security.entity.User
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.ManyToOne
import javax.persistence.Table

@Entity
@Table(name = "tags")
data class Tag(
        @ManyToOne val owner: User,
        @Column(length = 100) val name: String,
        @Column(length = 150) val slug: String
) : BaseQuizEntity()
