package fm.force.quiz.authentication

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.WebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import javax.sql.DataSource


@Configuration
class ApplicationSecurity : WebSecurityConfigurerAdapter(true) {
    val logger: Logger = LoggerFactory.getLogger(ApplicationSecurity::class.java)

    @Autowired
    fun initialize(builder: AuthenticationManagerBuilder, dataSource: DataSource) {
        logger.debug("Configuring Application security")
    }
}