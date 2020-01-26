package fm.force.quiz.security.configuration.properties

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.time.Duration
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration
import javax.annotation.PostConstruct

@Configuration
@ConfigurationProperties(prefix = "force.security.jwt")
class JwtConfigurationProperties {
    private val logger: Logger = LoggerFactory.getLogger(this::class.java)

    lateinit var secret: String
    var expire: Duration = Duration.ofDays(1L)
    var issuer = "quiz"

    @PostConstruct
    fun postConstruct() {
        logger.info("[ø] Set issuer to $issuer and expire in $expire")
    }
}
