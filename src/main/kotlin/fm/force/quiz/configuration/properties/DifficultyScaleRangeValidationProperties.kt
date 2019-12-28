package fm.force.quiz.configuration.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration


@Configuration
@ConfigurationProperties("force.quiz.validation.difficultyscalerange")
class DifficultyScaleRangeValidationProperties {
    var minUpper = Int.MAX_VALUE

    var maxUpper = Int.MAX_VALUE

    var minTitleLength = 3
    var maxTitleLength = 100
}
