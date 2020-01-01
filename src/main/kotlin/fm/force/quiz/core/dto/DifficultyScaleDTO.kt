package fm.force.quiz.core.dto

import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer
import fm.force.quiz.core.entity.DifficultyScale
import java.time.Instant

data class DifficultyScaleDTO(
        @JsonSerialize(using = ToStringSerializer::class) val id: Long,
        val name: String,
        val difficultyScaleRanges: Collection<DifficultyScaleRangeDTO>,
        val createdAt: Instant,
        val updatedAt: Instant
) : DTOSerializeMarker


data class PatchDifficultyScaleDTO(
        val name: String? = null,
        val max: Int? = null
) : DTOMarker


fun DifficultyScale.toDTO() = DifficultyScaleDTO(
        id = id,
        name = name,
        difficultyScaleRanges = difficultyScaleRanges.map { it.toDTO() },
        createdAt = createdAt,
        updatedAt = updatedAt
)
