package fm.force.quiz.security.jwt

import fm.force.quiz.security.service.JwtAuthProviderService
import org.springframework.security.config.annotation.SecurityConfigurerAdapter
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.web.DefaultSecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

class JwtConfigurer(private val jwtAuthProviderService: JwtAuthProviderService) : SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity>() {
    override fun configure(builder: HttpSecurity) {
        val jwtTokenFilter = JwtTokenFilter(jwtAuthProviderService)
        builder.addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter::class.java)
    }
}
