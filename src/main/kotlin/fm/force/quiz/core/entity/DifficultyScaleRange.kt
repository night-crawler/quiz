package fm.force.quiz.core.entity

import fm.force.quiz.security.entity.User
import javax.persistence.Entity
import javax.persistence.ManyToOne
import javax.persistence.Table

@Entity
@Table(name = "difficulty_scale_ranges")
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
}
