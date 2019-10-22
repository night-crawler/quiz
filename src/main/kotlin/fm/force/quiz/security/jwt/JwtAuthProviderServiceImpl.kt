package fm.force.quiz.security.jwt

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.stereotype.Service
import javax.servlet.http.HttpServletRequest

@Service
class JwtAuthProviderServiceImpl(val jwtProvider: JwtProvider) : JwtAuthProviderService() {
    private val logger: Logger = LoggerFactory.getLogger(this::class.java)
    private val bearer = "bearer "
    override fun authorizeRequest(request: HttpServletRequest): Authentication {
        val authHeader = request.getHeader("authorization")

        when {
            (authHeader == null) -> throw BadCredentialsException("Authorization was not present")
            (authHeader.isEmpty()) -> throw BadCredentialsException("Authorization header is empty")
            (authHeader.length <= bearer.length) -> throw BadCredentialsException("Authorization header corrupted")
            (authHeader.substring(0, bearer.length).toLowerCase() != bearer)
                -> throw BadCredentialsException("Authorization header must start with Bearer")
        }

        val token = authHeader.substring(bearer.length)
        if (!jwtProvider.validate(token)) {
            throw AccessDeniedException("Provided token is not valid")
        }


        return UsernamePasswordAuthenticationToken(
                JwtUserDetailsFactoryImpl().createUserDetails(),
                "",
                listOf<GrantedAuthority>()
        )
    }
}
