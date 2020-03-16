package fm.force.quiz.core.controller

import fm.force.quiz.TestConfiguration
import fm.force.quiz.YamlPropertyLoaderFactory
import fm.force.quiz.common.getRandomString
import fm.force.quiz.security.dto.LoginRequestDTO
import fm.force.quiz.security.dto.RegisterRequestDTO
import fm.force.quiz.security.entity.User
import fm.force.quiz.security.service.AuthService
import io.kotlintest.TestCase
import io.kotlintest.provided.fm.force.quiz.util.JMapper
import io.kotlintest.specs.WordSpec
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.PropertySource
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders

class AuthenticatedTestClient(private val mockMvc: MockMvc, private val token: String) {
    private fun post(uri: String, content: String) =
        mockMvc.perform(
            MockMvcRequestBuilders.post(uri)
                .header("authorization", "Bearer $token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content)
        )

    private fun patch(uri: String, content: String) =
        mockMvc.perform(
            MockMvcRequestBuilders.patch(uri)
                .header("authorization", "Bearer $token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content)
        )

    fun get(uri: String) =
        mockMvc.perform(
            MockMvcRequestBuilders.get(uri)
                .header("authorization", "Bearer $token")
                .contentType(MediaType.APPLICATION_JSON)
        )

    fun delete(uri: String) =
        mockMvc.perform(
            MockMvcRequestBuilders.delete(uri)
                .header("authorization", "Bearer $token")
        )

    fun post(uri: String, dto: Any) = this.post(uri, JMapper.writeValueAsString(dto))
    fun patch(uri: String, dto: Any) = this.patch(uri, JMapper.writeValueAsString(dto))
}

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@ContextConfiguration(classes = [TestConfiguration::class])
@PropertySource("classpath:application-test.yaml", factory = YamlPropertyLoaderFactory::class)
@ActiveProfiles("test")
@AutoConfigureMockMvc
abstract class AbstractControllerTest : WordSpec() {
    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var authService: AuthService

    lateinit var user: User
    lateinit var client: AuthenticatedTestClient

    override fun beforeTest(testCase: TestCase) {
        val email = "user-${getRandomString()}@example.com"
        val password = getRandomString()
        user = authService.register(RegisterRequestDTO(email, password), isActive = true)
        val userJwt = authService.login(LoginRequestDTO(email, password))
        client = AuthenticatedTestClient(mockMvc, userJwt.accessToken)
    }
}
