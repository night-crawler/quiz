package fm.force.quiz.security.configuration.properties

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import javax.annotation.PostConstruct

/**
 * To enable custom configurations for different sets of paths and allowed origins,
 * add this block to the yaml configuration:
 * ```
 * cors:
 *   configs:
 *     - path: "*&#47;*"
 *       allowedOrigins: "http://localhost:3001"
 *       allowedMethods: "*"
 *       allowedHeaders: "*"
 *       allowCredentials: true
 * ```
 */
@Configuration
@ConfigurationProperties("force.security.cors")
class CorsConfigurationProperties {
    val logger: Logger = LoggerFactory.getLogger(this::class.java)

    class CorsConfigurationWrapper : CorsConfiguration() {
        lateinit var path: String

        override fun toString(): String = "CorsConfigurationWrapper(" +
            "path=$path " +
            "allowedOrigins=$allowedOrigins " +
            "allowedMethods=$allowedMethods " +
            "allowedHeaders=$allowedHeaders " +
            "allowCredentials=$allowCredentials)"
    }

    // add a reasonable default for development purposes
    var configs: List<CorsConfigurationWrapper> = listOf(
        CorsConfigurationWrapper().apply {
            path = "/**"
            allowedOrigins = listOf("http://localhost:3001")
            allowedMethods = listOf(CorsConfiguration.ALL)
            allowedHeaders = listOf(CorsConfiguration.ALL)
            allowCredentials = true
        }
    )

    val source
        get() = UrlBasedCorsConfigurationSource().apply {
            configs.map { registerCorsConfiguration(it.path, it) }
        }

    @PostConstruct
    fun reportConfiguration() {
        configs.forEach {
            logger.info("[ø] Loaded CORS: $it")
        }
    }
}
