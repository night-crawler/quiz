package fm.force.quiz.common.http.converter

import java.nio.charset.StandardCharsets
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonDecodingException
import org.springframework.http.HttpInputMessage
import org.springframework.http.HttpOutputMessage
import org.springframework.http.MediaType
import org.springframework.http.converter.AbstractHttpMessageConverter
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.http.converter.HttpMessageNotWritableException
import org.springframework.lang.Nullable
import org.springframework.util.StreamUtils

/**
 * For more information visit this PR:
 * https://github.com/spring-projects/spring-framework/pull/24436/files
 */
class KotlinSerializationJsonHttpMessageConverter(
    private val json: Json
) : AbstractHttpMessageConverter<Any>(MediaType.APPLICATION_JSON, MediaType("application", "*+json")) {
    private val resolver: KotlinSerializationResolver = KotlinSerializationResolver()

    override fun supports(clazz: Class<*>) = try {
        resolver.resolve(clazz)
        true
    } catch (e: Exception) {
        false
    }

    override fun readInternal(
        clazz: Class<*>,
        inputMessage: HttpInputMessage
    ): Any {
        val contentType = inputMessage.headers.contentType
        val jsonText = StreamUtils.copyToString(inputMessage.body, getCharsetToUse(contentType)!!)
        return try {
            json.parse(resolver.resolve(clazz), jsonText)
        } catch (ex: JsonDecodingException) {
            throw HttpMessageNotReadableException("Could not read JSON: " + ex.message, ex, inputMessage)
        }
    }

    override fun writeInternal(
        o: Any,
        outputMessage: HttpOutputMessage
    ) {
        try {
            val json = json.stringify(resolver.resolve(o.javaClass), o)
            val contentType = outputMessage.headers.contentType
            outputMessage.body.write(json.toByteArray(getCharsetToUse(contentType)!!))
            outputMessage.body.flush()
        } catch (ex: Exception) {
            throw HttpMessageNotWritableException("Could not write JSON: " + ex.message, ex)
        }
    }

    private fun getCharsetToUse(@Nullable contentType: MediaType?) = contentType?.charset ?: DEFAULT_CHARSET

    companion object {
        private val DEFAULT_CHARSET = StandardCharsets.UTF_8
    }
}
