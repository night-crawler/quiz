package fm.force.quiz.core.entity

import fm.force.quiz.security.entity.User
import java.time.Duration
import java.time.Instant
import java.time.temporal.TemporalAmount
import javax.persistence.Entity
import javax.persistence.ManyToOne
import javax.persistence.Table

@Entity
@Table(name = "quiz_sessions")
data class QuizSession(
        @ManyToOne val owner: User,
        @ManyToOne val quiz: Quiz,
        @ManyToOne var difficultyScale: DifficultyScale? = null,
        var validTill: Instant = Instant.now() + Duration.ofDays(1),
        var isCompleted: Boolean = false,
        var isCancelled: Boolean = false,
        var completedAt: Instant? = null,
        var cancelledAt: Instant? = null
) : BaseQuizEntity()
