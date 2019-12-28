package io.kotlintest.provided.fm.force.quiz.core.service

import fm.force.quiz.common.getRandomString
import fm.force.quiz.configuration.properties.DifficultyScaleValidationProperties
import fm.force.quiz.core.dto.PaginationQuery
import fm.force.quiz.core.dto.PatchDifficultyScaleDTO
import fm.force.quiz.core.dto.SortQuery
import fm.force.quiz.core.exception.ValidationError
import fm.force.quiz.core.service.DifficultyScaleService
import fm.force.quiz.factory.TestDataFactory
import fm.force.quiz.security.service.JwtUserDetailsFactoryService
import io.kotlintest.data.forall
import io.kotlintest.matchers.collections.shouldHaveSize
import io.kotlintest.matchers.numerics.shouldBeGreaterThan
import io.kotlintest.provided.fm.force.quiz.TestConfiguration
import io.kotlintest.provided.fm.force.quiz.YamlPropertyLoaderFactory
import io.kotlintest.shouldBe
import io.kotlintest.shouldNotBe
import io.kotlintest.shouldThrow
import io.kotlintest.tables.row
import org.springframework.context.annotation.PropertySource
import org.springframework.test.context.ContextConfiguration

@PropertySource("classpath:application-test.yaml", factory = YamlPropertyLoaderFactory::class)
@ContextConfiguration(classes = [TestConfiguration::class])
open class DifficultyScaleServiceTest(
        testDataFactory: TestDataFactory,
        jwtUserDetailsFactoryService: JwtUserDetailsFactoryService,
        difficultyScaleService: DifficultyScaleService,
        validationProps: DifficultyScaleValidationProperties
) : GenericCRUDServiceTest(
        testDataFactory = testDataFactory,
        jwtUserDetailsFactoryService = jwtUserDetailsFactoryService
) {

    init {
        "should validate" {
            forall(
                    row(PatchDifficultyScaleDTO()),
                    row(PatchDifficultyScaleDTO(name = getRandomString(validationProps.minNameLength - 1))),
                    row(PatchDifficultyScaleDTO(max = 0)),
                    row(PatchDifficultyScaleDTO(max = validationProps.allowedMax, name = ""))
            ) {
                shouldThrow<ValidationError> { difficultyScaleService.create(it) }
            }

            val createDTO = PatchDifficultyScaleDTO(
                    name = getRandomString(validationProps.maxNameLength),
                    max = validationProps.allowedMax
            )

            difficultyScaleService.validateCreate(createDTO)
        }

        "should paginate" {
            (1..5).map { testDataFactory.getDifficultyScale(owner = user, name = "diff scale - $it") }
            (1..5).map { testDataFactory.getDifficultyScale(owner = alien, name = "diff scale - $it") }

            val page = difficultyScaleService.find(
                    PaginationQuery.default(),
                    SortQuery.byIdDesc(),
                    "SCALE"
            )
            page.content shouldHaveSize 5
        }

        "should create" {
            val createDTO = PatchDifficultyScaleDTO(
                    name = getRandomString(validationProps.maxNameLength),
                    max = validationProps.allowedMax
            )

            val entity = difficultyScaleService.create(createDTO)
            entity.id shouldBeGreaterThan 0
        }

        "should patch" {
            val scale = testDataFactory.getDifficultyScale(owner = user)
            val patch = PatchDifficultyScaleDTO(
                    name = getRandomString(validationProps.maxNameLength),
                    max = validationProps.allowedMax
            )
            val entity = difficultyScaleService.patch(scale.id, patch)
            entity.max shouldBe patch.max
            entity.name shouldBe patch.name
            entity.updatedAt shouldNotBe scale.updatedAt
        }
    }

}
