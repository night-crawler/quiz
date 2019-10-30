package fm.force.quiz.security.controller

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import fm.force.quiz.security.dto.RegisterUserRequestDTO
import io.kotlintest.specs.WordSpec
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
class RegisterControllerTest(
        private val mockMvc: MockMvc
) : WordSpec() {

    init {
        "POST /register" should {
            "register a new user" {
                val req = RegisterUserRequestDTO("user@example.com", "sample")
                val mapper = jacksonObjectMapper()
                val res = mapper.writeValueAsString(req)

                mockMvc.perform(
                        post("/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(res))
                        .andExpect(status().isOk)
                        .andDo(print())
            }
        }
    }
}
