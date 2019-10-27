package io.kotlintest.provided.fm.force.quiz.security.service

import fm.force.quiz.security.service.PasswordHashGeneratorService
import io.kotlintest.provided.fm.force.quiz.security.jwt.JwtConfiguration
import io.kotlintest.specs.WordSpec
import org.springframework.test.context.ContextConfiguration


@ContextConfiguration(classes = [JwtConfiguration::class])
open class PasswordHashGeneratorServiceTest(
        private val passwordHashGeneratorService: PasswordHashGeneratorService
) : WordSpec() {

    init {
        "PasswordHashGeneratorService" should {
            "generate hashes" {
            }
        }
    }
}
