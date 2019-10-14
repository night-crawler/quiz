package io.kotlintest.provided.fm.force.quiz.security.jwt

import fm.force.quiz.security.jwt.JwtTokenProvider
import io.kotlintest.provided.fm.force.quiz.security.YamlPropertyLoaderFactory
import org.springframework.boot.test.context.ConfigFileApplicationContextInitializer
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.PropertySource
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer
import org.springframework.test.context.ContextConfiguration


@TestConfiguration
@ComponentScan("fm.force.quiz")
@ContextConfiguration(initializers=[ConfigFileApplicationContextInitializer::class])
@PropertySource("classpath:application-test.yaml", factory = YamlPropertyLoaderFactory::class)
class JwtConfiguration {
    @Bean
    fun jwtTokenProvide() : JwtTokenProvider {
        return JwtTokenProvider()
    }

    @Bean
    fun propertiesResolver(): PropertySourcesPlaceholderConfigurer {
        return PropertySourcesPlaceholderConfigurer()
    }
}
