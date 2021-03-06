package fm.force.quiz.core.entity

import fm.force.quiz.configuration.BATCH_SIZE
import fm.force.quiz.security.entity.User
import org.hibernate.annotations.BatchSize
import javax.persistence.Entity
import javax.persistence.ManyToOne
import javax.persistence.Table
import javax.persistence.UniqueConstraint

@Entity
@Table(
    name = "difficulty_scale_ranges",
    uniqueConstraints = [UniqueConstraint(columnNames = ["difficulty_scale_id", "title"])]
)
@BatchSize(size = BATCH_SIZE)
data class DifficultyScaleRange(
    @ManyToOne val owner: User,
    @ManyToOne val difficultyScale: DifficultyScale,

    var title: String,

    var min: Int = 0,
    var max: Int = 10

) : BaseQuizEntity() {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is DifficultyScaleRange) return false
        if (!super.equals(other)) return false

        if (owner != other.owner) return false
        if (title != other.title) return false
        if (min != other.min) return false
        if (max != other.max) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + owner.hashCode()
        result = 31 * result + title.hashCode()
        result = 31 * result + min
        result = 31 * result + max
        return result
    }

    override fun toString(): String {
        return "DifficultyScaleRange(id=$id, owner=$owner, difficultyScale=$difficultyScale, title='$title', min=$min, max=$max)"
    }
}
