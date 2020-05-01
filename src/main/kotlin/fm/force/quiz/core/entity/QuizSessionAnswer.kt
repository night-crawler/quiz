package fm.force.quiz.core.entity

import fm.force.quiz.security.entity.User
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.JoinColumn
import javax.persistence.JoinTable
import javax.persistence.ManyToMany
import javax.persistence.ManyToOne
import javax.persistence.Table
import javax.persistence.UniqueConstraint

@Entity
@Table(
    name = "quiz_session_answers",
    uniqueConstraints = [UniqueConstraint(columnNames = ["quiz_session_id", "quiz_session_question_id"])]
)
data class QuizSessionAnswer(
    @ManyToOne val owner: User,
    @ManyToOne val quizSession: QuizSession,
    @ManyToOne val quizSessionQuestion: QuizSessionQuestion,

    @ManyToMany(targetEntity = QuizSessionQuestionAnswer::class, fetch = FetchType.LAZY)
    @JoinTable(
        name = "quiz_session_answer__quiz_session_question_answers",
        joinColumns = [JoinColumn(name = "quiz_session_answer_id")],
        inverseJoinColumns = [JoinColumn(name = "quiz_session_question_answer_id")]
    )
    val answers: Set<QuizSessionQuestionAnswer> = setOf()
) : BaseQuizEntity() {
    override fun toString(): String {
        return "QuizSessionAnswer(owner=${owner.id}, quizSession=${quizSession.id}, quizSessionQuestion=${quizSessionQuestion.id}, answers=...)"
    }
}
