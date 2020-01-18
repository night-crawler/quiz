package fm.force.quiz.core.service

import fm.force.quiz.core.dto.PaginationQuery
import fm.force.quiz.core.dto.QuizSessionPatchDTO
import fm.force.quiz.core.dto.QuizSessionSearchDTO
import fm.force.quiz.core.dto.SortQuery
import fm.force.quiz.core.exception.NotFoundException
import fm.force.quiz.core.exception.ValidationError
import io.kotlintest.data.forall
import io.kotlintest.matchers.collections.shouldHaveSize
import io.kotlintest.shouldBe
import io.kotlintest.shouldNotBe
import io.kotlintest.shouldThrow
import io.kotlintest.tables.row
import java.time.Duration
import java.time.Instant

open class QuizSessionServiceTest(service: QuizSessionService) : AbstractCRUDServiceTest() {

    init {
        "should validate create" {
            forall(
                row(QuizSessionPatchDTO(quiz = 0))
            ) {
                shouldThrow<ValidationError> { service.create(it) }
            }
        }

        "should validate transitions" {
            val invalidStateQuizSession = testDataFactory.getQuizSession(owner = user, isCancelled = true, isCompleted = true)
            shouldThrow<ValidationError> { service.cancel(invalidStateQuizSession.id) }
            shouldThrow<ValidationError> { service.complete(invalidStateQuizSession.id) }

            val invalidValidDate = testDataFactory.getQuizSession(owner = user, validTill = Instant.now() - Duration.ofDays(2))
            shouldThrow<ValidationError> { service.complete(invalidValidDate.id) }

            testDataFactory.getQuizSession(owner = alien).id.let {
                shouldThrow<NotFoundException> {
                    service.complete(it)
                }
            }
        }

        "should cancel and complete" {
            var quizSession = testDataFactory.getQuizSession(owner = user).let { service.complete(it.id) }
            quizSession.isCompleted shouldBe true
            quizSession.completedAt shouldNotBe null

            quizSession = testDataFactory.getQuizSession(owner = user).let { service.cancel(it.id) }
            quizSession.isCancelled shouldBe true
            quizSession.cancelledAt shouldNotBe null
        }

        "should create a session for a not owned quiz" {
            val quiz = testDataFactory.getQuiz(owner = alien)
            service.create(QuizSessionPatchDTO(quiz = quiz.id))
        }

        "should find" {
            val sessions = (1..5).map { testDataFactory.getQuizSession(owner = user) }
            val quizzes = sessions.map { it.quiz }
            val scales = sessions.mapNotNull { it.difficultyScale }

            service.cancel(sessions[0].id)
            service.complete(sessions[1].id)

            forall(
                row(QuizSessionSearchDTO(isCancelled = true)),
                row(QuizSessionSearchDTO(isCompleted = true)),
                row(QuizSessionSearchDTO(quiz = quizzes.random().id)),
                row(QuizSessionSearchDTO(difficultyScale = scales.random().id))
            ) {
                service.find(PaginationQuery.default(), SortQuery.byIdDesc(), it).content shouldHaveSize 1
            }
        }
    }
}
