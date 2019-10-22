package fm.force.quiz.security.configuration

import fm.force.quiz.security.jwt.JwtAuthProviderService
import fm.force.quiz.security.jwt.JwtConfigurer
import fm.force.quiz.security.jwt.JwtProvider
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy


@Configuration
@EnableWebSecurity
class ApplicationSecurityConfiguration(
        private val jwtProvider: JwtProvider,
        private val jwtAuthProviderService: JwtAuthProviderService
) : WebSecurityConfigurerAdapter(true) {
    private final val AUTH_ENDPOINT_PREFIX = "/auth/**"
    private final val ADMIN_ENDPOINT_PREFIX = "/admin/**"

    private final val logger: Logger = LoggerFactory.getLogger(this::class.java)

    override fun configure(http: HttpSecurity) {
        http
                .httpBasic().disable()
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
//                .exceptionHandling()
                .authorizeRequests()
                .antMatchers(AUTH_ENDPOINT_PREFIX).permitAll()
                .anyRequest().authenticated()
                .and()
                .apply(JwtConfigurer(jwtAuthProviderService))
    }
}
