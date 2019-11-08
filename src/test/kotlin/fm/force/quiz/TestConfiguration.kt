package io.kotlintest.provided.fm.force.quiz

import io.kotlintest.provided.fm.force.quiz.YamlPropertyLoaderFactory
import org.springframework.boot.test.context.ConfigFileApplicationContextInitializer
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.PropertySource
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer
import org.springframework.test.context.ContextConfiguration


// Cannot have multiple configurations now for different app parts
// https://stackoverflow.com/questions/27440985/unable-to-register-mbean-hikaridatasource-hikaripool-0-with-key-datasource
// Maybe this one is a solution: @EnableMBeanExport(registration=RegistrationPolicy.IGNORE_EXISTING)
@TestConfiguration
@PropertySource("classpath:application-test.yaml", factory = YamlPropertyLoaderFactory::class)
@ComponentScan("fm.force.quiz")
@ContextConfiguration(initializers=[ConfigFileApplicationContextInitializer::class])
class TestConfiguration {
    companion object {
        @Bean
        fun propertiesResolver(): PropertySourcesPlaceholderConfigurer {
            return PropertySourcesPlaceholderConfigurer()
        }
    }
}
