package fm.force.quiz.security.dto

import fm.force.quiz.security.validator.CheckEmail
import javax.validation.constraints.NotBlank


data class RegisterUserRequestDTO(
        @field:CheckEmail
        val email: String,

        @field:NotBlank(message = "Password is mandatory")
        val password: String
)
