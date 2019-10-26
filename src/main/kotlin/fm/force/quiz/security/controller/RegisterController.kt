package fm.force.quiz.security.controller

import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("auth")
class RegisterController {
    @PostMapping("register")
    fun register(): String {
        return "OK"
    }
}
