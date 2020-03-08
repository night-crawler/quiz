package fm.force.quiz.configuration

import fm.force.quiz.core.dto.ErrorMessage
import fm.force.quiz.core.dto.FieldError
import fm.force.quiz.core.dto.GenericError
import kotlinx.serialization.UnstableDefault
import kotlinx.serialization.json.JsonConfiguration
import kotlinx.serialization.modules.SerializersModule
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@UnstableDefault
class KotlinSerializationConfiguration {
    @Bean
    fun kotlinJsonConfiguration() = JsonConfiguration(
        encodeDefaults = true,
        ignoreUnknownKeys = true,
        isLenient = false,
        serializeSpecialFloatingPointValues = false,
        allowStructuredMapKeys = true,
        prettyPrint = false,
        unquotedPrint = false,
        indent = "    ",
        useArrayPolymorphism = false,
        classDiscriminator = "@type"
    )

    @Bean
    fun serializersModule() = SerializersModule {
        polymorphic(GenericError::class) {
            ErrorMessage::class with ErrorMessage.serializer()
            FieldError::class with FieldError.serializer()
        }
    }
}
