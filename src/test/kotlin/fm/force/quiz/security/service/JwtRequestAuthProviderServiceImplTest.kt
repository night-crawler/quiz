package fm.force.quiz.security.service

import fm.force.quiz.TestConfiguration
import io.kotlintest.specs.WordSpec
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.test.context.ContextConfiguration

@ContextConfiguration(classes = [TestConfiguration::class])
open class JwtRequestAuthProviderServiceImplTest(
    private val jwtRequestAuthProviderService: JwtRequestAuthProviderService,
    private val jwtProviderService: JwtProviderService
) : WordSpec() {
    private val jwtUserDetailsFactory = JwtUserDetailsMapper()

    init {
        "JwtRequestAuthProviderService" should {
            "check stuff" {
                val details = jwtUserDetailsFactory.of().apply {
                    username = "example"
                }
                val token = jwtProviderService.issueAccessToken(details)

                val request = MockHttpServletRequest()
                request.addHeader("Authorization", "Bearer $token")

                jwtRequestAuthProviderService.authenticateRequest(request)
            }
        }
    }
}
