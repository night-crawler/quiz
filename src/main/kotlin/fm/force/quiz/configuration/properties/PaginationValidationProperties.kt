package fm.force.quiz.configuration.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "force.quiz.validation.pagination")
class PaginationValidationProperties {
    var minPageSize: Int = 25
    var maxPageSize: Int = 100
}
