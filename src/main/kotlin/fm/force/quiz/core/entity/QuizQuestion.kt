package fm.force.quiz.core.entity

import fm.force.quiz.configuration.BATCH_SIZE
import fm.force.quiz.security.entity.User
import org.hibernate.annotations.BatchSize
import javax.persistence.Entity
import javax.persistence.ManyToOne
import javax.persistence.Table
import javax.persistence.UniqueConstraint


@BatchSize(size = BATCH_SIZE)
@Entity
@Table(
    name = "quizzes__questions",
    uniqueConstraints = [UniqueConstraint(columnNames = ["quiz_id", "question_id"])]
)
data class QuizQuestion(
    @ManyToOne
    val owner: User,

    @ManyToOne
    val quiz: Quiz,

    @ManyToOne
    val question: Question,

    // hibernate.globally_quoted_identifiers=true
    // var order: Int
    // @Column(name="`open`")
    var seq: Int
) : BaseQuizEntity()
