package fm.force.quiz.security.configuration.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "force.security.password.argon2")
class Argon2PasswordConfigurationProperties {
    val maxMillisecs: Long = 1000
    val memory: Int = 65536
    val parallelism: Int = 1
    val iterations: Int = 10
}
