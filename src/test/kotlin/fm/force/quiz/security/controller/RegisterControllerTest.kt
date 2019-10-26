package fm.force.quiz.security.controller

import io.kotlintest.specs.WordSpec
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
class RegisterControllerTest(
        private val mockMvc: MockMvc
) : WordSpec() {

    init {
        "POST /register" should {
            "register a new user" {
                mockMvc.perform(
                        post("/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                )
            }
        }
    }
}
