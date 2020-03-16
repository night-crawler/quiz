package fm.force.quiz.security.service

import fm.force.quiz.security.jwt.JwtUserDetails
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component

@Component
class AuthenticationFacade(private val authService: AuthService) {
    val principal
        get() =
            SecurityContextHolder.getContext().authentication.principal as JwtUserDetails

    val jwtUserDetails
        get() =
            authService.loadUserByUsername(principal.username) as JwtUserDetails

    val user get() = authService.getUser(principal.username)
}
