package fm.force.quiz.core.service

import fm.force.quiz.TestConfiguration
import fm.force.quiz.YamlPropertyLoaderFactory
import fm.force.quiz.configuration.properties.PaginationValidationProperties
import fm.force.quiz.core.dto.PaginationParams
import fm.force.quiz.core.dto.PaginationQuery
import fm.force.quiz.core.exception.ValidationError
import io.kotlintest.data.forall
import io.kotlintest.shouldBe
import io.kotlintest.shouldThrow
import io.kotlintest.specs.StringSpec
import io.kotlintest.tables.row
import org.springframework.context.annotation.PropertySource
import org.springframework.test.context.ContextConfiguration

@PropertySource("classpath:application-test.yaml", factory = YamlPropertyLoaderFactory::class)
@ContextConfiguration(classes = [TestConfiguration::class])
open class PaginationServiceTest(
        private val paginationService: PaginationService,
        private val props: PaginationValidationProperties
) : StringSpec() {

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
                    row(PaginationQuery(page = null, pageSize = null), PaginationParams(page = 0, pageSize = props.minPageSize)),
                    row(PaginationQuery(page = 1, pageSize = null), PaginationParams(page = 0, pageSize = props.minPageSize)),
                    row(PaginationQuery(page = 1, pageSize = props.maxPageSize), PaginationParams(page = 0, pageSize = props.maxPageSize))
            ) { query, pagination ->
                paginationService.getPagination(query) shouldBe pagination
            }
        }
    }
}
