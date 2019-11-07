package fm.force.quiz.core.entity

import com.vladmihalcea.hibernate.type.array.IntArrayType
import org.hibernate.annotations.GenericGenerator
import java.time.Instant
import javax.persistence.*
import org.hibernate.annotations.Type
import org.hibernate.annotations.TypeDef


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

        @OneToMany(mappedBy = "question", fetch = FetchType.LAZY)
        val answers: Set<Answer> = setOf(),

        @Type(type = "int-array")
        @Column(name = "correct_answers", columnDefinition = "integer[]")
        var correctAnswers: Array<Number> = arrayOf(),

        @Column(name = "created_at")
        val createdAt: Instant = Instant.now()
) {
    override fun toString(): String {
        return "Question(id=$id text=$text createdAt=$createdAt)"
    }
}
