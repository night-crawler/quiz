package fm.force.quiz.security.configuration

import fm.force.quiz.security.configuration.properties.AuthConfigurationProperties
import fm.force.quiz.security.configuration.properties.CorsConfigurationProperties
import fm.force.quiz.security.jwt.JwtConfigurer
import fm.force.quiz.security.service.JwtRequestAuthProviderService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter

@Configuration
@EnableWebSecurity
class ApplicationSecurityConfiguration(
    private val jwtRequestAuthProviderService: JwtRequestAuthProviderService,
    private val config: AuthConfigurationProperties,
    private val corsConfigurationProperties: CorsConfigurationProperties,
    private val securityAuthenticationEntryPoint: SecurityAuthenticationEntryPoint
) : WebSecurityConfigurerAdapter(true) {
    val logger: Logger = LoggerFactory.getLogger(ApplicationSecurityConfiguration::class.java)

    override fun configure(http: HttpSecurity) {
        logger.debug("Applying security access patterns: ${config.access}")
        http
            .cors().configurationSource(corsConfigurationProperties.source).and()
            .httpBasic().disable()
            .csrf().disable()
            .sessionManagement().disable()
            .headers()
            .and().anonymous()
            .and().exceptionHandling().authenticationEntryPoint(securityAuthenticationEntryPoint)
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
            .and().apply(JwtConfigurer(jwtRequestAuthProviderService))
    }
}
