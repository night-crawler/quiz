package fm.force.quiz.common.http.converter

import kotlinx.serialization.KSerializer
import kotlinx.serialization.serializerByTypeToken
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class KotlinSerializationResolver {
    val logger: Logger = LoggerFactory.getLogger(this::class.java)

    @Synchronized
    fun resolve(type: Class<*>): KSerializer<Any> {
        RESOLVER_CACHE[type]?.let { return it }

        val resolvedSerializer = try {
            serializerByTypeToken(type)
        } catch (exc: Exception) {
            if (MISSES_CACHE[type] == null) {
                logger.debug("[ø] Serializer was not found for: $type; exception: $exc")
                MISSES_CACHE[type] = 0
            }

            MISSES_CACHE.computeIfPresent(type) { _, v -> v + 1 }
            throw exc
        }
        RESOLVER_CACHE[type] = resolvedSerializer
        return resolvedSerializer
    }

    companion object {
        private val RESOLVER_CACHE = mutableMapOf<Class<*>, KSerializer<Any>>()
        private val MISSES_CACHE = mutableMapOf<Class<*>, Int>()
    }
}
