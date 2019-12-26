package fm.force.quiz.core.dto

import fm.force.quiz.core.entity.Topic
import java.time.Instant

data class PatchTopicDTO(val title: String)

data class TopicDTO(
        val id: Long,
        val owner: Long,
        val title: String,
        val createdAt: Instant,
        val updatedAt: Instant
)

fun Topic.toDTO() = TopicDTO(
        id = id,
        title = title,
        owner = owner.id,
        createdAt = createdAt,
        updatedAt = updatedAt
)
