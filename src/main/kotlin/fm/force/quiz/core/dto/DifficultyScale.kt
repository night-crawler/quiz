package fm.force.quiz.core.dto

import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer
import fm.force.quiz.core.entity.DifficultyScale
import java.time.Instant

data class DifficultyScaleFullDTO(
        @JsonSerialize(using = ToStringSerializer::class) val id: Long,
        val name: String,
        val difficultyScaleRanges: Collection<DifficultyScaleRangeFullDTO>,
        val createdAt: Instant,
        val updatedAt: Instant
) : DTOFullSerializationMarker

data class DifficultyScaleRestrictedDTO(
        val id: Long,
        val name: String,
        val difficultyScaleRanges: Collection<DifficultyScaleRangeFullDTO>
) : DTORestrictedSerializationMarker

data class DifficultyScalePatchDTO(
        val name: String? = null,
        val max: Int? = null
) : DTOMarker

fun DifficultyScale.toFullDTO() = DifficultyScaleFullDTO(
        id = id,
        name = name,
        difficultyScaleRanges = difficultyScaleRanges.map { it.toFullDTO() },
        createdAt = createdAt,
        updatedAt = updatedAt
)
