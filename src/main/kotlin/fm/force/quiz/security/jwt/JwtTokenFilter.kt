package fm.force.quiz.security.jwt

import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler
import org.springframework.web.filter.GenericFilterBean
import javax.servlet.FilterChain
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse


class JwtTokenFilter : GenericFilterBean() {
    private val failureHandler = SimpleUrlAuthenticationFailureHandler()

    override fun doFilter(request: ServletRequest?, response: ServletResponse?, chain: FilterChain?) {
        failureHandler.onAuthenticationFailure(request as HttpServletRequest?, response as HttpServletResponse?, JwtAuthenticationException("LOL!"))

//        SecurityContextHolder.getContext().authentication = UsernamePasswordAuthenticationToken(
//                JwtUserDetails(email = "vasya@example.com"),
//                "",
//                listOf<GrantedAuthority>()
//        )
        chain?.doFilter(request, response)
    }

}