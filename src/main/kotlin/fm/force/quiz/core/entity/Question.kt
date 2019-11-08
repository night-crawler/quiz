package fm.force.quiz.core.entity

import com.vladmihalcea.hibernate.type.array.IntArrayType
import org.hibernate.annotations.GenericGenerator
import java.time.Instant
import javax.persistence.*
import org.hibernate.annotations.Type
import org.hibernate.annotations.TypeDef
import javax.persistence.JoinColumn
import javax.persistence.JoinTable
import javax.persistence.ManyToMany



@Entity
@Table(name = "questions")
@TypeDef(
        name = "int-array",
        typeClass = IntArrayType::class
)
data class Question(
        @Id
        @GeneratedValue(generator = "increment")
        @GenericGenerator(name = "increment", strategy = "increment")
        val id: Long? = null,

        val text: String,

        @ManyToMany(targetEntity = Answer::class, fetch = FetchType.LAZY)
        @JoinTable(
                name = "questions__rel_answers__answers",
                joinColumns = [JoinColumn(name = "question_id")],
                inverseJoinColumns = [JoinColumn(name = "answer_id")]
        )
        val answers: Set<Answer> = setOf(),

        @ManyToMany(targetEntity = Answer::class, fetch = FetchType.LAZY)
        @JoinTable(
                name = "questions__rel_correct_answers__answers",
                joinColumns = [JoinColumn(name = "question_id")],
                inverseJoinColumns = [JoinColumn(name = "answer_id")]
        )
        var correctAnswers: Set<Answer> = setOf(),

        @Column(name = "created_at")
        val createdAt: Instant = Instant.now()
) {
    override fun toString(): String {
        return "Question(id=$id text=$text createdAt=$createdAt)"
    }
}
