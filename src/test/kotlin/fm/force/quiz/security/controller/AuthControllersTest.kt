package fm.force.quiz.security.controller

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import fm.force.quiz.TestConfiguration
import fm.force.quiz.YamlPropertyLoaderFactory
import fm.force.quiz.security.configuration.properties.JwtConfigurationProperties
import fm.force.quiz.security.configuration.properties.PasswordConfigurationProperties
import fm.force.quiz.security.dto.JwtResponseTokensDTO
import fm.force.quiz.security.dto.LoginRequestDTO
import fm.force.quiz.security.dto.RegisterRequestDTO
import io.kotlintest.specs.WordSpec
import javax.servlet.http.Cookie
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.PropertySource
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.ResultActions
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
    private val passwordConfigurationProperties: PasswordConfigurationProperties,
    private val jwtConfigurationProperties: JwtConfigurationProperties
) : WordSpec() {
    val mapper by lazy { jacksonObjectMapper() }
    fun performPost(uri: String, content: String, cookies: Collection<Cookie> = listOf()): ResultActions {
        val requestBuilder = post(uri)
            .contentType(MediaType.APPLICATION_JSON)
            .content(content)

        if (cookies.isNotEmpty())
            requestBuilder.cookie(*cookies.toTypedArray())

        return mockMvc.perform(requestBuilder)
    }

    fun performPost(uri: String, dto: Any, cookies: Collection<Cookie> = listOf()) =
        this.performPost(uri, mapper.writeValueAsString(dto), cookies)

    init {
        "POST /auth/register" should {
            "register a new user" {
                val data = RegisterRequestDTO("user-sample001@example.com", "validSample")
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
                val failCreds = LoginRequestDTO("user-fail@example.com", "validSample")
                performPost("/auth/register", failCreds)
                    .andExpect(status().isCreated)
                    .andDo(print())

                performPost("/auth/login", failCreds)
                    .andExpect(status().isForbidden)
                    .andDo(print())
            }

            "successfully login" {
                val successCreds = LoginRequestDTO("user-success@example.com", "validSample")
                // FIXME: it's a cheat to test it like this
                passwordConfigurationProperties.userIsEnabledAfterCreation = true
                performPost("/auth/register", successCreds)
                    .andExpect(status().isCreated)

                performPost("/auth/login", successCreds)
                    .andExpect(status().isOk)
                    .andDo(print())
            }
        }

        "POST /auth/refresh" should {
            "issue an access token by a valid refresh token" {
                passwordConfigurationProperties.userIsEnabledAfterCreation = true
                val creds = LoginRequestDTO("refresh-user-sample@example.com", "validSample")
                performPost("/auth/register", creds).andExpect(status().isCreated)

                val responseText = performPost("/auth/login", creds)
                    .andExpect(status().isOk)
                    .andReturn().response.contentAsString
                val tokens = mapper.readValue(responseText, JwtResponseTokensDTO::class.java)

                val cookie = Cookie(
                    jwtConfigurationProperties.refreshTokenCookieName,
                    tokens.refreshToken
                )
                performPost("/auth/refresh", "", listOf(cookie))
                    .andExpect(status().isOk)
                    .andReturn().response.contentAsString
            }
        }
    }
}
