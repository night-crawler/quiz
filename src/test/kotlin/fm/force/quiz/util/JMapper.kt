package io.kotlintest.provided.fm.force.quiz.util

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue


class JMapper {
    companion object {
        val mapper by lazy {
            jacksonObjectMapper().also { it.findAndRegisterModules() }
        }

        inline fun <reified T> fromString(raw: String) = mapper.readValue<T>(raw)

        fun writeValueAsString(dto: Any): String = mapper.writeValueAsString(dto)

        fun pformat(input: String): String? = try {
            val json: Any = mapper.readValue(input, Any::class.java)
            mapper.writerWithDefaultPrettyPrinter().writeValueAsString(json)
        } catch (exc: JsonProcessingException) {
            input
        }

        inline fun <reified K, reified V>toMap(input: String) = mapper.readValue<Map<K, V>>(input)
    }
}
