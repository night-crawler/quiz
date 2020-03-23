package fm.force.quiz.core.dto

import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer
import java.time.Instant

data class DifficultyScaleFullDTO(
    @JsonSerialize(using = ToStringSerializer::class)
    val id: Long,
    val name: String,
    val difficultyScaleRanges: Collection<DifficultyScaleRangeFullDTO>,
    val createdAt: Instant,
    val updatedAt: Instant
) : DTOFullSerializationMarker

data class DifficultyScaleRestrictedDTO(
    val id: Long,
    val name: String,
    val difficultyScaleRanges: Collection<DifficultyScaleRangeRestrictedDTO>
) : DTORestrictedSerializationMarker

data class DifficultyScalePatchDTO(
    val name: String? = null,
    val max: Int? = null
) : DTOMarker
