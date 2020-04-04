package fm.force.quiz.core.dto

import kotlinx.serialization.ContextualSerialization
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.Instant

@Serializable
@SerialName("TagFullDTO")
data class TagFullDTO(
    @ContextualSerialization
    val id: Long,
    val name: String,
    val slug: String,
    @ContextualSerialization
    val createdAt: Instant,
    @ContextualSerialization
    val updatedAt: Instant
) : DTOFullSerializationMarker

@Serializable
@SerialName("TagRestrictedDTO")
data class TagRestrictedDTO(
    @ContextualSerialization
    val id: Long,
    val name: String,
    val slug: String
) : DTORestrictedSerializationMarker

@Serializable
@SerialName("TagPatchDTO")
data class TagPatchDTO(val name: String) : DTOMarker
