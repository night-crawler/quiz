package fm.force.quiz.security.configuration.properties

import java.time.Duration
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "force.security.jwt")
class JwtConfigurationProperties {
    lateinit var secret: String
    var expire: Duration = Duration.ofDays(1L)
    var issuer = "quiz"
}
