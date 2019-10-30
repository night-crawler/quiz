package fm.force.quiz.security.controller

import fm.force.quiz.security.dto.RegisterUserRequestDTO
import fm.force.quiz.security.service.RegisterUserService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

@RestController
@RequestMapping("auth")
class RegisterController(
        private val registerUserService: RegisterUserService
) {
    @PostMapping("register")
    fun register(@Valid @RequestBody request: RegisterUserRequestDTO): String {
        return "OK"
    }
}
