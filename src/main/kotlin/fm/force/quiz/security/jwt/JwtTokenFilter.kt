package fm.force.quiz.security.jwt

import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler
import org.springframework.web.filter.GenericFilterBean
import javax.servlet.FilterChain
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import org.springframework.security.access.AccessDeniedException

class JwtTokenFilter(
        private val jwtAuthProviderService: JwtAuthProviderService
) : GenericFilterBean() {
    private val failureHandler = SimpleUrlAuthenticationFailureHandler()

    override fun doFilter(request: ServletRequest?, response: ServletResponse?, chain: FilterChain?) {
        if (false) {
            // TODO: this method gets called twice for some reason with different arguments
            failureHandler.onAuthenticationFailure(
                    request as HttpServletRequest?,
                    response as HttpServletResponse?,
                    JwtAuthenticationException("LOL!")
            )
            return
        }

//        throw AccessDeniedException("!")

//        SecurityContextHolder.getContext().authentication = UsernamePasswordAuthenticationToken(
//                JwtUserDetails(),
//                "",
//                listOf<GrantedAuthority>()
//        )
        chain?.doFilter(request, response)
    }


}