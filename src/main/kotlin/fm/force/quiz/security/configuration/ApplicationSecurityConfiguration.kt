package fm.force.quiz.security.configuration

import de.mkammerer.argon2.Argon2Factory
import fm.force.quiz.security.service.JwtAuthProviderService
import fm.force.quiz.security.jwt.JwtConfigurer
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter


@Configuration
@EnableWebSecurity
class ApplicationSecurityConfiguration(
        private val jwtAuthProviderService: JwtAuthProviderService
) : WebSecurityConfigurerAdapter(true) {
    @Value("\${auth.prefixes.permitAll:/auth/**}")
    private lateinit var authEndpointPrefix: String

    @Value("\${auth.prefixes.admin:/admin/**}")
    private lateinit var adminEndpointPrefix: String

    @Bean
    fun passwordHashGenerator() {
        Argon2Factory.create()
    }

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
