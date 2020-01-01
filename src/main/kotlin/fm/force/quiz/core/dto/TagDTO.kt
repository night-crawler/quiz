package fm.force.quiz.core.dto

import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer
import fm.force.quiz.core.entity.Tag
import java.time.Instant

data class PatchTagDTO(val name: String) : DTOMarker

data class TagDTO(
        @JsonSerialize(using = ToStringSerializer::class) val id: Long,
        val name: String,
        val slug: String,
        val createdAt: Instant,
        val updatedAt: Instant
) : DTOSerializeMarker

fun Tag.toDTO() = TagDTO(
        id = id,
        name = name,
        slug = slug,
        createdAt = createdAt,
        updatedAt = updatedAt
)
