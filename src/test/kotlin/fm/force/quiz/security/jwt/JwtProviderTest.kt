package fm.force.quiz.security.jwt

import fm.force.quiz.common.getRandomString
import fm.force.quiz.security.entity.Role
import fm.force.quiz.security.entity.User
import io.kotlintest.matchers.boolean.shouldBeTrue
import io.kotlintest.matchers.string.shouldNotBeBlank
import io.kotlintest.matchers.types.shouldBeNull
import io.kotlintest.matchers.types.shouldNotBeNull
import io.kotlintest.provided.fm.force.quiz.security.jwt.JwtConfiguration
import io.kotlintest.specs.WordSpec
import org.springframework.test.context.ContextConfiguration
import java.util.*


@ContextConfiguration(classes = [JwtConfiguration::class])
open class JwtProviderTest(
        private val jwtProvider: JwtProvider
) : WordSpec() {
    private val jwtUserDetailsFactory = JwtUserDetailsFactoryImpl()
    private val randomUserDetails: JwtUserDetails get() = jwtUserDetailsFactory.createUserDetails(randomUser)
    private val randomUser
        get() = User(
                username = "username-${getRandomString()}",
                email = "email-${getRandomString()}@example.com",
                roles = listOf(Role(Role.predefinedRoleNames.random()))
        )

    init {
        "JwtTokenProvider" should {
            "issue jwt tokens with empty roles" {
                val userDetails = jwtUserDetailsFactory.createUserDetails().apply {
                    username = "sample@example.com"
                    password = "password"
                }

                val token = jwtProvider.issue(userDetails)
                token.shouldNotBeBlank()
            }

            "serialize tokens for valid User entities" {
                val token = jwtProvider.issue(randomUserDetails)
                token.shouldNotBeBlank()
            }

            "validate tokens" {
                var token = jwtProvider.issue(randomUserDetails)
                var details = jwtProvider.validate(token)
                details.shouldNotBeNull()
                details.username.shouldNotBeBlank()
                details.isUsable().shouldBeTrue()

                token = jwtProvider.issue(randomUserDetails, now = Date(Date().time - 100000000))
                jwtProvider.validate(token).shouldBeNull()
            }
        }
    }
}
