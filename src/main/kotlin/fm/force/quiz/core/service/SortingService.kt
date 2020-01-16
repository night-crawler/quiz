package fm.force.quiz.core.service

import am.ik.yavi.builder.ValidatorBuilder
import am.ik.yavi.builder.konstraint
import am.ik.yavi.builder.konstraintOnCondition
import am.ik.yavi.core.ConstraintCondition
import am.ik.yavi.core.ConstraintGroup
import fm.force.quiz.configuration.properties.SortingValidationProperties
import fm.force.quiz.core.dto.SortQuery
import fm.force.quiz.core.exception.SortingViolations
import fm.force.quiz.core.exception.ValidationError
import fm.force.quiz.core.validator.nonEmptyString
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service

@Service
class SortingService(
    val validationProps: SortingValidationProperties
) {
    private val whenSortIsPresent = ConstraintCondition<SortQuery> { sortQuery, _: ConstraintGroup? -> sortQuery.sort != null }
    val validator = ValidatorBuilder.of<SortQuery>()
        .konstraintOnCondition(whenSortIsPresent) {
            konstraint(SortQuery::sort) {
                lessThanOrEqual(validationProps.maxSortingFields).message(
                    "The `sort` parameter must contain max ${validationProps.maxSortingFields} items"
                )
            }
                .forEach(SortQuery::sort, "sort", nonEmptyString)
        }
        .build()

    fun validate(sortQuery: SortQuery) = validator
        .validate(sortQuery)
        .throwIfInvalid { ValidationError(it) }

    fun getSorting(
        sortQuery: SortQuery,
        defaultSortFieldName: String = "id",
        defaultSortDirection: Sort.Direction = Sort.Direction.ASC,
        allowedFields: Collection<String> = emptySet()
    ): Sort {
        validate(sortQuery)

        val sortFields = sortQuery.sort
        if (sortFields.isNullOrEmpty()) {
            return Sort.by(defaultSortDirection, defaultSortFieldName)
        }

        val orders = mutableSetOf<Sort.Order>()
        val checkAllowedFields = allowedFields.isNotEmpty()
        sortFields.forEach {
            val parts = it.split("-")
            val property = parts.last()
            when {
                parts.size > 2 || property == "-" ->
                    throw ValidationError(SortingViolations(it, "Sorting argument `{0}` must not contain more than one '-'"))
                property.isEmpty() ->
                    throw ValidationError(SortingViolations(it, "`{0}` must contain a field name"))
                checkAllowedFields && !allowedFields.contains(property) ->
                    throw ValidationError(SortingViolations(it, "Sorting by `{0}` is not allowed"))
            }

            val direction = if (parts.size == 2) Sort.Direction.DESC else Sort.Direction.ASC
            orders.add(Sort.Order(direction, property))
        }

        return Sort.by(*orders.toTypedArray())
    }
}
