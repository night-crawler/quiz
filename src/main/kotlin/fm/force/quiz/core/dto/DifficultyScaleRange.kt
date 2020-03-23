package fm.force.quiz.core.dto

import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer
import java.time.Instant

data class DifficultyScaleRangeFullDTO(
    @JsonSerialize(using = ToStringSerializer::class)
    val id: Long,
    @JsonSerialize(using = ToStringSerializer::class)
    val difficultyScale: Long,
    val title: String,
    val min: Int,
    val max: Int,
    val createdAt: Instant,
    val updatedAt: Instant
) : DTOFullSerializationMarker

data class DifficultyScaleRangeRestrictedDTO(
    @JsonSerialize(using = ToStringSerializer::class)
    val id: Long,
    val title: String,
    val min: Int,
    val max: Int
) : DTORestrictedSerializationMarker

data class DifficultyScaleRangePatchDTO(
    val difficultyScale: Long? = null,
    val title: String? = null,
    val min: Int? = null,
    val max: Int? = null
) : DTOMarker

data class DifficultyScaleRangeSearchDTO(
    val difficultyScale: Long? = null,
    val title: String? = null
) : DTOSearchMarker
