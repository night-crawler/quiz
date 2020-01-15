package fm.force.quiz.core.service

import fm.force.quiz.configuration.properties.DifficultyScaleRangeValidationProperties
import fm.force.quiz.core.dto.DifficultyScaleRangePatchDTO
import fm.force.quiz.core.dto.DifficultyScaleRangeSearchDTO
import fm.force.quiz.core.dto.PaginationQuery
import fm.force.quiz.core.dto.SortQuery
import fm.force.quiz.core.exception.ValidationError
import io.kotlintest.data.forall
import io.kotlintest.matchers.collections.shouldHaveSize
import io.kotlintest.shouldThrow
import io.kotlintest.tables.row

open class DifficultyScaleRangeServiceTest(
        service: DifficultyScaleRangeService,
        validationProps: DifficultyScaleRangeValidationProperties
) : AbstractCRUDServiceTest() {

    init {
        "should validate when creating a new instance" {
            val range = testDataFactory.getDifficultyScaleRange(owner = user)
            forall(
                    row(DifficultyScaleRangePatchDTO()),
                    row(DifficultyScaleRangePatchDTO(difficultyScale = -1)),
                    row(DifficultyScaleRangePatchDTO(title = "")),
                    row(DifficultyScaleRangePatchDTO(min = -1)),
                    // swap min & max
                    row(DifficultyScaleRangePatchDTO(difficultyScale = range.difficultyScale.id, min = validationProps.maxUpper, max = 1, title = "sample sample"))
            ) {
                shouldThrow<ValidationError> { service.create(it) }
            }
        }

        "should validate when patching existent instance" {
            val range = testDataFactory.getDifficultyScaleRange(owner = user, min = 100, max = 1000)
            // difficultyScale must not be changed
            var patch = DifficultyScaleRangePatchDTO(
                    difficultyScale = range.id, min = 100, max = validationProps.maxUpper, title = "sample sample")
            shouldThrow<ValidationError> { service.patch(range.id, patch) }

            // even if a patch itself is valid, we must check against changed boundaries
            patch = DifficultyScaleRangePatchDTO(max = 1)
            shouldThrow<ValidationError> { service.patch(range.id, patch) }
        }

        "validation should take into account intersections" {
            val scale = testDataFactory.getDifficultyScale(owner = user, createNRandomRanges = 0)
            testDataFactory.getDifficultyScaleRange(owner = user, difficultyScale = scale, min = 5, max = 10)

            var r = DifficultyScaleRangePatchDTO(difficultyScale = scale.id, title = "sample", min = 0, max = 5)
            shouldThrow<ValidationError> { service.create(r) }

            r = DifficultyScaleRangePatchDTO(difficultyScale = scale.id, title = "sample", min = 10, max = 12)
            shouldThrow<ValidationError> { service.create(r) }

            r = DifficultyScaleRangePatchDTO(difficultyScale = scale.id, title = "sample", min = 13, max = 13)
            service.create(r)
        }

        "should paginate" {
            (1..5).forEach { testDataFactory.getDifficultyScaleRange(owner = user, title = "uniqueTitle $it") }
            (1..5).forEach { testDataFactory.getDifficultyScaleRange(owner = alien, title = "uniqueTitle $it") }

            val page = service.find(
                    PaginationQuery.default(),
                    SortQuery.byIdDesc(),
                    DifficultyScaleRangeSearchDTO(title = "NIQUE")
            )

            page.content shouldHaveSize 5
        }
    }
}