package io.kotlintest.provided.fm.force.quiz.core.service

import fm.force.quiz.core.service.QuestionService
import io.kotlintest.provided.fm.force.quiz.TestConfiguration
import io.kotlintest.provided.fm.force.quiz.YamlPropertyLoaderFactory
import io.kotlintest.specs.StringSpec
import org.springframework.context.annotation.PropertySource
import org.springframework.test.context.ContextConfiguration

@PropertySource("classpath:application-test.yaml", factory = YamlPropertyLoaderFactory::class)
@ContextConfiguration(classes = [TestConfiguration::class])
class QuestionServiceTest(
        questionService: QuestionService
) : StringSpec() {

    init {
        "should validate" {
        }
    }

}