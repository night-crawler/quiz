package fm.force.quiz.core.service

import fm.force.quiz.common.getRandomString
import fm.force.quiz.configuration.properties.AnswerValidationProperties
import fm.force.quiz.core.dto.AnswerPatchDTO
import fm.force.quiz.core.dto.PaginationQuery
import fm.force.quiz.core.dto.SearchQueryDTO
import fm.force.quiz.core.dto.SortQuery
import fm.force.quiz.core.entity.Answer
import fm.force.quiz.core.exception.ValidationError
import io.kotlintest.data.forall
import io.kotlintest.matchers.collections.shouldHaveSize
import io.kotlintest.shouldThrow
import io.kotlintest.tables.row

open class AnswerServiceTest(
    private val answerService: AnswerService,
    validationProps: AnswerValidationProperties
) : AbstractCRUDServiceTest() {

    init {
        "should validate" {
            forall(
                row(Answer(text = getRandomString(validationProps.minAnswerLength - 1), owner = user)),
                row(Answer(text = getRandomString(validationProps.maxAnswerLength + 1), owner = user))
            ) {
                shouldThrow<ValidationError> { answerService.validateEntity(it) }
            }
        }

        "should create an answer from dto" {
            answerService.create(AnswerPatchDTO(getRandomString(validationProps.minAnswerLength)))
        }

        "should return a paginated response" {
            (1..5).map { testDataFactory.getAnswer(owner = user, text = "some answer $it") }
            (1..5).map { testDataFactory.getAnswer(owner = alien, text = "never get this $it answer") }

            var answerPage = answerService.find(PaginationQuery.default(), SortQuery.byIdDesc(), SearchQueryDTO("answer"))
            answerPage.content shouldHaveSize 5
            testDataFactory.removeAllAnswers()

            (1..5).map { testDataFactory.getAnswer(owner = alien, text = "never get this $it answer") }
            answerPage = answerService.find(PaginationQuery.default(), SortQuery.byIdDesc(), null)
            answerPage.content shouldHaveSize 0
        }
    }
}
