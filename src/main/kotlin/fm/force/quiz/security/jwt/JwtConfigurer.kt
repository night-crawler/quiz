package fm.force.quiz.security.jwt

import org.springframework.security.config.annotation.SecurityConfigurerAdapter
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.web.DefaultSecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

class JwtConfigurer : SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity>() {
    override fun configure(builder: HttpSecurity) {
        val jwtTokenFilter = JwtTokenFilter()
        println("HEY I AM HERE")
        builder.addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter::class.java)
        println("?")
    }
}