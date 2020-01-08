package fm.force.quiz.core.entity

import fm.force.quiz.security.entity.User
import javax.persistence.*

@Entity
@Table(name = "quizzes")
data class Quiz(
        @ManyToOne val owner: User,

        var title: String,

        @OneToMany(mappedBy = "quiz")
        @OrderBy("seq ASC")
        var quizQuestions: MutableList<QuizQuestion> = mutableListOf(),

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

) : BaseQuizEntity() {
        override fun equals(other: Any?): Boolean {
                if (this === other) return true
                if (other !is Quiz) return false
                if (!super.equals(other)) return false

                if (owner != other.owner) return false
                if (title != other.title) return false
                if (tags != other.tags) return false
                if (topics != other.topics) return false
                if (difficultyScale != other.difficultyScale) return false

                return true
        }

        override fun hashCode(): Int {
                var result = super.hashCode()
                result = 31 * result + owner.hashCode()
                result = 31 * result + title.hashCode()
                result = 31 * result + tags.hashCode()
                result = 31 * result + topics.hashCode()
                result = 31 * result + (difficultyScale?.hashCode() ?: 0)
                return result
        }
}
