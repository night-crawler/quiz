package fm.force.quiz.core.dto

import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer
import java.time.Instant

data class AnswerFullDTO(
    @JsonSerialize(using = ToStringSerializer::class)
    val id: Long,
    val text: String,
    val createdAt: Instant,
    val updatedAt: Instant
) : DTOFullSerializationMarker

data class AnswerRestrictedDTO(
    @JsonSerialize(using = ToStringSerializer::class)
    val id: Long,
    val text: String
) : DTORestrictedSerializationMarker

data class AnswerPatchDTO(val text: String) : DTOMarker
