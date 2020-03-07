package fm.force.quiz.security.configuration.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

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
    class CorsConfigurationWrapper : CorsConfiguration() {
        lateinit var path: String
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

    val source get() = UrlBasedCorsConfigurationSource().apply {
        configs.map { registerCorsConfiguration(it.path, it) }
    }
}
