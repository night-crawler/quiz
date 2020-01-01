package fm.force.quiz.core.dto

import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer
import fm.force.quiz.core.entity.DifficultyScaleRange
import java.time.Instant

data class DifficultyScaleRangeDTO(
        @JsonSerialize(using = ToStringSerializer::class)
        val id: Long,
        @JsonSerialize(using = ToStringSerializer::class) val owner: Long,
        val difficultyScale: Long,
        val title: String,
        val min: Int,
        val max: Int,
        val createdAt: Instant,
        val updatedAt: Instant
) : DTOSerializeMarker

data class PatchDifficultyScaleRangeDTO(
        val difficultyScale: Long? = null,
        val title: String? = null,
        val min: Int? = null,
        val max: Int? = null
) : DTOMarker

fun DifficultyScaleRange.toDTO() = DifficultyScaleRangeDTO(
        id = id,
        owner = owner.id,
        difficultyScale = difficultyScale.id,
        title = title,
        min = min,
        max = max,
        createdAt = createdAt,
        updatedAt = updatedAt
)
