package fm.force.quiz.core.service

import am.ik.yavi.builder.ValidatorBuilder
import am.ik.yavi.builder.konstraint
import am.ik.yavi.builder.konstraintOnCondition
import am.ik.yavi.core.ConstraintCondition
import am.ik.yavi.core.ConstraintGroup
import fm.force.quiz.configuration.properties.PaginationValidationProperties
import fm.force.quiz.core.dto.PaginationParams
import fm.force.quiz.core.dto.PaginationQuery
import fm.force.quiz.core.exception.ValidationError
import org.springframework.stereotype.Service


@Service
class PaginationService(
        val paginationProps: PaginationValidationProperties
) {
    private val whenPageIsPresent = ConstraintCondition<PaginationQuery> { query, _ -> query.page != null }
    private val whenPageSizeIsPresent = ConstraintCondition<PaginationQuery> { query, _ -> query.pageSize != null }

    val validator = ValidatorBuilder.of<PaginationQuery>()
            .konstraintOnCondition(whenPageIsPresent) {
                konstraint(PaginationQuery::page) {
                    greaterThanOrEqual(1)
                }
            }
            .konstraintOnCondition(whenPageSizeIsPresent) {
                konstraint(PaginationQuery::pageSize) {
                    greaterThanOrEqual(paginationProps.minPageSize).message("Page size must be at least ${paginationProps.minPageSize}")
                    lessThanOrEqual(paginationProps.maxPageSize).message("Page size must be less than or equal to ${paginationProps.maxPageSize}")
                }
            }
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
