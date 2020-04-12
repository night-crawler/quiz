package fm.force.quiz.configuration

import fm.force.quiz.common.serializer.QuizJson
import kotlinx.serialization.UnstableDefault
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@UnstableDefault
class KotlinSerializationConfiguration {
    @Bean
    fun kotlinXJson() = QuizJson.jsonX
}
