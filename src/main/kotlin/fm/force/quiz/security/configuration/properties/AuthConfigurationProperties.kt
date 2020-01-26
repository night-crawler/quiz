package fm.force.quiz.security.configuration.properties

import fm.force.quiz.security.entity.Role
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

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

        override fun toString() =
            "AccessPattern(pattern='$pattern', authority=$authority, anonymous=$anonymous)"
    }

    var access: List<AccessPattern> = listOf(
        AccessPattern(pattern = "/admin/**", authority = Role.PredefinedRoles.ADMIN.name),
        AccessPattern(pattern = "/auth/**", anonymous = true)
    )
}
