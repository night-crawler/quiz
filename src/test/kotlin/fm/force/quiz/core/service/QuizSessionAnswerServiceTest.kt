package fm.force.quiz.core.service

import fm.force.quiz.core.dto.QuizSessionAnswerPatchDTO
import fm.force.quiz.core.exception.ValidationError
import fm.force.quiz.core.repository.QuizSessionQuestionRepository
import io.kotlintest.data.forall
import io.kotlintest.shouldThrow
import io.kotlintest.tables.row

open class QuizSessionAnswerServiceTest(
    private val quizSessionQuestionRepository: QuizSessionQuestionRepository,
    service: QuizSessionAnswerService
) : AbstractCRUDServiceTest() {

    init {
        "should validate" {
            val quizSessionQuestion = testDataFactory.getQuizSessionQuestion(owner = user)
            forall(
                row(QuizSessionAnswerPatchDTO(1, 1, setOf(1))),
                row(QuizSessionAnswerPatchDTO(quizSessionQuestion.quizSession.id, 1, setOf(1))),
                row(QuizSessionAnswerPatchDTO(quizSessionQuestion.quizSession.id, quizSessionQuestion.id, setOf(1)))
            ) {
                shouldThrow<ValidationError> { service.create(it) }
            }
        }

        "should create" {
            val quizSessionQuestion = quizSessionQuestionRepository.refresh(
                testDataFactory.getQuizSessionQuestion(owner = user)
            )
            val dto = QuizSessionAnswerPatchDTO(
                quizSessionQuestion.quizSession.id,
                quizSessionQuestion.id,
                setOf(quizSessionQuestion.answers.random().id)
            )
            service.create(dto)
        }

        "should not be able to answer to a question in a foreign session" {
            val quizSessionQuestion = quizSessionQuestionRepository.refresh(
                testDataFactory.getQuizSessionQuestion(owner = alien)
            )

            val patchDTO = QuizSessionAnswerPatchDTO(
                session = quizSessionQuestion.quizSession.id,
                question = quizSessionQuestion.id,
                answers = setOf(quizSessionQuestion.answers.random().id)
            )
            shouldThrow<ValidationError> {
                service.create(patchDTO)
            }
        }
    }
}
