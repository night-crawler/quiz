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
    @ExceptionHandler(DataIntegrityViolationException::class)
    @ResponseStatus(HttpStatus.CONFLICT)
    fun handleUserExists(ex: DataIntegrityViolationException) {}

    @PostMapping("register")
    @ResponseStatus(value = HttpStatus.CREATED)
    fun register(@Valid @RequestBody request: RegisterRequestDTO): RegisterResponseDTO {
       return jwtUserDetailsService.register(request)
    }
}
