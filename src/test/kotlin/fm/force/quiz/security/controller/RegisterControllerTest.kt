package fm.force.quiz.security.controller

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import fm.force.quiz.security.dto.RegisterRequestDTO
import fm.force.quiz.YamlPropertyLoaderFactory
import io.kotlintest.specs.WordSpec
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.PropertySource
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@PropertySource("classpath:application-test.yaml", factory = YamlPropertyLoaderFactory::class)
@ActiveProfiles("test")
@AutoConfigureMockMvc
class RegisterControllerTest(
        private val mockMvc: MockMvc
) : WordSpec() {
    val mapper by lazy { jacksonObjectMapper() }
    fun performPost(uri: String, content: String) =
            mockMvc.perform(
                    post(uri)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(content)
            )

    fun performPost(uri: String, dto: Any) = this.performPost(uri, mapper.writeValueAsString(dto))

    init {
        "POST /auth/register" should {
            "register a new user" {
                val data = RegisterRequestDTO("user-sample001@example.com", "samplesample")
                performPost("/auth/register", data)
                        .andExpect(status().isCreated)
                        .andDo(print())

                performPost("/auth/register", data)
                        .andExpect(status().isConflict)
                        .andDo(print())
            }

            "ensure validation is working" {
                performPost("/auth/register", """{"email": "", "password": ""}""")
                        .andExpect(status().isBadRequest)
                        .andDo(print())
            }
        }
    }
}
