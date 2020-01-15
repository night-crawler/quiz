package fm.force.quiz.core.controller

import fm.force.quiz.core.dto.*
import fm.force.quiz.core.repository.JpaDifficultyScaleRangeRepository
import fm.force.quiz.factory.TestDataFactory
import fm.force.quiz.util.entityId
import fm.force.quiz.util.expectOkOrPrint
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status


class CoreControllersTest(testDataFactory: TestDataFactory, val jpaDifficultyScaleRangeRepository: JpaDifficultyScaleRangeRepository) : AbstractControllerTest() {
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
                println(jpaDifficultyScaleRangeRepository.findAll())
                smokeTestCRUD(
                        "/difficultyScaleRanges",
                        DifficultyScaleRangePatchDTO(
                                difficultyScale = difficultyScaleIds.random(),
                                title = "sample", min = 0, max = 1),
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
