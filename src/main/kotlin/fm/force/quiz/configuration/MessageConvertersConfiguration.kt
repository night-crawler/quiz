package fm.force.quiz.configuration

import fm.force.quiz.common.http.converter.KotlinSerializationJsonHttpMessageConverter
import kotlinx.serialization.json.Json
import org.springframework.context.annotation.Configuration
import org.springframework.http.converter.HttpMessageConverter
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class MessageConvertersConfiguration(private val json: Json) : WebMvcConfigurer {
    override fun extendMessageConverters(converters: MutableList<HttpMessageConverter<*>>) {
        converters.add(0, KotlinSerializationJsonHttpMessageConverter(json))
    }
}
