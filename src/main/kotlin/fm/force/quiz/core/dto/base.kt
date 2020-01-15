package fm.force.quiz.core.dto

import fm.force.quiz.core.entity.BaseQuizEntity
import org.springframework.data.domain.Page


inline fun <T : BaseQuizEntity> Page<out T>.toDTO(serialize: (T) -> Any) = PageDTO(
        numberOfElements = numberOfElements,
        totalElements = totalElements,
        totalPages = totalPages,
        sort = sort,
        pageSize = size,
        isFirst = isFirst,
        isLast = isLast,
        content = content.map(serialize)
)

enum class SerializationType {
    FULL, RESTRICTED
}

interface DTOMarker
interface DTOSearchMarker
interface DTOSerializationMarker : DTOMarker
interface DTOFullSerializationMarker : DTOSerializationMarker
interface DTORestrictedSerializationMarker : DTOSerializationMarker


data class SearchQueryDTO(val query: String?) : DTOSearchMarker
