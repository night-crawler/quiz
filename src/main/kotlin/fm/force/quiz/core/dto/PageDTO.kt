package fm.force.quiz.core.dto

import org.springframework.data.domain.Sort

data class PageDTO(
        val sort: Sort,
        val numberOfElements: Int,
        val totalElements: Long,
        val totalPages: Int,
        val pageSize: Int,
        val isLast: Boolean,
        val isFirst: Boolean,
        val content: Collection<Any>
)