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
): BaseQuizEntity()
