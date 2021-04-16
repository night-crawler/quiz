package fm.force.quiz.core.entity

import fm.force.quiz.configuration.BATCH_SIZE
import fm.force.quiz.security.entity.User
import org.hibernate.annotations.BatchSize
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.ManyToMany
import javax.persistence.ManyToOne
import javax.persistence.Table

@Entity
@BatchSize(size = BATCH_SIZE)
@Table(name = "answers")
data class Answer(
    @Column(columnDefinition = "TEXT")
    var text: String,

    @ManyToMany(mappedBy = "answers", fetch = FetchType.LAZY)
    @BatchSize(size = BATCH_SIZE)
    val questions: Set<Question> = setOf(),

    @ManyToMany(mappedBy = "correctAnswers", fetch = FetchType.LAZY)
    @BatchSize(size = BATCH_SIZE)
    val correctInQuestions: Set<Question> = setOf(),

    @ManyToOne
    val owner: User
) : BaseQuizEntity() {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Answer) return false
        if (!super.equals(other)) return false

        if (text != other.text) return false
        if (owner != other.owner) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + text.hashCode()
        result = 31 * result + owner.hashCode()
        return result
    }
}
