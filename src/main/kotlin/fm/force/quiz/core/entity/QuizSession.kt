package fm.force.quiz.core.entity

import fm.force.quiz.configuration.BATCH_SIZE
import fm.force.quiz.security.entity.User
import org.hibernate.annotations.BatchSize
import java.time.Duration
import java.time.Instant
import javax.persistence.CascadeType
import javax.persistence.Entity
import javax.persistence.ManyToOne
import javax.persistence.OneToMany
import javax.persistence.Table

@Entity
@Table(name = "quiz_sessions")
@BatchSize(size = BATCH_SIZE)
data class QuizSession(
    @ManyToOne val owner: User,
    @ManyToOne val quiz: Quiz? = null,
    @ManyToOne var difficultyScale: DifficultyScale? = null,

    @OneToMany(mappedBy = "quizSession", cascade = [CascadeType.ALL])
    @BatchSize(size = BATCH_SIZE)
    var questions: MutableList<QuizSessionQuestion> = mutableListOf(),

    var validTill: Instant = Instant.now() + Duration.ofDays(1),
    var isCompleted: Boolean = false,
    var isCancelled: Boolean = false,
    var completedAt: Instant? = null,
    var cancelledAt: Instant? = null
) : BaseQuizEntity() {
    override fun toString(): String {
        return "QuizSession(owner=$owner, quiz=${quiz?.id}, difficultyScale=${difficultyScale?.id}, " +
            "validTill=$validTill, isCompleted=$isCompleted, isCancelled=$isCancelled, " +
            "completedAt=$completedAt, cancelledAt=$cancelledAt)"
    }
}
