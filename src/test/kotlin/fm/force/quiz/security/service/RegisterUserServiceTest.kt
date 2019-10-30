package io.kotlintest.provided.fm.force.quiz.security.service

import fm.force.quiz.security.service.RegisterUserService
import io.kotlintest.provided.fm.force.quiz.security.jwt.JwtConfiguration
import io.kotlintest.specs.StringSpec
import org.springframework.test.context.ContextConfiguration

@ContextConfiguration(classes = [JwtConfiguration::class])
open class RegisterUserServiceTest(
        private val registerUserService: RegisterUserService
) : StringSpec() {

    init {
        "register" {
            registerUserService.register("vasya@example.com", "trash")
        }
    }

}