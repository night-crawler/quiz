package fm.force.quiz.core.controller

import fm.force.quiz.AbstractSprintBootControllerTest
import fm.force.quiz.common.dto.DTOMarker
import fm.force.quiz.common.getRandomString
import fm.force.quiz.security.dto.LoginRequestDTO
import fm.force.quiz.security.dto.RegisterRequestDTO
import fm.force.quiz.security.entity.User
import fm.force.quiz.security.service.AuthService
import fm.force.quiz.util.entityId
import fm.force.quiz.util.expectOkOrPrint
import io.kotlintest.TestCase
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

abstract class AbstractQuizControllerTest : AbstractSprintBootControllerTest() {
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

    fun smokeTestCRUD(path: String, create: DTOMarker, patch: DTOMarker) {
        val id = client.post(path, create)
            .andDo(expectOkOrPrint)
            .andExpect(MockMvcResultMatchers.status().isCreated)
            .andReturn().response.entityId
        client
            .get(path)
            .andDo(expectOkOrPrint)
            .andExpect(MockMvcResultMatchers.status().is2xxSuccessful)
        client
            .patch("$path/$id", patch)
            .andDo(expectOkOrPrint)
        client
            .delete("$path/$id")
            .andDo(expectOkOrPrint)
    }
}
