package fm.force.quiz.security.controller

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import fm.force.quiz.TestConfiguration
import fm.force.quiz.YamlPropertyLoaderFactory
import fm.force.quiz.security.configuration.properties.PasswordConfigurationProperties
import fm.force.quiz.security.dto.LoginRequestDTO
import fm.force.quiz.security.dto.RegisterRequestDTO
import io.kotlintest.specs.WordSpec
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.PropertySource
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@ContextConfiguration(classes = [TestConfiguration::class])
@PropertySource("classpath:application-test.yaml", factory = YamlPropertyLoaderFactory::class)
@ActiveProfiles("test")
@AutoConfigureMockMvc
class AuthControllersTest(
    private val mockMvc: MockMvc,
    private val passwordConfigurationProperties: PasswordConfigurationProperties
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

        "POST /auth/login" should {
            "fail because profile has not been activated" {
                passwordConfigurationProperties.userIsEnabledAfterCreation = false
                val failCreds = LoginRequestDTO("user-fail@example.com", "samplesample")
                performPost("/auth/register", failCreds)
                    .andExpect(status().isCreated)
                    .andDo(print())

                performPost("/auth/login", failCreds)
                    .andExpect(status().isForbidden)
                    .andDo(print())
            }

            "successfully login" {
                val successCreds = LoginRequestDTO("user-success@example.com", "samplesample")
                // FIXME: it's a cheat to test it like this
                passwordConfigurationProperties.userIsEnabledAfterCreation = true
                performPost("/auth/register", successCreds)
                    .andExpect(status().isCreated)

                performPost("/auth/login", successCreds)
                    .andExpect(status().isOk)
                    .andDo(print())
            }
        }
    }
}
