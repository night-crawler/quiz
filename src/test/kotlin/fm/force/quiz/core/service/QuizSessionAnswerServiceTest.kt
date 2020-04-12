package fm.force.quiz.core.service

import fm.force.quiz.common.dto.QuizSessionAnswerPatchDTO
import fm.force.quiz.core.exception.ValidationError
import fm.force.quiz.core.repository.QuizSessionQuestionRepository
import fm.force.quiz.core.repository.QuizSessionRepository
import io.kotlintest.data.forall
import io.kotlintest.shouldThrow
import io.kotlintest.tables.row

open class QuizSessionAnswerServiceTest(
    private val quizSessionQuestionRepository: QuizSessionQuestionRepository,
    private val quizSessionRepository: QuizSessionRepository,
    service: QuizSessionAnswerService
) : AbstractCRUDServiceTest() {

    init {
        "should validate" {
            val session = quizSessionRepository.refresh(
                testDataFactory.getQuizSession(
                    owner = user,
                    isCompleted = true,
                    isCancelled = true
                )
            )
            val quizSessionQuestion = session.questions.random()
            forall(
                row(QuizSessionAnswerPatchDTO(1, 1, setOf(1))),
                row(QuizSessionAnswerPatchDTO(quizSessionQuestion.quizSession.id, 1, setOf(1))),
                row(QuizSessionAnswerPatchDTO(quizSessionQuestion.quizSession.id, quizSessionQuestion.id, setOf(1)))
            ) {
                shouldThrow<ValidationError> { service.create(it) }
            }
        }

        "should create" {
            val session = quizSessionRepository.refresh(testDataFactory.getQuizSession(owner = user))
            val quizSessionQuestion = quizSessionQuestionRepository.refresh(session.questions.random())
            val dto = QuizSessionAnswerPatchDTO(
                quizSessionQuestion.quizSession.id,
                quizSessionQuestion.id,
                setOf(quizSessionQuestion.answers.random().id)
            )
            service.create(dto)
        }

        "should not be able to answer to a question in a foreign session" {
            val session = quizSessionRepository.refresh(testDataFactory.getQuizSession(owner = alien))
            val quizSessionQuestion = quizSessionQuestionRepository.refresh(session.questions.random())

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
