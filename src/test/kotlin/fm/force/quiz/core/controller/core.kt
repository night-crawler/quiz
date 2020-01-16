package fm.force.quiz.core.controller

import fm.force.quiz.core.dto.AnswerPatchDTO
import fm.force.quiz.core.dto.DTOMarker
import fm.force.quiz.core.dto.DifficultyScalePatchDTO
import fm.force.quiz.core.dto.DifficultyScaleRangePatchDTO
import fm.force.quiz.core.dto.QuestionPatchDTO
import fm.force.quiz.core.dto.QuizPatchDTO
import fm.force.quiz.core.dto.QuizQuestionPatchDTO
import fm.force.quiz.core.dto.QuizSessionPatchDTO
import fm.force.quiz.core.dto.TagPatchDTO
import fm.force.quiz.core.dto.TopicPatchDTO
import fm.force.quiz.core.repository.DifficultyScaleRangeRepository
import fm.force.quiz.factory.TestDataFactory
import fm.force.quiz.util.entityId
import fm.force.quiz.util.expectOkOrPrint
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class CoreControllersTest(
    testDataFactory: TestDataFactory,
    val difficultyScaleRangeRepository: DifficultyScaleRangeRepository
) : AbstractControllerTest() {
    init {
        "CRUD controllers" should {
            "/tags" {
                smokeTestCRUD("/tags", TagPatchDTO("sample"), TagPatchDTO("Patched"))
            }
            "/topics" {
                smokeTestCRUD("/topics", TopicPatchDTO("sample"), TopicPatchDTO("Patched"))
            }
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
                println(difficultyScaleRangeRepository.findAll())
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
            "/quizSessions/cancel" {
                val quizId = testDataFactory.getQuizSession(owner = user).id
                client.post("/quizSessions/$quizId/cancel", "")
            }
            "/quizSessions/complete" {
                val quizId = testDataFactory.getQuizSession(owner = user).id
                client.post("/quizSessions/$quizId/complete", "")
            }
        }
    }

    fun smokeTestCRUD(path: String, create: DTOMarker, patch: DTOMarker) {
        val id = client.post(path, create)
            .andDo(expectOkOrPrint)
            .andExpect(status().isCreated)
            .andReturn().response.entityId
        client
            .get(path)
            .andDo(expectOkOrPrint)
            .andExpect(status().is2xxSuccessful)
        client
            .patch("$path/$id", patch)
            .andDo(expectOkOrPrint)
        client
            .delete("$path/$id")
            .andDo(expectOkOrPrint)
    }
}
