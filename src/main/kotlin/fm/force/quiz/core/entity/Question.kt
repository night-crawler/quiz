package fm.force.quiz.core.entity

import fm.force.quiz.security.entity.User
import javax.persistence.*
import javax.persistence.JoinColumn
import javax.persistence.JoinTable
import javax.persistence.ManyToMany


@Entity
@Table(name = "questions")
data class Question(
        @ManyToOne val owner: User,

        @Column(columnDefinition = "TEXT")
        val text: String,

        @ManyToMany(targetEntity = Answer::class, fetch = FetchType.LAZY)
        @JoinTable(
                name = "questions__rel_answers__answers",
                joinColumns = [JoinColumn(name = "question_id")],
                inverseJoinColumns = [JoinColumn(name = "answer_id")]
        )
        val answers: Set<Answer> = setOf(),

        @ManyToMany(targetEntity = Answer::class, fetch = FetchType.LAZY)
        @JoinTable(
                name = "questions__rel_correct_answers__answers",
                joinColumns = [JoinColumn(name = "question_id")],
                inverseJoinColumns = [JoinColumn(name = "answer_id")]
        )
        var correctAnswers: Set<Answer> = setOf(),

        @ManyToMany(targetEntity = Tag::class, fetch = FetchType.LAZY)
        @JoinTable(
                name = "questions__tags",
                joinColumns = [JoinColumn(name = "question_id")],
                inverseJoinColumns = [JoinColumn(name = "tag_id")]
        )
        var tags: Set<Tag> = setOf(),

        @ManyToMany(targetEntity = Topic::class, fetch = FetchType.LAZY)
        @JoinTable(
                name = "questions__topics",
                joinColumns = [JoinColumn(name = "question_id")],
                inverseJoinColumns = [JoinColumn(name = "topic_id")]
        )
        var topics: Set<Topic> = setOf(),

        /**
         * Normalized difficulty in range []0;1e6)
         */
        var difficulty: Int = 0

) : BaseQuizEntity()
