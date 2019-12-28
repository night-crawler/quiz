package fm.force.quiz.core.dto

import fm.force.quiz.core.entity.DifficultyScale
import java.time.Instant

data class DifficultyScaleDTO(
        val id: Long,
        val name: String,
        val difficultyScaleRanges: Collection<DifficultyScaleRangeDTO>,
        val createdAt: Instant,
        val updatedAt: Instant
)


data class PatchDifficultyScaleDTO(
        val name: String? = null,
        val max: Int? = null
)


fun DifficultyScale.toDTO() = DifficultyScaleDTO(
        id = id,
        name = name,
        difficultyScaleRanges = difficultyScaleRanges.map { it.toDTO() },
        createdAt = createdAt,
        updatedAt = updatedAt
)
