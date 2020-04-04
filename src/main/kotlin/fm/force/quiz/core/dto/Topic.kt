package fm.force.quiz.core.dto

import kotlinx.serialization.ContextualSerialization
import kotlinx.serialization.Serializable

@Serializable
data class TopicFullDTO(
    @ContextualSerialization
    val id: Long,
    val title: String,

    @ContextualSerialization
    val createdAt: InstantAlias,

    @ContextualSerialization
    val updatedAt: InstantAlias
) : DTOFullSerializationMarker

@Serializable
data class TopicRestrictedDTO(
    @ContextualSerialization
    val id: Long,

    val title: String
) : DTORestrictedSerializationMarker

@Serializable
data class TopicPatchDTO(val title: String) : DTOMarker
