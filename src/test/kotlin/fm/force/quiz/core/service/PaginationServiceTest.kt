package fm.force.quiz.core.service

import fm.force.quiz.common.dto.PaginationParams
import fm.force.quiz.common.dto.PaginationQuery
import fm.force.quiz.configuration.properties.PaginationValidationProperties
import fm.force.quiz.core.exception.ValidationError
import io.kotlintest.data.forall
import io.kotlintest.shouldBe
import io.kotlintest.shouldThrow
import io.kotlintest.tables.row

open class PaginationServiceTest(
    private val paginationService: PaginationService,
    private val props: PaginationValidationProperties
) : AbstractCRUDServiceTest() {

    init {
        "page validation exceptions" {
            forall(
                row(PaginationQuery(page = -1, pageSize = props.minPageSize)),
                row(PaginationQuery(page = 0, pageSize = props.minPageSize)),
                row(PaginationQuery(page = 1, pageSize = props.minPageSize - 1)),
                row(PaginationQuery(page = 1, pageSize = props.maxPageSize + 1))
            ) { shouldThrow<ValidationError> { paginationService.getPagination(it) } }
        }

        "correct pagination query" {
            forall(
                row(
                    PaginationQuery(page = null, pageSize = null),
                    PaginationParams(page = 0, pageSize = props.minPageSize)
                ),

                row(
                    PaginationQuery(page = 1, pageSize = null),
                    PaginationParams(page = 0, pageSize = props.minPageSize)
                ),

                row(
                    PaginationQuery(page = 1, pageSize = props.maxPageSize),
                    PaginationParams(page = 0, pageSize = props.maxPageSize)
                )

            ) { query, pagination ->
                paginationService.getPagination(query) shouldBe pagination
            }
        }
    }
}
