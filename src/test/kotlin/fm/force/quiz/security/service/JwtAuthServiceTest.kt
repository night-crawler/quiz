package fm.force.quiz.security.service

import fm.force.quiz.TestConfiguration
import fm.force.quiz.security.dto.RegisterRequestDTO
import io.kotlintest.matchers.haveSize
import io.kotlintest.matchers.numerics.shouldBeGreaterThan
import io.kotlintest.should
import io.kotlintest.shouldNotBe
import io.kotlintest.shouldThrow
import io.kotlintest.specs.StringSpec
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.test.context.ContextConfiguration
import javax.validation.Validator


@ContextConfiguration(classes = [TestConfiguration::class])
open class JwtAuthServiceTest(
        private val service: JwtAuthService,
        private val validator: Validator
) : StringSpec() {
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

        "duplicate email" {
            service.register(RegisterRequestDTO("dup@example.com", "password"))

            shouldThrow<DataIntegrityViolationException> {
                service.register(RegisterRequestDTO("dup@example.com", "password"))
            }
        }
    }

}