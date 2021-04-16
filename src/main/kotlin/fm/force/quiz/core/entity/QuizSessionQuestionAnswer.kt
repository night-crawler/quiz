package fm.force.quiz.core.entity

import fm.force.quiz.configuration.BATCH_SIZE
import fm.force.quiz.security.entity.User
import org.hibernate.annotations.BatchSize
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.ManyToOne
import javax.persistence.Table

@Entity
@Table(name = "quiz_session_question_answers")
@BatchSize(size = BATCH_SIZE)
data class QuizSessionQuestionAnswer(
    @ManyToOne val owner: User,
    @ManyToOne val quizSession: QuizSession,
    @ManyToOne val quizSessionQuestion: QuizSessionQuestion,
    @ManyToOne val originalAnswer: Answer? = null,

    @Column(columnDefinition = "TEXT")
    val text: String,

    val isCorrect: Boolean
) : BaseQuizEntity() {
    override fun toString() =
        "QuizSessionQuestionAnswer(" +
            "owner=${owner.id}, quizSession=${quizSession.id}, " +
            "quizSessionQuestion=${quizSessionQuestion.id}, originalAnswer=${originalAnswer?.id}, " +
            "text='$text', isCorrect=$isCorrect)"
}
