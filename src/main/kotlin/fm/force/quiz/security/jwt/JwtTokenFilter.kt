package fm.force.quiz.security.jwt

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler
import org.springframework.web.filter.GenericFilterBean
import javax.servlet.FilterChain
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.context.SecurityContextHolder

class JwtTokenFilter(
        private val jwtAuthProviderService: JwtAuthProviderService
) : GenericFilterBean() {
    // TODO: Why would they ever define their shitty logger?
    private val niceLogger: Logger = LoggerFactory.getLogger(this::class.java)
    private val failureHandler = SimpleUrlAuthenticationFailureHandler()

    // TODO: this method gets called twice for some reason with different arguments
    // TODO: I wonder if request / response can really be null
    override fun doFilter(request: ServletRequest?, response: ServletResponse?, chain: FilterChain?) {
        val httpRequest = request as HttpServletRequest?
        val httpResponse = response as HttpServletResponse?

        try {
            SecurityContextHolder.getContext().authentication = jwtAuthProviderService.authorizeRequest(httpRequest)
            niceLogger.trace("Request was authenticated by JWT token")
        } catch (exc: AuthenticationException) {
            // TODO: take a look at BasicAuthenticationFilter for options when it's OK to process the chain
            failureHandler.onAuthenticationFailure(httpRequest, httpResponse, exc)
            niceLogger.debug("Authentication failed: {}", exc.localizedMessage)
            return
        }
        chain?.doFilter(request, response)
    }
}
