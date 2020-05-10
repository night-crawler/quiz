package fm.force.quiz.common.dto

import fm.force.quiz.common.serializer.InstantAlias
import kotlinx.serialization.ContextualSerialization
import kotlinx.serialization.Serializable

@Serializable
data class TopicFullDTO(
    @ContextualSerialization
    val id: Long,
    val title: String,
    val slug: String,

    @ContextualSerialization
    val createdAt: InstantAlias,

    @ContextualSerialization
    val updatedAt: InstantAlias
) : DTOFullSerializationMarker

@Serializable
data class TopicRestrictedDTO(
    @ContextualSerialization
    val id: Long,

    val title: String,
    val slug: String
) : DTORestrictedSerializationMarker

@Serializable
data class TopicPatchDTO(val title: String) : DTOMarker
