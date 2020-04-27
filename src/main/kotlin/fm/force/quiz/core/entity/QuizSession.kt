package fm.force.quiz.core.entity

import fm.force.quiz.security.entity.User
import java.time.Duration
import java.time.Instant
import javax.persistence.CascadeType
import javax.persistence.Entity
import javax.persistence.ManyToOne
import javax.persistence.OneToMany
import javax.persistence.Table

@Entity
@Table(name = "quiz_sessions")
data class QuizSession(
    @ManyToOne val owner: User,
    @ManyToOne val quiz: Quiz? = null,
    @ManyToOne var difficultyScale: DifficultyScale? = null,

    @OneToMany(mappedBy = "quizSession", cascade = [CascadeType.ALL])
    val questions: List<QuizSessionQuestion> = listOf(),

    var validTill: Instant = Instant.now() + Duration.ofDays(1),
    var isCompleted: Boolean = false,
    var isCancelled: Boolean = false,
    var completedAt: Instant? = null,
    var cancelledAt: Instant? = null
) : BaseQuizEntity()
