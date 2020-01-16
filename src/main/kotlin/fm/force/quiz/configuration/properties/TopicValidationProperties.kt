package fm.force.quiz.configuration.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties("force.quiz.validation.topic")
class TopicValidationProperties {
    var minTitleLength = 3
    var maxTitleLength = 100
}
