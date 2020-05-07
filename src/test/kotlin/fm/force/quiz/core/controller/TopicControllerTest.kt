package fm.force.quiz.core.controller

import fm.force.quiz.common.dto.TopicPatchDTO
import fm.force.quiz.common.getRandomString
import fm.force.quiz.util.expectOkOrPrint
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

class TopicControllerTest : AbstractQuizControllerTest() {
    init {
        "CRUD controllers" should {
            "/topics" {
                smokeTestCRUD("/topics", TopicPatchDTO("sample"), TopicPatchDTO("Patched"))
            }
            "/topics/getOrCreate" {
                val randomString = getRandomString()
                client
                    .post("/topics/getOrCreate", TopicPatchDTO(randomString))
                    .andDo(expectOkOrPrint)
                    .andExpect(MockMvcResultMatchers.status().isCreated)

                client
                    .post("/topics/getOrCreate", TopicPatchDTO(randomString))
                    .andDo(expectOkOrPrint)
                    .andExpect(MockMvcResultMatchers.status().isOk)
            }
        }
    }
}
