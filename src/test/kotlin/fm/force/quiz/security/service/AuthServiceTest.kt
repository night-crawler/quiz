package fm.force.quiz.security.service

import fm.force.quiz.AbstractBootTest
import fm.force.quiz.security.configuration.properties.JwtConfigurationProperties
import fm.force.quiz.security.configuration.properties.PasswordConfigurationProperties
import fm.force.quiz.security.dto.LoginRequestDTO
import fm.force.quiz.security.dto.RegisterRequestDTO
import fm.force.quiz.security.repository.UserRepository
import io.kotlintest.matchers.haveSize
import io.kotlintest.matchers.numerics.shouldBeGreaterThan
import io.kotlintest.should
import io.kotlintest.shouldBe
import io.kotlintest.shouldNotBe
import io.kotlintest.shouldThrow
import io.kotlintest.specs.StringSpec
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.security.authentication.BadCredentialsException
import javax.servlet.http.Cookie
import javax.validation.Validator


open class AuthServiceTest(
    private val userRepository: UserRepository,
    private val service: AuthService,
    private val validator: Validator,
    private val conf: JwtConfigurationProperties,
    private val passwordConfigurationProperties: PasswordConfigurationProperties
) : AbstractBootTest, StringSpec() {
    init {
        // Actually this test does not seem to be a proper place to check validation errors
        "ensure empty values are not allowed" {
            val violations = validator.validate(
                RegisterRequestDTO("", "")
            )
            violations.size shouldBeGreaterThan 2
        }

        "ensure valid credentials are accepted" {
            val violations = validator.validate(
                RegisterRequestDTO("user@example.com", "12345678")
            )
            violations should haveSize(0)
        }

        "normal register" {
            val user = service.register(RegisterRequestDTO("user@example.com", "password"))
            user.id shouldNotBe null
        }

        "duplicated emails should not be accepted" {
            service.register(RegisterRequestDTO("dup@example.com", "password"))

            shouldThrow<DataIntegrityViolationException> {
                service.register(RegisterRequestDTO("dup@example.com", "password"))
            }
        }

        "user activation" {
            var user = service.register(
                RegisterRequestDTO("activate+me@example.com", "password"),
                isActive = false
            )
            service.activateUser(user)
            user = service.getUser(user.email)
            user.isActive shouldBe true
        }

        "issue access token by refresh token set in cookies" {
            service.register(RegisterRequestDTO("refresh+me@example.com", "password"), isActive = true)
            val tokens = service.login(LoginRequestDTO("refresh+me@example.com", "password"))

            // should not throw any exceptions with a correct token
            val correctRequest = MockHttpServletRequest()
            correctRequest.setCookies(service.getRefreshTokenCookie(tokens.refreshToken))
            service.issueAccessTokenByRefreshToken(correctRequest)

            val requestWithMissingCookie = MockHttpServletRequest()
            requestWithMissingCookie.setCookies(Cookie("Johnny", "Be good"))
            shouldThrow<BadCredentialsException> {
                service.issueAccessTokenByRefreshToken(requestWithMissingCookie)
            }

            val requestWithWrongCookie = MockHttpServletRequest()
            requestWithWrongCookie.setCookies(Cookie(conf.refreshTokenCookieName, "Nope"))
            shouldThrow<BadCredentialsException> {
                service.issueAccessTokenByRefreshToken(requestWithWrongCookie)
            }
        }
    }
}
