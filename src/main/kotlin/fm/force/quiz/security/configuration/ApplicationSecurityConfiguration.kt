package fm.force.quiz.security.configuration

import fm.force.quiz.security.service.JwtAuthProviderService
import fm.force.quiz.security.jwt.JwtConfigurer
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter


@Configuration
@EnableWebSecurity
class ApplicationSecurityConfiguration(
        private val jwtAuthProviderService: JwtAuthProviderService,
        private val config: AuthConfigurationProperties
) : WebSecurityConfigurerAdapter(true) {
    val logger: Logger = LoggerFactory.getLogger(ApplicationSecurityConfiguration::class.java)

    override fun configure(http: HttpSecurity) {
        logger.debug("Applying security access patterns: ${config.access}")
        http
                .httpBasic().disable()
                .csrf().disable()
                .sessionManagement().disable()
                .headers()
                .and().anonymous()
                .and().exceptionHandling()
                .and().authorizeRequests()
                .apply {
                    config.access.forEach {
                        if (it.anonymous) {
                            antMatchers(it.pattern).permitAll()
                        } else {
                            antMatchers(it.pattern).hasAuthority(it.authority)
                        }
                    }
                }
                .anyRequest().authenticated()
                .and().apply(JwtConfigurer(jwtAuthProviderService))
    }
}
