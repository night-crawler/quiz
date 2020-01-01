package fm.force.quiz.core.dto

import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer
import fm.force.quiz.core.entity.Quiz
import java.time.Instant

data class QuizFullDTO(
        @JsonSerialize(using = ToStringSerializer::class) val id: Long,
        @JsonSerialize(using = ToStringSerializer::class) val owner: Long,
        val title: String,
        val tags: Collection<TagFullDTO>,
        val topics: Collection<TopicFullDTO>,
        val quizQuestions: Collection<QuizQuestionFullDTO>,
        val difficultyScale: DifficultyScaleFullDTO?,
        val createdAt: Instant,
        val updatedAt: Instant
) : DTOFullSerializationMarker

data class PatchQuizDTO(
        val title: String? = null,
        val questions: Set<Long>? = null,
        val tags: Set<Long>? = null,
        val topics: Set<Long>? = null,
        val difficultyScale: Long? = null
) : DTOMarker


fun Quiz.toFullDTO() = QuizFullDTO(
        id = id,
        owner = owner.id,
        title = title,
        tags = tags.map { it.toFullDTO() },
        topics = topics.map { it.toFullDTO() },
        quizQuestions = quizQuestions.map { it.toFullDTO() },
        difficultyScale = difficultyScale?.toFullDTO(),
        createdAt = createdAt,
        updatedAt = updatedAt
)

