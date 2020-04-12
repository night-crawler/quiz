package fm.force.quiz.core.controller

import fm.force.quiz.common.dto.TagPatchDTO
import fm.force.quiz.common.getRandomString
import fm.force.quiz.util.expectOkOrPrint
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

class TagsControllerTest : AbstractControllerTest() {
    init {
        "CRUD controllers" should {
            "/tags" {
                smokeTestCRUD("/tags", TagPatchDTO("sample"), TagPatchDTO("Patched"))
            }
            "/tags/getOrCreate" {
                val randomString = getRandomString()
                client
                    .post("/tags/getOrCreate", TagPatchDTO(randomString))
                    .andDo(expectOkOrPrint)
                    .andExpect(MockMvcResultMatchers.status().isCreated)

                client
                    .post("/tags/getOrCreate", TagPatchDTO(randomString))
                    .andDo(expectOkOrPrint)
                    .andExpect(MockMvcResultMatchers.status().isOk)
            }
        }
    }
}
