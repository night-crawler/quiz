package fm.force.quiz.configuration

import fm.force.quiz.interceptor.SampleInterceptor
import org.springframework.stereotype.Component
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Component
class SampleInterceptorConfiguration(val sampleInterceptor: SampleInterceptor) : WebMvcConfigurer {
    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(sampleInterceptor)
    }
}
