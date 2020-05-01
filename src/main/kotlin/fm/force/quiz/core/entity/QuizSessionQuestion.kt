package fm.force.quiz.core.entity

import fm.force.quiz.security.entity.User
import javax.persistence.CascadeType
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.ManyToOne
import javax.persistence.OneToMany
import javax.persistence.Table
import javax.persistence.UniqueConstraint

@Entity
@Table(
    name = "quiz_session_questions",
    uniqueConstraints = [UniqueConstraint(columnNames = ["quiz_session_id", "original_question_id"])]
)
data class QuizSessionQuestion(
    @ManyToOne val owner: User,
    @ManyToOne val quizSession: QuizSession,
    @ManyToOne val originalQuestion: Question? = null,

    @OneToMany(mappedBy = "quizSessionQuestion", cascade = [CascadeType.ALL])
    val answers: List<QuizSessionQuestionAnswer> = listOf(),

    @Column(columnDefinition = "TEXT")
    var title: String,

    @Column(columnDefinition = "TEXT")
    var text: String,

    @Column(columnDefinition = "TEXT")
    var help: String,

    var difficulty: Int,

    val seq: Int
) : BaseQuizEntity() {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is QuizSessionQuestion) return false
        if (!super.equals(other)) return false

        if (owner != other.owner) return false
        if (quizSession != other.quizSession) return false
        if (originalQuestion != other.originalQuestion) return false
        if (text != other.text) return false
        if (seq != other.seq) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + owner.hashCode()
        result = 31 * result + quizSession.hashCode()
        result = 31 * result + (originalQuestion?.hashCode() ?: 0)
        result = 31 * result + text.hashCode()
        result = 31 * result + seq
        return result
    }

    override fun toString() =
        "QuizSessionQuestion(owner=${owner.id}, quizSession=${quizSession.id}, " +
            "originalQuestion=${originalQuestion?.id}, text='$text', seq=$seq)"
}
