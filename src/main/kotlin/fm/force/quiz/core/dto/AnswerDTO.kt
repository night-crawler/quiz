package fm.force.quiz.core.dto

import fm.force.quiz.core.entity.Answer
import java.time.Instant

data class AnswerDTO(
        val id: Long,
        val owner: Long,
        val text: String,
        val createdAt: Instant,
        val updatedAt: Instant
)

data class CreateAnswerDTO(
        val text: String
)


fun Answer.toDTO() = AnswerDTO(
        id = id,
        owner = owner.id,
        text = text,
        createdAt = createdAt,
        updatedAt = updatedAt
)
