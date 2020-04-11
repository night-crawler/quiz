package fm.force.quiz.core.controller

import fm.force.quiz.core.dto.AnswerPatchDTO
import fm.force.quiz.core.dto.DifficultyScalePatchDTO
import fm.force.quiz.core.dto.DifficultyScaleRangePatchDTO
import fm.force.quiz.core.dto.QuestionPatchDTO
import fm.force.quiz.core.dto.QuizPatchDTO
import fm.force.quiz.core.dto.QuizQuestionPatchDTO
import fm.force.quiz.core.dto.QuizSessionAnswerPatchDTO
import fm.force.quiz.core.dto.QuizSessionPatchDTO
import fm.force.quiz.core.repository.QuizSessionQuestionRepository
import fm.force.quiz.core.repository.QuizSessionRepository
import fm.force.quiz.factory.TestDataFactory
import fm.force.quiz.util.expectOkOrPrint
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class CoreControllersTest(
    testDataFactory: TestDataFactory,
    private val quizSessionRepository: QuizSessionRepository,
    private val quizSessionQuestionRepository: QuizSessionQuestionRepository
) : AbstractControllerTest() {
    init {
        "CRUD controllers" should {
            "/answers" {
                smokeTestCRUD("/answers", AnswerPatchDTO("sample"), AnswerPatchDTO("Patched"))
            }
            "/questions" {
                val answerIds = (1..5).map { testDataFactory.getAnswer(owner = user) }.map { it.id }.toMutableSet()
                smokeTestCRUD(
                    "/questions",
                    QuestionPatchDTO("sample", answers = answerIds, correctAnswers = answerIds),
                    QuestionPatchDTO("Patched")
                )
            }
            "/difficultyScales" {
                smokeTestCRUD(
                    "/difficultyScales",
                    DifficultyScalePatchDTO("sample", max = 20),
                    DifficultyScalePatchDTO("Patched")
                )
            }
            "/difficultyScaleRanges" {
                val difficultyScaleIds = (1..5).map {
                    testDataFactory.getDifficultyScale(owner = user, createNRandomRanges = 0)
                }.map { it.id }.toMutableSet()
                smokeTestCRUD(
                    "/difficultyScaleRanges",
                    DifficultyScaleRangePatchDTO(
                        difficultyScale = difficultyScaleIds.random(),
                        title = "sample", min = 0, max = 1
                    ),
                    DifficultyScaleRangePatchDTO(title = "Patched")
                )
            }
            "/quizzes" {
                smokeTestCRUD(
                    "/quizzes",
                    QuizPatchDTO("sample"),
                    QuizPatchDTO("changed")
                )
            }
            "/quizzes/questions" {
                val quiz = testDataFactory.getQuiz(owner = user)
                val question = testDataFactory.getQuestion(owner = user)
                smokeTestCRUD(
                    "/quizzes/${quiz.id}/quizQuestions",
                    QuizQuestionPatchDTO(question = question.id, seq = -1),
                    QuizQuestionPatchDTO(seq = -1)
                )
            }
            "/quizzes/{anon}/view" {
                val quizId = testDataFactory.getQuiz().id
                client
                    .get("/quizzes/$quizId/view")
                    .andDo(expectOkOrPrint)
                    .andExpect(status().is2xxSuccessful)
            }
            "/quizzes/{anon}/startSession" {
                val quizId = testDataFactory.getQuiz().id
                client
                    .post("/quizzes/$quizId/startSession", "")
                    .andDo(expectOkOrPrint)
                    .andExpect(status().isCreated)
            }
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
