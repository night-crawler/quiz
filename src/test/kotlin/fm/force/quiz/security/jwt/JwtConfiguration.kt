package io.kotlintest.provided.fm.force.quiz.security.jwt

import fm.force.quiz.common.getRandomString
import fm.force.quiz.security.entity.Role
import fm.force.quiz.security.entity.User
import io.kotlintest.provided.fm.force.quiz.security.YamlPropertyLoaderFactory
import org.springframework.boot.test.context.ConfigFileApplicationContextInitializer
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.PropertySource
import org.springframework.context.annotation.Scope
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer
import org.springframework.test.context.ContextConfiguration


@TestConfiguration
@PropertySource("classpath:application-test.yaml", factory = YamlPropertyLoaderFactory::class)
@ComponentScan("fm.force.quiz")
@ContextConfiguration(initializers=[ConfigFileApplicationContextInitializer::class])
class JwtConfiguration {
    companion object {
        // this stuff has to be static, otherwise it can be initialized not in time
//        @Bean
//        fun jwtTokenProvide() : JwtProvider {
//            return JwtProvider()
//        }
    }

    @Bean
    fun propertiesResolver(): PropertySourcesPlaceholderConfigurer {
        return PropertySourcesPlaceholderConfigurer()
    }
}
