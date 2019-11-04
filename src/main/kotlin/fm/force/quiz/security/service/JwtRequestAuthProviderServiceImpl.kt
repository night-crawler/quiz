package fm.force.quiz.security.service

import fm.force.quiz.security.jwt.JwtAuthenticationException
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Service
import javax.servlet.http.HttpServletRequest

@Service
class JwtRequestAuthProviderServiceImpl(val jwtProviderService: JwtProviderService) : JwtRequestAuthProviderService() {
    private val bearer = "bearer "
    override fun authorizeRequest(request: HttpServletRequest?): Authentication {
        request ?: throw JwtAuthenticationException("It must never happen")

        val authHeader = request.getHeader("authorization")

        when {
            (authHeader == null) -> throw BadCredentialsException("Authorization was not present")
            (authHeader.isEmpty()) -> throw BadCredentialsException("Authorization header is empty")
            (authHeader.length <= bearer.length) -> throw BadCredentialsException("Authorization header is corrupted")
            (authHeader.substring(0, bearer.length).toLowerCase() != bearer)
                -> throw BadCredentialsException("Authorization header must start with the `Bearer` keyword")
        }

        val token = authHeader.substring(bearer.length)
        val jwtUserDetails = jwtProviderService.validate(token)
                ?: throw JwtAuthenticationException("Provided token is invalid")

        return UsernamePasswordAuthenticationToken(jwtUserDetails, "", jwtUserDetails.authorities)
    }
}
