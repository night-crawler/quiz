package fm.force.quiz.security.jwt

import fm.force.quiz.common.getRandomString
import fm.force.quiz.security.entity.Role
import fm.force.quiz.security.entity.User
import io.kotlintest.matchers.boolean.shouldBeFalse
import io.kotlintest.matchers.boolean.shouldBeTrue
import io.kotlintest.matchers.string.shouldNotBeBlank
import io.kotlintest.provided.fm.force.quiz.security.jwt.JwtConfiguration
import io.kotlintest.specs.WordSpec
import org.springframework.test.context.ContextConfiguration
import java.util.*


@ContextConfiguration(classes = [JwtConfiguration::class])
open class JwtTokenTest(
        private val jwtTokenProvider: JwtTokenProvider,
        private val jwtDetailsFactory: IJwtUserDetailsFactory
) : WordSpec() {

    private val randomUser
        get() = User(
                username = "username-${getRandomString()}".also { println("ø $it") },
                email = "email-${getRandomString()}@example.com",
                roles = listOf(Role(Role.predefinedRoleNames.random()))
        )

    private val randomUserDetails: JwtUserDetails get() = jwtDetailsFactory.createUserDetails(randomUser)

    init {
        "JwtTokenProvider" should {
            "issue jwt tokens with empty roles" {
                val userDetails = jwtDetailsFactory.createUserDetails().apply {
                    username = "sample@example.com"
                    password = "password"
                }

                val token = jwtTokenProvider.issue(userDetails)
                token.shouldNotBeBlank()
            }

            "serialize tokens for valid User entities" {
                val token = jwtTokenProvider.issue(randomUserDetails)
                token.shouldNotBeBlank()
            }

            "validate tokens" {
                var token = jwtTokenProvider.issue(randomUserDetails)
                jwtTokenProvider.validate(token).shouldBeTrue()

                token = jwtTokenProvider.issue(randomUserDetails, now = Date(Date().time - 100000000))
                jwtTokenProvider.validate(token).shouldBeFalse()

                jwtTokenProvider.validate("").shouldBeFalse()
            }
        }
    }
}
