package fm.force.quiz.core.entity

import fm.force.quiz.security.entity.User
import javax.persistence.*

@Entity
@Table(name = "quizzes")
data class Quiz(
        @ManyToOne val owner: User,

        var title: String,

        @ManyToMany(targetEntity = Question::class, fetch = FetchType.LAZY)
        @JoinTable(
                name = "quiz__questions",
                joinColumns = [JoinColumn(name = "quiz_id")],
                inverseJoinColumns = [JoinColumn(name = "question_id")]
        )
        var questions: MutableSet<Question> = mutableSetOf(),

        @ManyToMany(targetEntity = Tag::class, fetch = FetchType.LAZY)
        @JoinTable(
                name = "quizzes__tags",
                joinColumns = [JoinColumn(name = "quiz_id")],
                inverseJoinColumns = [JoinColumn(name = "tag_id")]
        )
        var tags: MutableSet<Tag> = mutableSetOf(),

        @ManyToMany(targetEntity = Topic::class, fetch = FetchType.LAZY)
        @JoinTable(
                name = "quizzes__topics",
                joinColumns = [JoinColumn(name = "quiz_id")],
                inverseJoinColumns = [JoinColumn(name = "topic_id")]
        )
        var topics: MutableSet<Topic> = mutableSetOf(),

        @ManyToOne var difficultyScale: DifficultyScale? = null

) : BaseQuizEntity()
