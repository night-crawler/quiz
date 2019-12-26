package fm.force.quiz.core.dto

import fm.force.quiz.core.entity.Tag
import java.time.Instant

data class PatchTagDTO(val name: String)

data class TagDTO(
        val id: Long,
        val name: String,
        val slug: String,
        val createdAt: Instant,
        val updatedAt: Instant
)

fun Tag.toDTO() = TagDTO(
        id = id,
        name = name,
        slug = slug,
        createdAt = createdAt,
        updatedAt = updatedAt
)
