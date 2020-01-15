package fm.force.quiz.core.dto

import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer
import fm.force.quiz.core.entity.QuizQuestion
import java.time.Instant

data class QuizQuestionFullDTO(
        @JsonSerialize(using = ToStringSerializer::class) val id: Long,
        val seq: Int,
        val question: QuestionFullDTO,
        val createdAt: Instant,
        val updatedAt: Instant
) : DTOFullSerializationMarker

data class QuizQuestionRestrictedDTO(
        @JsonSerialize(using = ToStringSerializer::class) val id: Long,
        val seq: Int,
        val question: QuestionRestrictedDTO
) : DTORestrictedSerializationMarker

data class QuizQuestionPatchDTO(
        var id: Long? = null,
        var quiz: Long? = null,
        var question: Long? = null,
        var seq: Int? = null
) : DTOSerializationMarker

data class QuizQuestionSearchDTO(
        val quiz: Long
) : DTOSearchMarker

fun QuizQuestion.toFullDTO() = QuizQuestionFullDTO(
        id = id,
        seq = seq,
        question = question.toFullDTO(),
        createdAt = createdAt,
        updatedAt = updatedAt
)

fun QuizQuestion.toRestrictedDTO() = QuizQuestionRestrictedDTO(
        id = id,
        seq = seq,
        question = question.toRestrictedDTO()
)
