package fm.force.quiz.security.jwt

import fm.force.quiz.security.service.JwtRequestAuthProviderService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.GenericFilterBean
import javax.servlet.FilterChain
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletRequest


class JwtTokenFilter(
        private val jwtRequestAuthProviderService: JwtRequestAuthProviderService
) : GenericFilterBean() {
    private val niceLogger: Logger = LoggerFactory.getLogger(this::class.java)
    /* private val failureHandler = SimpleUrlAuthenticationFailureHandler()
    *  it seems this handler can terminate any request instantly:
    *     failureHandler.onAuthenticationFailure(httpRequest, httpResponse, exc)
    */


    /* sometimes spring calls doFilter twice after an auth failure:
     * first time for the original request, and the second one for an `/error` url
    */
    override fun doFilter(request: ServletRequest?, response: ServletResponse?, chain: FilterChain?) {
        val httpRequest = request as HttpServletRequest?
        val path = httpRequest?.requestURI

        try {
            SecurityContextHolder.getContext().authentication = jwtRequestAuthProviderService.authorizeRequest(httpRequest)
            niceLogger.debug("Request `{}` was authenticated by JWT token", path)
        } catch (exc: AuthenticationException) {
            niceLogger.debug("Request `{}` authentication failed: `{}`", path, exc.localizedMessage)
        }
        chain?.doFilter(request, response)
    }
}
