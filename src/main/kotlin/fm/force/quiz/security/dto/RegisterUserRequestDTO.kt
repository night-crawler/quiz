package fm.force.quiz.security.dto

import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank


data class RegisterUserRequestDTO(
        @NotBlank(message = "Email is mandatory")
        @Email
        val email: String,

        @NotBlank(message = "Password is mandatory")
        val password: String
)
