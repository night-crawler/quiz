package fm.force.quiz.security.configuration

import fm.force.quiz.security.jwt.JwtAuthProviderService
import fm.force.quiz.security.jwt.JwtConfigurer
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter


@Configuration
@EnableWebSecurity
class ApplicationSecurityConfiguration(
        private val jwtAuthProviderService: JwtAuthProviderService
) : WebSecurityConfigurerAdapter(true) {
    // TODO: read properties
    private final val authEndpointPrefix = "/auth/**"
    private final val adminEndpointPrefix = "/admin/**"

    override fun configure(http: HttpSecurity) {
        http
                .httpBasic().disable()
                .csrf().disable()
                .sessionManagement().disable()
                .anonymous()
                .and().exceptionHandling()
                .and().authorizeRequests()
                .antMatchers(authEndpointPrefix).permitAll()
                .antMatchers(adminEndpointPrefix).hasAuthority("ADMIN")
                .anyRequest().authenticated()

                .and().apply(JwtConfigurer(jwtAuthProviderService))
    }
}
