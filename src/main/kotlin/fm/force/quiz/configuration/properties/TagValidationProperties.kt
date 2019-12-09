package fm.force.quiz.configuration.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "force.quiz.validation.tag")
class TagValidationProperties {
    var minTagLength: Int = 3
}