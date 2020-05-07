package fm.force.quiz.security.service

import fm.force.quiz.AbstractBootTest
import io.kotlintest.matchers.boolean.shouldBeTrue
import io.kotlintest.matchers.string.shouldNotBeBlank
import io.kotlintest.matchers.types.shouldNotBeNull
import io.kotlintest.specs.WordSpec

open class PasswordHashGeneratorServiceTest(
    private val passwordHashGeneratorService: PasswordHashGeneratorService
) : AbstractBootTest, WordSpec() {

    init {
        "PasswordHashGeneratorService" should {
            "generate hashes" {
                val hash = passwordHashGeneratorService.encode("my password")
                hash.shouldNotBeBlank()
                hash.shouldNotBeNull()

                val matches = passwordHashGeneratorService.matches("my password", hash)
                matches.shouldBeTrue()
            }
        }
    }
}
