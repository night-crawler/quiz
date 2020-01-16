package fm.force.quiz.core.dto

import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer
import fm.force.quiz.core.entity.Question
import java.time.Instant

data class QuestionFullDTO(
    @JsonSerialize(using = ToStringSerializer::class)
    val id: Long,
    val text: String,
    val answers: Collection<AnswerFullDTO>,
    val correctAnswers: Collection<AnswerFullDTO>,
    val tags: Collection<TagFullDTO>,
    val topics: Collection<TopicFullDTO>,
    val difficulty: Int,
    val createdAt: Instant,
    val updatedAt: Instant
) : DTOFullSerializationMarker

data class QuestionRestrictedDTO(
    @JsonSerialize(using = ToStringSerializer::class)
    val id: Long,
    val text: String,
    val answers: Collection<AnswerRestrictedDTO>,
    val difficulty: Int
) : DTORestrictedSerializationMarker

data class QuestionPatchDTO(
    val text: String? = null,
    val answers: Set<Long>? = null,
    val correctAnswers: Set<Long>? = null,
    val tags: Set<Long>? = null,
    val topics: Set<Long>? = null,
    val difficulty: Int? = 0
) : DTOMarker

fun Question.toFullDTO() = QuestionFullDTO(
    id = id,
    text = text,
    answers = answers.map { it.toFullDTO() },
    correctAnswers = correctAnswers.map { it.toFullDTO() },
    tags = tags.map { it.toFullDTO() },
    topics = topics.map { it.toFullDTO() },
    difficulty = difficulty,
    createdAt = createdAt,
    updatedAt = updatedAt
)

fun Question.toRestrictedDTO() = QuestionRestrictedDTO(
    id = id,
    text = text,
    answers = answers.map { it.toRestrictedDTO() },
    difficulty = difficulty
)
