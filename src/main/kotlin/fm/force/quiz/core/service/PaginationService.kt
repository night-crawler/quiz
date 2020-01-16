package fm.force.quiz.core.service

import am.ik.yavi.builder.ValidatorBuilder
import fm.force.quiz.configuration.properties.PaginationValidationProperties
import fm.force.quiz.core.dto.PaginationParams
import fm.force.quiz.core.dto.PaginationQuery
import fm.force.quiz.core.exception.ValidationError
import fm.force.quiz.core.validator.intConstraint
import org.springframework.stereotype.Service

@Service
class PaginationService(
    private val paginationProps: PaginationValidationProperties
) {
    val validator = ValidatorBuilder.of<PaginationQuery>()
        .intConstraint(PaginationQuery::page, 1..Int.MAX_VALUE)
        .intConstraint(PaginationQuery::pageSize, paginationProps.minPageSize..paginationProps.maxPageSize)
        .build()

    fun validate(paginationQuery: PaginationQuery) = validator
        .validate(paginationQuery)
        .throwIfInvalid { ValidationError(it) }

    fun getPagination(paginationQuery: PaginationQuery): PaginationParams {
        validate(paginationQuery)
        return PaginationParams(
            page = (paginationQuery.page ?: 1) - 1,
            pageSize = paginationQuery.pageSize ?: paginationProps.minPageSize
        )
    }
}
