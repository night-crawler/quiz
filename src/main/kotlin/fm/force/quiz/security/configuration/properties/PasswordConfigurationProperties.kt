package fm.force.quiz.security.configuration.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "force.security.password")
class PasswordConfigurationProperties {
    var userIsEnabledAfterCreation = false
}
