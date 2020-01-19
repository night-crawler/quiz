package fm.force.quiz.core.dto

import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer
import fm.force.quiz.core.entity.QuizSession
import java.time.Instant

data class QuizSessionFullDTO(
    @JsonSerialize(using = ToStringSerializer::class)
    val id: Long,

    @JsonSerialize(using = ToStringSerializer::class)
    val quiz: Long,

    @JsonSerialize(using = ToStringSerializer::class)
    val difficultyScale: Long?,

    val validTill: Instant,
    val isCompleted: Boolean = false,
    val isCancelled: Boolean = false,
    val completedAt: Instant? = null,
    val cancelledAt: Instant? = null,
    val createdAt: Instant,
    val updatedAt: Instant
) : DTOFullSerializationMarker

data class QuizSessionPatchDTO(
    var quiz: Long
) : DTOSerializationMarker

data class QuizSessionSearchDTO(
    val quiz: Long? = null,
    val difficultyScale: Long? = null,
    val isCompleted: Boolean? = null,
    val isCancelled: Boolean? = null
) : DTOSearchMarker

fun QuizSession.toFullDTO() = QuizSessionFullDTO(
    id = id,
    quiz = quiz.id,
    difficultyScale = difficultyScale?.id,
    validTill = validTill,
    isCompleted = isCompleted,
    isCancelled = isCancelled,
    completedAt = completedAt,
    cancelledAt = cancelledAt,
    createdAt = createdAt,
    updatedAt = updatedAt
)
