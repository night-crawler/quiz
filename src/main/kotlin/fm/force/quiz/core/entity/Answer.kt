package fm.force.quiz.core.entity

import fm.force.quiz.security.entity.User
import javax.persistence.*

@Entity
@Table(name = "answers")
data class Answer(
        @Column(columnDefinition = "TEXT")
        val text: String,

        @ManyToMany(mappedBy = "answers", fetch = FetchType.LAZY)
        val questions: Set<Question> = setOf(),

        @ManyToMany(mappedBy = "correctAnswers", fetch = FetchType.LAZY)
        val correctInQuestions: Set<Question> = setOf(),

        @ManyToOne
        val owner: User
) : BaseQuizEntity()
