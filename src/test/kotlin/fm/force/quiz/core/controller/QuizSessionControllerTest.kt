package fm.force.quiz.core.controller

import fm.force.quiz.common.dto.QuizSessionAnswerPatchDTO
import fm.force.quiz.common.dto.QuizSessionPatchDTO
import fm.force.quiz.core.repository.QuizSessionQuestionRepository
import fm.force.quiz.core.repository.QuizSessionRepository
import fm.force.quiz.factory.TestDataFactory
import fm.force.quiz.util.expectOkOrPrint
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class QuizSessionControllerTest(
    testDataFactory: TestDataFactory,
    private val quizSessionRepository: QuizSessionRepository,
    private val quizSessionQuestionRepository: QuizSessionQuestionRepository
) : AbstractControllerTest() {
    init {
        "CRUD controllers" should {
            "/quizSessions" {
                val quiz = testDataFactory.getQuiz()
                smokeTestCRUD(
                    "/quizSessions",
                    QuizSessionPatchDTO(quiz = quiz.id),
                    // nothing to patch here, it's ignored
                    QuizSessionPatchDTO(quiz = quiz.id)
                )
            }
            "/quizSessions/{id}/cancel" {
                val sessionId = testDataFactory.getQuizSession(owner = user).id
                client
                    .post("/quizSessions/$sessionId/cancel", "")
                    .andDo(expectOkOrPrint)
                    .andExpect(status().is2xxSuccessful)
            }
            "/quizSessions/{id}/complete" {
                val sessionId = testDataFactory.getQuizSession(owner = user).id
                client
                    .post("/quizSessions/$sessionId/complete", "")
                    .andDo(expectOkOrPrint)
                    .andExpect(status().is2xxSuccessful)
            }
            "/quizSessions/{id}/questions" {
                val sessionId = testDataFactory.getQuizSession(owner = user).id
                client
                    .get("/quizSessions/$sessionId/questions")
                    .andDo(expectOkOrPrint)
                    .andExpect(status().is2xxSuccessful)
            }
            "/quizSessions/{id}/doAnswer" {
                val session = quizSessionRepository.refresh(testDataFactory.getQuizSession(owner = user))
                val question = quizSessionQuestionRepository.refresh(session.questions.random())
                val answer = question.answers.random()
                client
                    .post(
                        "/quizSessions/${session.id}/doAnswer",
                        QuizSessionAnswerPatchDTO(
                            question = question.id,
                            answers = setOf(answer.id)
                        )
                    )
                    .andDo(expectOkOrPrint)
                    .andExpect(status().is2xxSuccessful)
            }
        }
    }
}
