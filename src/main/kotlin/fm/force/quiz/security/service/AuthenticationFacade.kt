package fm.force.quiz.security.service

import fm.force.quiz.security.jwt.JwtUserDetails
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component

@Component
class AuthenticationFacade(
        private val jwtUserDetailsService: JwtUserDetailsService
) {
    val principal
        get() =
            SecurityContextHolder.getContext().authentication.principal as JwtUserDetails

    val jwtUserDetails
        get() =
            jwtUserDetailsService.loadUserByUsername(principal.username) as JwtUserDetails

    val user get() = jwtUserDetailsService.getUser(principal.username)
}
