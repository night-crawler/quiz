package io.kotlintest.provided.fm.force.quiz.core.service

import fm.force.quiz.configuration.properties.DifficultyScaleRangeValidationProperties
import fm.force.quiz.core.dto.PaginationQuery
import fm.force.quiz.core.dto.PatchDifficultyScaleRangeDTO
import fm.force.quiz.core.dto.SortQuery
import fm.force.quiz.core.exception.ValidationError
import fm.force.quiz.core.service.DifficultyScaleRangeService
import fm.force.quiz.factory.TestDataFactory
import fm.force.quiz.security.service.JwtUserDetailsFactoryService
import io.kotlintest.data.forall
import io.kotlintest.matchers.collections.shouldHaveSize
import io.kotlintest.provided.fm.force.quiz.TestConfiguration
import io.kotlintest.provided.fm.force.quiz.YamlPropertyLoaderFactory
import io.kotlintest.shouldThrow
import io.kotlintest.tables.row
import org.springframework.context.annotation.PropertySource
import org.springframework.test.context.ContextConfiguration

@PropertySource("classpath:application-test.yaml", factory = YamlPropertyLoaderFactory::class)
@ContextConfiguration(classes = [TestConfiguration::class])
open class DifficultyScaleRangeServiceTest(
        testDataFactory: TestDataFactory,
        jwtUserDetailsFactoryService: JwtUserDetailsFactoryService,
        service: DifficultyScaleRangeService,
        validationProps: DifficultyScaleRangeValidationProperties
) : GenericCRUDServiceTest(
        testDataFactory = testDataFactory,
        jwtUserDetailsFactoryService = jwtUserDetailsFactoryService
) {

    init {
        "should validate when creating a new instance" {
            val range = testDataFactory.getDifficultyScaleRange(owner = user)
            forall(
                    row(PatchDifficultyScaleRangeDTO()),
                    row(PatchDifficultyScaleRangeDTO(difficultyScale = -1)),
                    row(PatchDifficultyScaleRangeDTO(title = "")),
                    row(PatchDifficultyScaleRangeDTO(min = -1)),
                    // swap min & max
                    row(PatchDifficultyScaleRangeDTO(difficultyScale = range.difficultyScale.id, min = validationProps.maxUpper, max = 1, title = "sample sample"))
            ) {
                shouldThrow<ValidationError> { service.create(it) }
            }
        }

        "should validate when patching existent instance" {
            val range = testDataFactory.getDifficultyScaleRange(owner = user, min = 100, max = 1000)
            // difficultyScale must not be changed
            var patch = PatchDifficultyScaleRangeDTO(
                    difficultyScale = range.id, min = 100, max = validationProps.maxUpper, title = "sample sample")
            shouldThrow<ValidationError> { service.patch(range.id, patch) }

            // even if a patch itself is valid, we must check against changed boundaries
            patch = PatchDifficultyScaleRangeDTO(max = 1)
            shouldThrow<ValidationError> { service.patch(range.id, patch) }
        }

        "validation should take into account intersections" {
            val scale = testDataFactory.getDifficultyScale(owner = user, createNRandomRanges = 0)
            testDataFactory.getDifficultyScaleRange(owner = user, difficultyScale = scale, min = 5, max = 10)

            var r = PatchDifficultyScaleRangeDTO(difficultyScale = scale.id, title = "sample", min = 0, max = 5)
            shouldThrow<ValidationError> { service.create(r) }

            r = PatchDifficultyScaleRangeDTO(difficultyScale = scale.id, title = "sample", min = 10, max = 12)
            shouldThrow<ValidationError> { service.create(r) }

            r = PatchDifficultyScaleRangeDTO(difficultyScale = scale.id, title = "sample", min = 13, max = 13)
            service.create(r)
        }

        "should paginate" {
            (1..5).forEach { testDataFactory.getDifficultyScaleRange(owner = user, title = "uniqueTitle $it") }
            (1..5).forEach { testDataFactory.getDifficultyScaleRange(owner = alien, title = "uniqueTitle $it") }

            val page = service.find(
                    PaginationQuery.default(),
                    SortQuery.byIdDesc(),
                    "NIQUE"
            )

            page.content shouldHaveSize 5
        }
    }
}