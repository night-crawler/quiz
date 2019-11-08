package io.kotlintest.provided.fm.force.quiz.security.service

import fm.force.quiz.security.service.PasswordHashGeneratorService
import io.kotlintest.matchers.boolean.shouldBeTrue
import io.kotlintest.matchers.string.shouldNotBeBlank
import io.kotlintest.matchers.types.shouldNotBeNull
import io.kotlintest.provided.fm.force.quiz.TestConfiguration
import io.kotlintest.specs.WordSpec
import org.springframework.test.context.ContextConfiguration


@ContextConfiguration(classes = [TestConfiguration::class])
open class PasswordHashGeneratorServiceTest(
        private val passwordHashGeneratorService: PasswordHashGeneratorService
) : WordSpec() {

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
