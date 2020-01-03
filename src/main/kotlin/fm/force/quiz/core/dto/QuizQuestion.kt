package fm.force.quiz.core.dto

import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer
import fm.force.quiz.core.entity.QuizQuestion
import java.time.Instant

data class QuizQuestionFullDTO(
        @JsonSerialize(using = ToStringSerializer::class) val id: Long,
        @JsonSerialize(using = ToStringSerializer::class) val owner: Long,
        val seq: Int,
        val question: QuestionFullDTO,
        val createdAt: Instant,
        val updatedAt: Instant
) : DTOFullSerializationMarker


data class QuizQuestionPatchDTO(
        val id: Long? = null,
        val quiz: Long? = null,
        val question: Long? = null,
        val seq: Int? = null
)

fun QuizQuestion.toFullDTO() = QuizQuestionFullDTO(
        id = id,
        owner = owner.id,
        seq = seq,
        question = question.toFullDTO(),
        createdAt = createdAt,
        updatedAt = updatedAt
)
