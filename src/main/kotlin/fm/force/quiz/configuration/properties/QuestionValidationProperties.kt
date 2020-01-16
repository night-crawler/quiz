package fm.force.quiz.configuration.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "force.quiz.validation.question")
class QuestionValidationProperties {
    var minTextLength: Int = 5
    var maxAnswers: Int = 10
    var maxTags: Int = 20
    var maxTopics: Int = 20
}
