package fm.force.quiz.configuration

import fm.force.quiz.interceptor.SampleInterceptor
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

/**
 * NOTE: @EnableWebMvc makes here a mess with initialization
 * Note: only one @Configuration class may have the @EnableWebMvc annotation to import the Spring Web MVC configuration.
 * There can however be multiple @Configuration classes implementing WebMvcConfigurer in order to customize the provided
 * configuration.
 */
@Configuration
class SampleInterceptorConfiguration : WebMvcConfigurer {
    @Autowired
    lateinit var sampleInterceptor: SampleInterceptor

    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(sampleInterceptor)
    }
}
