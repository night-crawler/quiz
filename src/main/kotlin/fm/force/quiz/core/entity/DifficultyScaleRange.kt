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

) : BaseQuizEntity()
