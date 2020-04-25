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

    val textRange get() = minTextLength..Int.MAX_VALUE
    val answersRange get() = 1..maxAnswers
    val tagsRange get() = 0..maxTags
    val topicsRange get() = 0..maxTopics
    val helpRange get() = 0..Int.MAX_VALUE
}
