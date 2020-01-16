package fm.force.quiz.security.controller

import fm.force.quiz.security.dto.RegisterRequestDTO
import fm.force.quiz.security.dto.RegisterResponseDTO
import fm.force.quiz.security.dto.toDTO
import fm.force.quiz.security.service.JwtAuthService
import javax.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("auth")
class RegisterController(
    private val jwtAuthService: JwtAuthService
) {
    @PostMapping("register")
    @ResponseStatus(value = HttpStatus.CREATED)
    fun register(
        @Valid @RequestBody
        request: RegisterRequestDTO
    ): RegisterResponseDTO {
        return jwtAuthService.register(request).toDTO()
    }
}
