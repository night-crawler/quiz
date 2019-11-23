package io.kotlintest.provided.fm.force.quiz.core.service

import fm.force.quiz.core.service.QuestionValidationService
import io.kotlintest.provided.fm.force.quiz.TestConfiguration
import io.kotlintest.provided.fm.force.quiz.YamlPropertyLoaderFactory
import io.kotlintest.specs.StringSpec
import org.springframework.context.annotation.PropertySource
import org.springframework.test.context.ContextConfiguration

@PropertySource("classpath:application-test.yaml", factory = YamlPropertyLoaderFactory::class)
@ContextConfiguration(classes = [TestConfiguration::class])
class QuestionValidationServiceTest(
        questionValidationService: QuestionValidationService
) : StringSpec() {

    init {
        "should validate" {
        }
    }

}