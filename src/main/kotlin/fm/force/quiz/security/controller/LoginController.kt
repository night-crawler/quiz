package fm.force.quiz.security.controller

import fm.force.quiz.security.dto.JwtResponseDTO
import fm.force.quiz.security.dto.LoginRequestDTO
import fm.force.quiz.security.service.JwtUserDetailsService
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

@RestController
@Transactional
@RequestMapping("/auth/login")
class LoginController(
        val jwtUserDetailsService: JwtUserDetailsService
) {

    @PostMapping
    fun login(@Valid @RequestBody request: LoginRequestDTO): JwtResponseDTO {
        return jwtUserDetailsService.login(request)
    }
}
