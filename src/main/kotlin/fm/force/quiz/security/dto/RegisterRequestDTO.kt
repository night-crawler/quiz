package fm.force.quiz.security.dto

import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size


// TODO: how to get rid of magic numbers not using constants file?
data class RegisterRequestDTO(
        @field:Email(message = "Email is mandatory")
        @field:Size(min = 8, max = 100)
        val email: String,

        @field:NotBlank(message = "Password is mandatory")
        @field:Size(min = 8, max = 64)
        val password: String
)
