package fm.force.quiz.core.dto

import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer
import fm.force.quiz.core.entity.Topic
import java.time.Instant

data class TopicFullDTO(
    @JsonSerialize(using = ToStringSerializer::class)
    val id: Long,
    val title: String,
    val createdAt: Instant,
    val updatedAt: Instant
) : DTOFullSerializationMarker

data class TopicRestrictedDTO(
    @JsonSerialize(using = ToStringSerializer::class)
    val id: Long,
    val title: String
) : DTORestrictedSerializationMarker

data class TopicPatchDTO(val title: String) : DTOMarker

fun Topic.toFullDTO() = TopicFullDTO(
    id = id,
    title = title,
    createdAt = createdAt,
    updatedAt = updatedAt
)

fun Topic.toRestrictedDTO() = TopicRestrictedDTO(id = id, title = title)
