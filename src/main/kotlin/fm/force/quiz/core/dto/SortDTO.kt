package fm.force.quiz.core.dto

import kotlinx.serialization.Serializable

@Serializable
data class SortDTO(
    val isSorted: Boolean,
    val isUnsorted: Boolean,
    val isEmpty: Boolean
)
