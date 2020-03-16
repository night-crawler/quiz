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
import io.kotlintest.shouldBe
import io.kotlintest.shouldNotBe
import io.kotlintest.specs.WordSpec
import java.util.Date
import org.springframework.test.context.ContextConfiguration

@ContextConfiguration(classes = [TestConfiguration::class])
open class JwtProviderServiceTest(
    private val jwtProviderService: JwtProviderService
) : WordSpec() {
    private val jwtUserDetailsFactory = JwtUserDetailsMapper()
    private val randomUserDetails: JwtUserDetails get() = jwtUserDetailsFactory.of(randomUser)
    private val randomUser
        get() = User(
            username = "username-${getRandomString()}",
            email = "email-${getRandomString()}@example.com",
            roles = setOf(Role(Role.predefinedRoleNames.random()))
        )

    init {
        "JwtTokenProvider" should {
            "issue jwt tokens with empty roles" {
                val userDetails = jwtUserDetailsFactory.of().apply {
                    username = "sample@example.com"
                    password = "password"
                }

                val token = jwtProviderService.issueAccessToken(userDetails)
                token.shouldNotBeBlank()
            }

            "issue an access token for valid User Details" {
                val token = jwtProviderService.issueAccessToken(randomUserDetails)
                token.shouldNotBeBlank()
            }

            "issue a refresh token for valid User Details" {
                val token = jwtProviderService.issueRefreshToken(randomUserDetails)
                token.shouldNotBeBlank()
            }

            "extract UserDetails from access token" {
                var token = jwtProviderService.issueAccessToken(randomUserDetails)
                val details = jwtProviderService.extractUserDetailsFromAccessToken(token)
                details.shouldNotBeNull()
                details.username.shouldNotBeBlank()
                details.isUsable().shouldBeTrue()

                token = jwtProviderService.issueAccessToken(
                    randomUserDetails,
                    now = Date(0)
                )
                jwtProviderService.extractUserDetailsFromAccessToken(token).shouldBeNull()
            }

            "extract username from refresh token" {
                var token = jwtProviderService.issueRefreshToken(randomUserDetails)
                jwtProviderService.extractUsernameFromRefreshToken(token) shouldNotBe null

                token = jwtProviderService.issueRefreshToken(
                    randomUserDetails,
                    now = Date(0)
                )

                jwtProviderService.extractUsernameFromRefreshToken(token) shouldBe null
            }
        }
    }
}
