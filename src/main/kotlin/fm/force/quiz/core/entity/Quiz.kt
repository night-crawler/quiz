package fm.force.quiz.core.entity

import fm.force.quiz.configuration.BATCH_SIZE
import fm.force.quiz.security.entity.User
import org.hibernate.annotations.BatchSize
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.JoinColumn
import javax.persistence.JoinTable
import javax.persistence.ManyToMany
import javax.persistence.ManyToOne
import javax.persistence.OneToMany
import javax.persistence.OrderBy
import javax.persistence.Table

@Entity
@Table(name = "quizzes")
@BatchSize(size = BATCH_SIZE)
data class Quiz(
    @ManyToOne val owner: User,

    var title: String,

    @OneToMany(mappedBy = "quiz")
    @OrderBy("seq ASC")
    @BatchSize(size = BATCH_SIZE)
    var quizQuestions: MutableList<QuizQuestion> = mutableListOf(),

    @ManyToMany(targetEntity = Tag::class, fetch = FetchType.LAZY)
    @JoinTable(
        name = "quizzes__tags",
        joinColumns = [JoinColumn(name = "quiz_id")],
        inverseJoinColumns = [JoinColumn(name = "tag_id")]
    )
    @BatchSize(size = BATCH_SIZE)
    var tags: MutableSet<Tag> = mutableSetOf(),

    @ManyToMany(targetEntity = Topic::class, fetch = FetchType.LAZY)
    @JoinTable(
        name = "quizzes__topics",
        joinColumns = [JoinColumn(name = "quiz_id")],
        inverseJoinColumns = [JoinColumn(name = "topic_id")]
    )
    @BatchSize(size = BATCH_SIZE)
    var topics: MutableSet<Topic> = mutableSetOf(),

    @ManyToOne
    var difficultyScale: DifficultyScale? = null

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

    override fun toString(): String {
        return "Quiz(owner=$owner, title='$title', difficultyScale=$difficultyScale)"
    }

}
