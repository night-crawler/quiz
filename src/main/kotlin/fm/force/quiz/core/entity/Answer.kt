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

        @ManyToOne(cascade = [(CascadeType.ALL)], fetch = FetchType.LAZY)
        val question: Question,

        val createdAt: Instant = Instant.now()
)
