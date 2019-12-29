package fm.force.quiz.configuration.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "force.quiz.validation.quiz")
class QuizValidationProperties {
    val minTitleLength: Int = 3
    val maxTitleLength: Int = 150
    val maxQuestions: Int = Int.MAX_VALUE
    val maxTags: Int = 20
    val maxTopics: Int = 20
}
