package fm.force.quiz.core.dto

import kotlinx.serialization.ContextualSerialization
import kotlinx.serialization.Serializable
import java.time.Instant

@Serializable
data class QuizSessionFullDTO(
    @ContextualSerialization
    val id: Long,

    @ContextualSerialization
    val quiz: Long,

    @ContextualSerialization
    val difficultyScale: Long?,

    @ContextualSerialization
    val validTill: Instant,

    val isCompleted: Boolean = false,
    val isCancelled: Boolean = false,

    @ContextualSerialization
    val completedAt: Instant? = null,

    @ContextualSerialization
    val cancelledAt: Instant? = null,

    @ContextualSerialization
    val createdAt: InstantAlias,

    @ContextualSerialization
    val updatedAt: InstantAlias
) : DTOFullSerializationMarker

@Serializable
data class QuizSessionPatchDTO(
    @ContextualSerialization
    var quiz: Long
) : DTOSerializationMarker

@Serializable
data class QuizSessionSearchDTO(
    @ContextualSerialization
    val quiz: Long? = null,
    val difficultyScale: Long? = null,
    val isCompleted: Boolean? = null,
    val isCancelled: Boolean? = null
) : DTOSearchMarker
