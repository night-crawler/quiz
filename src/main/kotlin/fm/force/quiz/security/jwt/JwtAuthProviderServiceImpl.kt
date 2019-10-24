package fm.force.quiz.security.jwt

import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Service
import javax.servlet.http.HttpServletRequest

@Service
class JwtAuthProviderServiceImpl(val jwtProvider: JwtProvider) : JwtAuthProviderService() {
    private val bearer = "bearer "
    override fun authorizeRequest(request: HttpServletRequest?): Authentication {
        request ?: throw JwtAuthenticationException("It must never happen")

        val authHeader = request.getHeader("authorization")

        when {
            (authHeader == null) -> throw BadCredentialsException("Authorization was not present")
            (authHeader.isEmpty()) -> throw BadCredentialsException("Authorization header is empty")
            (authHeader.length <= bearer.length) -> throw BadCredentialsException("Authorization header corrupted")
            (authHeader.substring(0, bearer.length).toLowerCase() != bearer)
                -> throw BadCredentialsException("Authorization header must start with Bearer")
        }

        val token = authHeader.substring(bearer.length)
        val jwtUserDetails = jwtProvider.validate(token)
                ?: throw JwtAuthenticationException("Provided token is not valid")

        return UsernamePasswordAuthenticationToken(jwtUserDetails, "", jwtUserDetails.authorities)
    }
}
