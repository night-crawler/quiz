package fm.force.quiz.security.controller

import fm.force.quiz.security.dto.JwtResponseTokensDTO
import fm.force.quiz.security.dto.LoginRequestDTO
import fm.force.quiz.security.dto.RegisterRequestDTO
import fm.force.quiz.security.dto.toDTO
import fm.force.quiz.security.service.AuthService
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import javax.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("auth")
class AuthController(private val authService: AuthService) {
    @PostMapping("register")
    @ResponseStatus(value = HttpStatus.CREATED)
    fun register(
        @Valid @RequestBody
        request: RegisterRequestDTO
    ) =
        authService.register(request).toDTO()

    @PostMapping("login")
    fun login(
        @Valid @RequestBody
        request: LoginRequestDTO,
        response: HttpServletResponse
    ): JwtResponseTokensDTO = authService.login(request).also {
        authService.setupRefreshTokenCookie(response, it.refreshToken)
    }

    @PostMapping("refresh")
    fun refresh(request: HttpServletRequest) =
        authService.issueAccessTokenByRefreshToken(request)
}
