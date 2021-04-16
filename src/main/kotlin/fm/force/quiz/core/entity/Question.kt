package fm.force.quiz.core.entity

import fm.force.quiz.configuration.BATCH_SIZE
import fm.force.quiz.security.entity.User
import org.hibernate.annotations.BatchSize
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.JoinColumn
import javax.persistence.JoinTable
import javax.persistence.ManyToMany
import javax.persistence.ManyToOne
import javax.persistence.Table

@Entity
@Table(name = "questions")
@BatchSize(size = BATCH_SIZE)
data class Question(
    @ManyToOne val owner: User,

    @Column(columnDefinition = "TEXT")
    var title: String,

    @Column(columnDefinition = "TEXT")
    var text: String,

    @Column(columnDefinition = "TEXT")
    var help: String,

    @ManyToMany(targetEntity = Answer::class, fetch = FetchType.LAZY)
    @JoinTable(
        name = "questions__rel_answers__answers",
        joinColumns = [JoinColumn(name = "question_id")],
        inverseJoinColumns = [JoinColumn(name = "answer_id")]
    )
    @BatchSize(size = BATCH_SIZE)
    var answers: MutableSet<Answer> = mutableSetOf(),

    @ManyToMany(targetEntity = Answer::class, fetch = FetchType.LAZY)
    @JoinTable(
        name = "questions__rel_correct_answers__answers",
        joinColumns = [JoinColumn(name = "question_id")],
        inverseJoinColumns = [JoinColumn(name = "answer_id")]
    )
    @BatchSize(size = BATCH_SIZE)
    var correctAnswers: MutableSet<Answer> = mutableSetOf(),

    @ManyToMany(targetEntity = Tag::class, fetch = FetchType.LAZY)
    @JoinTable(
        name = "questions__tags",
        joinColumns = [JoinColumn(name = "question_id")],
        inverseJoinColumns = [JoinColumn(name = "tag_id")]
    )
    @BatchSize(size = BATCH_SIZE)
    var tags: MutableSet<Tag> = mutableSetOf(),

    @ManyToMany(targetEntity = Topic::class, fetch = FetchType.LAZY)
    @JoinTable(
        name = "questions__topics",
        joinColumns = [JoinColumn(name = "question_id")],
        inverseJoinColumns = [JoinColumn(name = "topic_id")]
    )
    @BatchSize(size = BATCH_SIZE)
    var topics: MutableSet<Topic> = mutableSetOf(),

    /**
     * Normalized difficulty in range [0;1e6)
     */
    var difficulty: Int = 0

) : BaseQuizEntity()
