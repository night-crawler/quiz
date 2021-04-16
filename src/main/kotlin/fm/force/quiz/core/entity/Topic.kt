package fm.force.quiz.core.entity

import fm.force.quiz.configuration.BATCH_SIZE
import fm.force.quiz.security.entity.User
import org.hibernate.annotations.BatchSize
import javax.persistence.Entity
import javax.persistence.ManyToOne
import javax.persistence.Table
import javax.persistence.UniqueConstraint

@Entity
@Table(
    name = "topics",
    uniqueConstraints = [UniqueConstraint(columnNames = ["owner_id", "title"])]
)
@BatchSize(size = BATCH_SIZE)
data class Topic(
    @ManyToOne val owner: User,
    var title: String,
    var slug: String
) : BaseQuizEntity()
