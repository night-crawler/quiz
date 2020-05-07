package fm.force.quiz.configuration

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class YamlObjectMapperConfiguration {
    @Bean
    fun yamlObjectMapper() = ObjectMapper(YAMLFactory()).also {
        it.findAndRegisterModules()
    }
}
