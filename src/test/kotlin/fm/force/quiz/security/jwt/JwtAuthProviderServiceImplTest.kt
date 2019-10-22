package fm.force.quiz.security.jwt

import io.kotlintest.provided.fm.force.quiz.security.jwt.JwtConfiguration
import io.kotlintest.specs.WordSpec
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.test.context.ContextConfiguration

@ContextConfiguration(classes = [JwtConfiguration::class])
open class JwtAuthProviderServiceImplTest(
        private val jwtAuthProviderService: JwtAuthProviderService,
        private val jwtProvider: JwtProvider
) : WordSpec() {
    private val jwtUserDetailsFactory = JwtUserDetailsFactoryImpl()

    init {
        "JwtAuthProviderService" should {
            "check stuff" {
                val details = jwtUserDetailsFactory.createUserDetails().apply {
                    username = "example"
                }
                val token = jwtProvider.issue(details)

                val request = MockHttpServletRequest()
                request.addHeader("Authorization", "Bearer $token")

                jwtAuthProviderService.authorizeRequest(request)
            }
        }
    }
}

