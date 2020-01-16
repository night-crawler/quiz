package fm.force.quiz.security.service

import fm.force.quiz.TestConfiguration
import fm.force.quiz.common.getRandomString
import fm.force.quiz.security.entity.Role
import fm.force.quiz.security.entity.User
import fm.force.quiz.security.jwt.JwtUserDetails
import io.kotlintest.matchers.boolean.shouldBeTrue
import io.kotlintest.matchers.string.shouldNotBeBlank
import io.kotlintest.matchers.types.shouldBeNull
import io.kotlintest.matchers.types.shouldNotBeNull
import io.kotlintest.specs.WordSpec
import java.util.Date
import org.springframework.test.context.ContextConfiguration

@ContextConfiguration(classes = [TestConfiguration::class])
open class JwtProviderServiceTest(
    private val jwtProviderService: JwtProviderService
) : WordSpec() {
    private val jwtUserDetailsFactory = JwtUserDetailsMapper()
    private val randomUserDetails: JwtUserDetails get() = jwtUserDetailsFactory.fromUser(randomUser)
    private val randomUser
        get() = User(
            username = "username-${getRandomString()}",
            email = "email-${getRandomString()}@example.com",
            roles = setOf(Role(Role.predefinedRoleNames.random()))
        )

    init {
        "JwtTokenProvider" should {
            "issue jwt tokens with empty roles" {
                val userDetails = jwtUserDetailsFactory.fromEmpty().apply {
                    username = "sample@example.com"
                    password = "password"
                }

                val token = jwtProviderService.issue(userDetails)
                token.shouldNotBeBlank()
            }

            "serialize tokens for valid User entities" {
                val token = jwtProviderService.issue(randomUserDetails)
                token.shouldNotBeBlank()
            }

            "validate tokens" {
                var token = jwtProviderService.issue(randomUserDetails)
                val details = jwtProviderService.validate(token)
                details.shouldNotBeNull()
                details.username.shouldNotBeBlank()
                details.isUsable().shouldBeTrue()

                token = jwtProviderService.issue(randomUserDetails, now = Date(Date().time - 100000000))
                jwtProviderService.validate(token).shouldBeNull()
            }
        }
    }
}
