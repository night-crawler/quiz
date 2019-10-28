package fm.force.quiz.security.configuration

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "force.security.jwt")
class JwtConfigurationProperties {
    lateinit var secret: String
    var expire = 5 * 60
    var issuer = "quiz"
}

@Configuration
@ConfigurationProperties(prefix = "force.security.password")
class PasswordConfigurationProperties {
    lateinit var secret: String
    var iterations = 5
    var minLength = 8
}

@Configuration
@ConfigurationProperties(prefix = "force.security.password.argon2")
class Argon2PasswordConfigurationProperties {
    val maxMillisecs: Long = 1000
    val memory: Int = 65536
    val parallelism: Int = 1
    val iterations: Int = 10
}
