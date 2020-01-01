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


interface DTOMarker
interface DTOSerializeMarker : DTOMarker
