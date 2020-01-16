package fm.force.quiz.core.dto

import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer
import fm.force.quiz.core.entity.Tag
import java.time.Instant

data class TagFullDTO(
    @JsonSerialize(using = ToStringSerializer::class)
    val id: Long,
    val name: String,
    val slug: String,
    val createdAt: Instant,
    val updatedAt: Instant
) : DTOFullSerializationMarker

data class TagRestrictedDTO(
    @JsonSerialize(using = ToStringSerializer::class)
    val id: Long,
    val name: String,
    val slug: String
) : DTORestrictedSerializationMarker

data class TagPatchDTO(val name: String) : DTOMarker

fun Tag.toFullDTO() = TagFullDTO(
    id = id,
    name = name,
    slug = slug,
    createdAt = createdAt,
    updatedAt = updatedAt
)

fun Tag.toRestrictedDTO() = TagRestrictedDTO(
    id = id,
    name = name,
    slug = slug
)
