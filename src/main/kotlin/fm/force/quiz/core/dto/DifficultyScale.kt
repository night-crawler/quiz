package fm.force.quiz.core.dto

import kotlinx.serialization.ContextualSerialization
import kotlinx.serialization.Serializable

@Serializable
data class DifficultyScaleFullDTO(
    @ContextualSerialization
    val id: Long,

    val name: String,
    val difficultyScaleRanges: Collection<DifficultyScaleRangeFullDTO>,

    @ContextualSerialization
    val createdAt: InstantAlias,

    @ContextualSerialization
    val updatedAt: InstantAlias
) : DTOFullSerializationMarker

@Serializable
data class DifficultyScaleRestrictedDTO(
    @ContextualSerialization
    val id: Long,
    val name: String,
    val difficultyScaleRanges: Collection<DifficultyScaleRangeRestrictedDTO>
) : DTORestrictedSerializationMarker

@Serializable
data class DifficultyScalePatchDTO(
    val name: String? = null,
    val max: Int? = null
) : DTOMarker
