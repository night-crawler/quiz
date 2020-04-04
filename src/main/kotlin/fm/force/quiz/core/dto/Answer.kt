package fm.force.quiz.core.dto

import kotlinx.serialization.ContextualSerialization
import kotlinx.serialization.Serializable
import java.time.Instant

@Serializable
data class AnswerFullDTO(
    @ContextualSerialization
    val id: Long,

    val text: String,

    @ContextualSerialization
    val createdAt: Instant,

    @ContextualSerialization
    val updatedAt: Instant
) : DTOFullSerializationMarker

@Serializable
data class AnswerRestrictedDTO(
    @ContextualSerialization
    val id: Long,
    val text: String
) : DTORestrictedSerializationMarker


@Serializable
data class AnswerPatchDTO(val text: String) : DTOMarker
