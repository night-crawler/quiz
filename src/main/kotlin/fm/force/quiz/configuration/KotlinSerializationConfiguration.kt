package fm.force.quiz.configuration

import fm.force.quiz.core.dto.*
import java.time.Instant
import kotlinx.serialization.UnstableDefault
import kotlinx.serialization.json.Json
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
        isLenient = true,
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
        polymorphic(DTOMarker::class) {
            AnswerFullDTO::class with AnswerFullDTO.serializer()
            AnswerRestrictedDTO::class with AnswerRestrictedDTO.serializer()
            AnswerPatchDTO::class with AnswerPatchDTO.serializer()

            DifficultyScaleFullDTO::class with DifficultyScaleFullDTO.serializer()
            DifficultyScaleRestrictedDTO::class with DifficultyScaleRestrictedDTO.serializer()
            DifficultyScalePatchDTO::class with DifficultyScalePatchDTO.serializer()

            DifficultyScaleRangeFullDTO::class with DifficultyScaleRangeFullDTO.serializer()
            DifficultyScaleRangeRestrictedDTO::class with DifficultyScaleRangeRestrictedDTO.serializer()
            DifficultyScaleRangePatchDTO::class with DifficultyScaleRangePatchDTO.serializer()

            QuestionFullDTO::class with QuestionFullDTO.serializer()
            QuestionRestrictedDTO::class with QuestionRestrictedDTO.serializer()
            QuestionPatchDTO::class with QuestionPatchDTO.serializer()

            QuizFullDTO::class with QuizFullDTO.serializer()
            QuizRestrictedDTO::class with QuizRestrictedDTO.serializer()
            QuizPatchDTO::class with QuizPatchDTO.serializer()

            QuizQuestionFullDTO::class with QuizQuestionFullDTO.serializer()
            QuizQuestionRestrictedDTO::class with QuizQuestionRestrictedDTO.serializer()
            QuizQuestionPatchDTO::class with QuizQuestionPatchDTO.serializer()

            QuizSessionFullDTO::class with QuizSessionFullDTO.serializer()
            QuizSessionPatchDTO::class with QuizSessionPatchDTO.serializer()

            QuizSessionAnswerFullDTO::class with QuizSessionAnswerFullDTO.serializer()
            QuizSessionAnswerRestrictedDTO::class with QuizSessionAnswerRestrictedDTO.serializer()
            QuizSessionAnswerPatchDTO::class with QuizSessionAnswerPatchDTO.serializer()

            QuizSessionQuestionFullDTO::class with QuizSessionQuestionFullDTO.serializer()
            QuizSessionQuestionRestrictedDTO::class with QuizSessionQuestionRestrictedDTO.serializer()
            QuizSessionQuestionPatchDTO::class with QuizSessionQuestionPatchDTO.serializer()

            QuizSessionQuestionAnswerFullDTO::class with QuizSessionQuestionAnswerFullDTO.serializer()
            QuizSessionQuestionAnswerRestrictedDTO::class with QuizSessionQuestionAnswerRestrictedDTO.serializer()

            TagFullDTO::class with TagFullDTO.serializer()
            TagRestrictedDTO::class with TagRestrictedDTO.serializer()
            TagPatchDTO::class with TagPatchDTO.serializer()

            TopicFullDTO::class with TopicFullDTO.serializer()
            TopicRestrictedDTO::class with TopicRestrictedDTO.serializer()
            TopicPatchDTO::class with TopicPatchDTO.serializer()
        }
        contextual(Instant::class, InstantSerializer)
        contextual(Long::class, LongAsStringSerializer)
    }

    @Bean
    fun kotlinXJson() = Json(kotlinJsonConfiguration(), serializersModule())
}
