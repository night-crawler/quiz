package fm.force.quiz.core.entity

import fm.force.quiz.configuration.BATCH_SIZE
import fm.force.quiz.security.entity.User
import org.hibernate.annotations.BatchSize
import javax.persistence.Entity
import javax.persistence.ManyToOne
import javax.persistence.Table

@Entity
@Table(name = "scores")
@BatchSize(size = BATCH_SIZE)
data class Score(
    @ManyToOne val owner: User,
    @ManyToOne val quizSession: QuizSession,
    @ManyToOne val tag: Tag?,
    @ManyToOne val topic: Topic?,
    var isCorrect: Boolean,
    var count: Long
) : BaseQuizEntity()
