package fm.force.quiz.configuration.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration


@Configuration
@ConfigurationProperties("force.quiz.validation.difficultyscale")
class DifficultyScaleValidationProperties {
    var allowedMax = Int.MAX_VALUE
    var maxNameLength = 80
    var minNameLength = 3
}
