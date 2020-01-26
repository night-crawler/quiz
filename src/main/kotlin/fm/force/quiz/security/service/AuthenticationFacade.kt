package fm.force.quiz.security.service

import fm.force.quiz.security.jwt.JwtUserDetails
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component

@Component
class AuthenticationFacade(private val jwtAuthService: JwtAuthService) {
    val principal
        get() =
            SecurityContextHolder.getContext().authentication.principal as JwtUserDetails

    val jwtUserDetails
        get() =
            jwtAuthService.loadUserByUsername(principal.username) as JwtUserDetails

    val user get() = jwtAuthService.getUser(principal.username)
}
