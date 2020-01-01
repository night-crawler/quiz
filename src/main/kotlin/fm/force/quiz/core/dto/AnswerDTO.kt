package fm.force.quiz.core.dto

import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer
import fm.force.quiz.core.entity.Answer
import java.time.Instant

data class AnswerDTO(
        @JsonSerialize(using = ToStringSerializer::class) val id: Long,
        @JsonSerialize(using = ToStringSerializer::class) val owner: Long,
        val text: String,
        val createdAt: Instant,
        val updatedAt: Instant
) : DTOSerializeMarker

data class CreateAnswerDTO(
        val text: String
) : DTOMarker


fun Answer.toDTO() = AnswerDTO(
        id = id,
        owner = owner.id,
        text = text,
        createdAt = createdAt,
        updatedAt = updatedAt
)
