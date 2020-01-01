package fm.force.quiz.core.dto

import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer
import fm.force.quiz.core.entity.Question
import java.time.Instant

data class PatchQuestionDTO(
        val text: String? = null,
        val answers: Set<Long>? = null,
        val correctAnswers: Set<Long>? = null,
        val tags: Set<Long>? = null,
        val topics: Set<Long>? = null,
        val difficulty: Int? = 0
) : DTOMarker

data class QuestionDTO(
        @JsonSerialize(using = ToStringSerializer::class) val id: Long,
        @JsonSerialize(using = ToStringSerializer::class) val owner: Long,
        val answers: Collection<AnswerDTO>,
        val correctAnswers: Collection<AnswerDTO>,
        val tags: Collection<TagDTO>,
        val topics: Collection<TopicDTO>,
        val difficulty: Int,
        val createdAt: Instant,
        val updatedAt: Instant
) : DTOSerializeMarker


fun Question.toDTO() = QuestionDTO(
        id = id,
        owner = owner.id,
        answers = answers.map { it.toDTO() },
        correctAnswers = correctAnswers.map { it.toDTO() },
        tags = tags.map { it.toDTO() },
        topics = topics.map { it.toDTO() },
        difficulty = difficulty,
        createdAt = createdAt,
        updatedAt = updatedAt
)
