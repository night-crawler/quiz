package fm.force.quiz.configuration.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties("force.quiz.validation.answer")
class AnswerValidationProperties {
    var minAnswerLength = 1
    var maxAnswerLength = 2048
}
