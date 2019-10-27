package fm.force.quiz.security.controller

import fm.force.quiz.security.dto.RegisterUserRequestDTO
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("auth")
class RegisterController {
    @PostMapping("register")
    fun register(@RequestBody request: RegisterUserRequestDTO): String {
        return "OK"
    }
}
