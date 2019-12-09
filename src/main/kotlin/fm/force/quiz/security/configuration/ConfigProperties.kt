package fm.force.quiz.security.configuration

import fm.force.quiz.security.entity.Role
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration
import java.time.Duration



@Configuration
@ConfigurationProperties(prefix = "force.security.jwt")
class JwtConfigurationProperties {
    lateinit var secret: String
    var expire: Duration = Duration.ofDays(1L)
    var issuer = "quiz"
}

@Configuration
@ConfigurationProperties(prefix = "force.security.auth")
class AuthConfigurationProperties {
    class AccessPattern() {
        constructor(pattern: String, authority: String? = null, anonymous: Boolean = false) : this() {
            this.pattern = pattern
            this.authority = authority
            this.anonymous = anonymous
        }
        lateinit var pattern: String
        var authority: String? = null
        var anonymous: Boolean = false

        override fun toString(): String {
            return "AccessPattern(pattern='$pattern', authority='$authority', anonymous=$anonymous)"
        }
    }
    var access: List<AccessPattern> = listOf(
            AccessPattern(pattern="/admin/**", authority = Role.PredefinedRoles.ADMIN.name),
            AccessPattern(pattern="/auth/**", anonymous = true)
    )
}

@Configuration
@ConfigurationProperties(prefix = "force.security.password")
class PasswordConfigurationProperties {
    var minEmailLength = 5
    var maxEmailLength = 255
    var minPasswordLength = 8
    var userIsEnabledAfterCreation = false
}

@Configuration
@ConfigurationProperties(prefix = "force.security.password.argon2")
class Argon2PasswordConfigurationProperties {
    val maxMillisecs: Long = 1000
    val memory: Int = 65536
    val parallelism: Int = 1
    val iterations: Int = 10
}
