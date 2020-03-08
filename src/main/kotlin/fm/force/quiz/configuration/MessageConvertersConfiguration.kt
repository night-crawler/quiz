package fm.force.quiz.configuration

import fm.force.quiz.common.http.converter.KotlinSerializationJsonHttpMessageConverter
import kotlinx.serialization.json.JsonConfiguration
import kotlinx.serialization.modules.SerialModule
import org.springframework.context.annotation.Configuration
import org.springframework.http.converter.HttpMessageConverter
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class MessageConvertersConfiguration(
    private val jsonConfiguration: JsonConfiguration,
    private val serialModule: SerialModule
) : WebMvcConfigurer {

    override fun extendMessageConverters(converters: MutableList<HttpMessageConverter<*>>) {
        converters.add(0, KotlinSerializationJsonHttpMessageConverter(jsonConfiguration, serialModule))
    }
}
