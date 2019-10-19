package fm.force.quiz.security.jwt

import fm.force.quiz.security.entity.User
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
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
        if (false) {
            // TODO: this method gets called twice for some reason with different arguments
            failureHandler.onAuthenticationFailure(
                    request as HttpServletRequest?,
                    response as HttpServletResponse?,
                    JwtAuthenticationException("LOL!")
            )
            return
        }

//        SecurityContextHolder.getContext().authentication = UsernamePasswordAuthenticationToken(
//                JwtUserDetails(),
//                "",
//                listOf<GrantedAuthority>()
//        )
        chain?.doFilter(request, response)
    }


}