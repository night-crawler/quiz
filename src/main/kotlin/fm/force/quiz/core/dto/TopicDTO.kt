package fm.force.quiz.core.dto

import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer
import fm.force.quiz.core.entity.Topic
import java.time.Instant

data class PatchTopicDTO(val title: String) : DTOMarker

data class TopicDTO(
        @JsonSerialize(using = ToStringSerializer::class) val id: Long,
        @JsonSerialize(using = ToStringSerializer::class) val owner: Long,
        val title: String,
        val createdAt: Instant,
        val updatedAt: Instant
) : DTOSerializeMarker

fun Topic.toDTO() = TopicDTO(
        id = id,
        title = title,
        owner = owner.id,
        createdAt = createdAt,
        updatedAt = updatedAt
)
