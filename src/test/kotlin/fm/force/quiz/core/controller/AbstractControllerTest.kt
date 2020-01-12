package fm.force.quiz.core.controller

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import fm.force.quiz.TestConfiguration
import fm.force.quiz.YamlPropertyLoaderFactory
import fm.force.quiz.common.getRandomString
import fm.force.quiz.security.dto.LoginRequestDTO
import fm.force.quiz.security.dto.RegisterRequestDTO
import fm.force.quiz.security.entity.User
import fm.force.quiz.security.service.JwtAuthService
import io.kotlintest.TestCase
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

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@ContextConfiguration(classes = [TestConfiguration::class])
@PropertySource("classpath:application-test.yaml", factory = YamlPropertyLoaderFactory::class)
@ActiveProfiles("test")
@AutoConfigureMockMvc
abstract class AbstractControllerTest : WordSpec() {
    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var jwtAuthService: JwtAuthService

    lateinit var user: User
    lateinit var token: String

    override fun beforeTest(testCase: TestCase) {
        val email = "user-${getRandomString()}@example.com"
        val password = getRandomString()
        user = jwtAuthService.register(RegisterRequestDTO(email, password), isActive = true)
        val userJwt = jwtAuthService.authenticate(LoginRequestDTO(email, password))
        token = userJwt.token
    }

    val mapper by lazy { jacksonObjectMapper() }
    fun performPost(uri: String, content: String) =
            mockMvc.perform(
                    MockMvcRequestBuilders.post(uri)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(content)
            )

    fun performPost(uri: String, dto: Any) = this.performPost(uri, mapper.writeValueAsString(dto))

}
