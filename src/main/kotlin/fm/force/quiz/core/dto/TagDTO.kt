package fm.force.quiz.core.dto

import fm.force.quiz.core.entity.Tag
import java.time.Instant

data class CreateTagDTO(val name: String)

data class TagDTO(val id: Long, val name: String, val slug: String, val createdAt: Instant, val owner: Long)

fun Tag.toDTO() = TagDTO(
        id = id!!,
        name = name,
        slug = slug,
        owner = owner.id!!,
        createdAt = createdAt
)
