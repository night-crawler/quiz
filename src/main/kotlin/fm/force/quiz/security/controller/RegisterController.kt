package fm.force.quiz.security.controller

import fm.force.quiz.security.dto.RegisterRequestDTO
import fm.force.quiz.security.dto.RegisterResponseDTO
import fm.force.quiz.security.service.JwtUserDetailsService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import javax.validation.Valid
import org.springframework.dao.DataIntegrityViolationException


@RestController
@RequestMapping("auth")
class RegisterController(
        private val jwtUserDetailsService: JwtUserDetailsService
) {
    @ResponseStatus(value = HttpStatus.CONFLICT, reason = "User exists")
    @ExceptionHandler(DataIntegrityViolationException::class)
    fun userExists() {
    }

    @PostMapping("register")
    @ResponseStatus(value= HttpStatus.CREATED)
    fun register(@Valid @RequestBody request: RegisterRequestDTO): RegisterResponseDTO {
       return jwtUserDetailsService.register(request)
    }
}
