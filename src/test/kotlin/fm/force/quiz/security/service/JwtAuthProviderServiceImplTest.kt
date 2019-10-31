package io.kotlintest.provided.fm.force.quiz.security.service

import fm.force.quiz.security.service.JwtAuthProviderService
import fm.force.quiz.security.service.JwtProviderService
import fm.force.quiz.security.service.JwtUserDetailsFactoryServiceImpl
import io.kotlintest.provided.fm.force.quiz.security.SecurityTestConfiguration
import io.kotlintest.specs.WordSpec
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.test.context.ContextConfiguration

@ContextConfiguration(classes = [SecurityTestConfiguration::class])
open class JwtAuthProviderServiceImplTest(
        private val jwtAuthProviderService: JwtAuthProviderService,
        private val jwtProviderService: JwtProviderService
) : WordSpec() {
    private val jwtUserDetailsFactory = JwtUserDetailsFactoryServiceImpl()

    init {
        "JwtAuthProviderService" should {
            "check stuff" {
                val details = jwtUserDetailsFactory.createUserDetails().apply {
                    username = "example"
                }
                val token = jwtProviderService.issue(details)

                val request = MockHttpServletRequest()
                request.addHeader("Authorization", "Bearer $token")

                jwtAuthProviderService.authorizeRequest(request)
            }
        }
    }
}
