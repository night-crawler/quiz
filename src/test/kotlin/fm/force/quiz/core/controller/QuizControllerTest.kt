package fm.force.quiz.core.controller

import fm.force.quiz.common.dto.QuizImportDTO
import fm.force.quiz.common.dto.QuizImportType
import fm.force.quiz.common.dto.QuizPatchDTO
import fm.force.quiz.common.dto.QuizQuestionPatchDTO
import fm.force.quiz.factory.TestDataFactory
import fm.force.quiz.util.expectOkOrPrint
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

class QuizControllerTest(testDataFactory: TestDataFactory) : AbstractQuizControllerTest() {
    private val importSample = javaClass.classLoader.getResource("sample-quiz.yaml")!!.readText()

    init {
        "CRUD controllers" should {
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
                    .andExpect(MockMvcResultMatchers.status().is2xxSuccessful)
            }
            "/quizzes/{anon}/startSession" {
                val quizId = testDataFactory.getQuiz().id
                client
                    .post("/quizzes/$quizId/startSession", "")
                    .andDo(expectOkOrPrint)
                    .andExpect(MockMvcResultMatchers.status().isCreated)
            }

            "/quizzes/import" {
                val dto = QuizImportDTO(importSample, QuizImportType.YAML)
                client
                    .post("/quizzes/import", dto)
                    .andDo(expectOkOrPrint)
                    .andExpect(MockMvcResultMatchers.status().isCreated)
            }
        }
    }
}
