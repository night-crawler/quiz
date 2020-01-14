package fm.force.quiz.core.controller

import fm.force.quiz.core.dto.AnswerPatchDTO
import fm.force.quiz.core.dto.QuestionPatchDTO
import fm.force.quiz.factory.TestDataFactory
import fm.force.quiz.util.expectOkOrPrint
import fm.force.quiz.util.entityId
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status


class CoreControllersTest(testDataFactory: TestDataFactory) : AbstractControllerTest() {
    init {
        "CRUD controllers" should {
            "smoke" {
                val answerIds = (1..5).map { testDataFactory.getAnswer(owner = user) }.map { it.id }.toMutableSet()

                listOf(
                        Triple("/answers", AnswerPatchDTO("sample"), AnswerPatchDTO("Patched")),
                        Triple("/questions",
                                QuestionPatchDTO("sample", answers = answerIds, correctAnswers = answerIds),
                                QuestionPatchDTO("Patched"))
                ).forEach { (path, create, patch) ->
                    val id = client.post(path, create)
                            .andDo(expectOkOrPrint)
                            .andExpect(status().isCreated)
                            .andReturn().response.entityId
                    client
                            .get(path)
                            .andDo(expectOkOrPrint)
                            .andExpect(status().is2xxSuccessful)
                            .andDo(print())
                    client
                            .patch("$path/$id", patch)
                            .andDo(expectOkOrPrint)
                    client
                            .delete("$path/$id")
                            .andDo(expectOkOrPrint)
                }
            }
        }
    }
}
