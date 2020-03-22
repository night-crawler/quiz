package fm.force.quiz.security.configuration

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.stereotype.Component

// we want to have 401 instead of the default 403
@Component
class SecurityAuthenticationEntryPoint(
    val serializer: IAuthenticationExceptionSerializer
) : AuthenticationEntryPoint {
    override fun commence(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authException: AuthenticationException
    ) {
        // https://www.iana.org/assignments/http-authschemes/http-authschemes.xhtml
        response.addHeader("WWW-Authenticate", """Bearer realm="/auth/login"""")
        response.status = HttpStatus.UNAUTHORIZED.value()
        response.contentType = "application/json"
        response.writer.write(serializer.serialize(authException))
    }
}
