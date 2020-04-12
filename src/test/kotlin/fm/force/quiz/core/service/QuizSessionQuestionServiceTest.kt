package fm.force.quiz.core.service

import fm.force.quiz.common.dto.PaginationQuery
import fm.force.quiz.common.dto.QuizSessionQuestionSearchDTO
import fm.force.quiz.common.dto.SortQuery
import fm.force.quiz.core.repository.QuizSessionQuestionRepository
import io.kotlintest.data.forall
import io.kotlintest.matchers.collections.shouldHaveAtLeastSize
import io.kotlintest.matchers.collections.shouldHaveSize
import io.kotlintest.tables.row

open class QuizSessionQuestionServiceTest(
    service: QuizSessionQuestionService,
    quizSessionQuestionRepository: QuizSessionQuestionRepository
) : AbstractCRUDServiceTest() {

    init {
        "should find" {
            val question = quizSessionQuestionRepository.refresh(
                testDataFactory.getQuizSessionQuestion(owner = user, text = "SAMPLE QSQ #1")
            )
            question.answers shouldHaveAtLeastSize 1

            testDataFactory.getQuizSessionQuestion(owner = alien, text = "never find me")

            forall(
                row(null),
                row(QuizSessionQuestionSearchDTO()),
                row(QuizSessionQuestionSearchDTO(quizSession = question.quizSession.id)),
                row(QuizSessionQuestionSearchDTO(originalQuestion = question.originalQuestion?.id)),
                row(QuizSessionQuestionSearchDTO(text = "qsq"))
            ) {
                val page = service.find(
                    PaginationQuery.default(),
                    SortQuery.byIdDesc(),
                    it
                )
                page.content shouldHaveAtLeastSize 1
            }

            val page = service.find(
                PaginationQuery.default(),
                SortQuery.byIdDesc(),
                QuizSessionQuestionSearchDTO(text = "never find")
            )
            page.content shouldHaveSize 0
        }
    }
}
