package io.kotlintest.provided.fm.force.quiz.security.service

import fm.force.quiz.security.dto.RegisterUserRequestDTO
import fm.force.quiz.security.service.RegisterUserService
import io.kotlintest.provided.fm.force.quiz.security.SecurityTestConfiguration
import io.kotlintest.specs.StringSpec
import org.springframework.test.context.ContextConfiguration

@ContextConfiguration(classes = [SecurityTestConfiguration::class])
open class RegisterUserServiceTest(
        private val registerUserService: RegisterUserService
) : StringSpec() {

    init {
        "check dto" {
            val registerUserRequestDTO = RegisterUserRequestDTO("", "p")
            println(registerUserRequestDTO.email)
        }

        "register" {
//            registerUserService.register("vasya@example.com", "trash")
        }
    }

}