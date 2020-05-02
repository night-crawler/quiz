package fm.force.quiz.security.configuration.properties

import java.time.Duration
import javax.annotation.PostConstruct
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "force.security.jwt")
class JwtConfigurationProperties {
    private val logger: Logger = LoggerFactory.getLogger(this::class.java)

    lateinit var secret: String
    var accessTokenExpire: Duration = Duration.ofDays(1L)
    var refreshTokenExpire: Duration = Duration.ofDays(30 * 6)
    var refreshTokenCookieName = "Refresh-Token"
    var refreshTokenCookiePath = "/"
    var issuer = "quiz"

    @PostConstruct
    fun postConstruct() {
        logger.info("[ø] JWT issuer: $issuer")
        logger.info("[ø] JWT access token expires in: ${accessTokenExpire.seconds} seconds")
        logger.info("[ø] JWT refresh token expires in: ${refreshTokenExpire.seconds} seconds")
        logger.info("[ø] JWT Refresh cookie name: $refreshTokenCookieName")
        logger.info("[ø] JWT Refresh cookie path: $refreshTokenCookiePath")
    }
}
