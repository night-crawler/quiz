package fm.force.quiz.core.entity

import fm.force.quiz.security.entity.User
import javax.persistence.Entity
import javax.persistence.ManyToOne
import javax.persistence.OneToMany
import javax.persistence.Table

@Entity
@Table(name="difficulty_scales")
data class DifficultyScale(
        @ManyToOne val owner: User,

        var name: String,
        var max: Int = 9,

        @OneToMany(mappedBy = "difficultyScale")
        val difficultyScaleRanges: MutableSet<DifficultyScaleRange> = mutableSetOf()
): BaseQuizEntity() {
        override fun equals(other: Any?): Boolean {
                if (this === other) return true
                if (other !is DifficultyScale) return false
                if (!super.equals(other)) return false

                if (owner != other.owner) return false
                if (name != other.name) return false
                if (max != other.max) return false

                return true
        }

        override fun hashCode(): Int {
                var result = super.hashCode()
                result = 31 * result + owner.hashCode()
                result = 31 * result + name.hashCode()
                result = 31 * result + max
                return result
        }

        override fun toString(): String {
                return "DifficultyScale(id=$id owner=${owner.id}, name='$name', max=$max)"
        }

}
