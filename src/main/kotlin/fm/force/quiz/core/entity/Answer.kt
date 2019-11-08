package fm.force.quiz.core.entity

import org.hibernate.annotations.GenericGenerator
import java.time.Instant
import javax.persistence.*

@Entity
@Table(name = "answers")
data class Answer(
        @Id
        @GeneratedValue(generator = "increment")
        @GenericGenerator(name = "increment", strategy = "increment")
        val id: Long? = null,

        val text: String,

        @ManyToMany(mappedBy = "answers", fetch = FetchType.LAZY)
        val questions: Set<Question> = setOf(),

        @ManyToMany(mappedBy = "correctAnswers", fetch = FetchType.LAZY)
        val correctInQuestions: Set<Question> = setOf(),

        val createdAt: Instant = Instant.now()
)
