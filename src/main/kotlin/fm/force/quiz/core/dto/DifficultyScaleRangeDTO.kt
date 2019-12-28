package fm.force.quiz.core.dto

import fm.force.quiz.core.entity.DifficultyScaleRange
import java.time.Instant

data class DifficultyScaleRangeDTO(
        val id: Long,
        val owner: Long,
        val difficultyScale: Long,
        val title: String,
        val min: Int,
        val max: Int,
        val createdAt: Instant,
        val updatedAt: Instant
)

data class PatchDifficultyScaleRangeDTO(
        val difficultyScale: Long? = null,
        val title: String? = null,
        val min: Int? = null,
        val max: Int? = null
)

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
