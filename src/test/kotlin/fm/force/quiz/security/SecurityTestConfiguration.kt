package io.kotlintest.provided.fm.force.quiz.security

import io.kotlintest.provided.fm.force.quiz.security.YamlPropertyLoaderFactory
import org.springframework.boot.test.context.ConfigFileApplicationContextInitializer
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.PropertySource
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer
import org.springframework.test.context.ContextConfiguration


@TestConfiguration
@PropertySource("classpath:application-test.yaml", factory = YamlPropertyLoaderFactory::class)
@ComponentScan("fm.force.quiz")
@ContextConfiguration(initializers=[ConfigFileApplicationContextInitializer::class])
class SecurityTestConfiguration {
    companion object {
        @Bean
        fun propertiesResolver(): PropertySourcesPlaceholderConfigurer {
            return PropertySourcesPlaceholderConfigurer()
        }
    }
}
