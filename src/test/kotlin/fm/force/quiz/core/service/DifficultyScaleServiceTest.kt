package fm.force.quiz.core.service

import fm.force.quiz.common.getRandomString
import fm.force.quiz.configuration.properties.DifficultyScaleValidationProperties
import fm.force.quiz.core.dto.PaginationQuery
import fm.force.quiz.core.dto.DifficultyScalePatchDTO
import fm.force.quiz.core.dto.SortQuery
import fm.force.quiz.core.exception.ValidationError
import fm.force.quiz.factory.TestDataFactory
import fm.force.quiz.security.service.JwtUserDetailsFactoryService
import io.kotlintest.data.forall
import io.kotlintest.matchers.collections.shouldHaveSize
import io.kotlintest.matchers.numerics.shouldBeGreaterThan
import io.kotlintest.shouldBe
import io.kotlintest.shouldNotBe
import io.kotlintest.shouldThrow
import io.kotlintest.tables.row

open class DifficultyScaleServiceTest(
        difficultyScaleService: DifficultyScaleService,
        validationProps: DifficultyScaleValidationProperties
) : GenericCRUDServiceTest() {

    init {
        "should validate" {
            forall(
                    row(DifficultyScalePatchDTO()),
                    row(DifficultyScalePatchDTO(name = getRandomString(validationProps.minNameLength - 1))),
                    row(DifficultyScalePatchDTO(max = 0)),
                    row(DifficultyScalePatchDTO(max = validationProps.allowedMax, name = ""))
            ) {
                shouldThrow<ValidationError> { difficultyScaleService.create(it) }
            }

            val createDTO = DifficultyScalePatchDTO(
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
            val createDTO = DifficultyScalePatchDTO(
                    name = getRandomString(validationProps.maxNameLength),
                    max = validationProps.allowedMax
            )

            val entity = difficultyScaleService.create(createDTO)
            entity.id shouldBeGreaterThan 0
        }

        "should patch" {
            val scale = testDataFactory.getDifficultyScale(owner = user)
            val patch = DifficultyScalePatchDTO(
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
