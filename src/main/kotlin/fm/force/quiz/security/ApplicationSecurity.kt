package fm.force.quiz.security

import fm.force.quiz.repository.JpaUserRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.provisioning.InMemoryUserDetailsManager


@EnableWebSecurity
class WebSecurityConfig {

    @Bean
    fun userDetailsService(): UserDetailsService {
        val manager = InMemoryUserDetailsManager()
        manager.createUser(User.withDefaultPasswordEncoder().username("user").password("password").roles("USER").build())
        return manager
    }
}

@Configuration
@EnableWebSecurity
class ApplicationSecurity : WebSecurityConfigurerAdapter(true) {
    private final val AUTH_ENDPOINT_PREFIX = "/auth/**"
    private final val ADMIN_ENDPOINT_PREFIX = "/admin/**"

    private val logger: Logger = LoggerFactory.getLogger(ApplicationSecurity::class.java)

    @Autowired
    fun initialize(builder: AuthenticationManagerBuilder, userRepository: JpaUserRepository) {
        logger.error("===> HAHA GOT IT $userRepository")
        logger.debug("Configuring Application security")
    }

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
//        security.httpBasic().disable()
//        security.sessionManagement().disable()
    }
}
