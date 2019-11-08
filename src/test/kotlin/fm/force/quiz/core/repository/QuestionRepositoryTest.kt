package io.kotlintest.provided.fm.force.quiz.core.repository

import fm.force.quiz.core.entity.Answer
import fm.force.quiz.core.entity.Question
import fm.force.quiz.core.repository.JpaAnswerRepository
import fm.force.quiz.core.repository.JpaQuestionRepository
import io.kotlintest.provided.fm.force.quiz.TestConfiguration
import io.kotlintest.provided.fm.force.quiz.YamlPropertyLoaderFactory
import io.kotlintest.specs.StringSpec
import org.springframework.context.annotation.PropertySource
import org.springframework.test.context.ContextConfiguration

@PropertySource("classpath:application-test.yaml", factory = YamlPropertyLoaderFactory::class)
@ContextConfiguration(classes = [TestConfiguration::class])
open class QuestionRepositoryTest(
        jpaQuestionRepository: JpaQuestionRepository,
        jpaAnswerRepository: JpaAnswerRepository
) : StringSpec() {

    init {
        "should create questions" {
            val answer = Answer(text = "answer")
            val question = Question(text = "question", answers = setOf(answer))
            jpaAnswerRepository.save(answer)
            jpaQuestionRepository.save(question)
        }
    }

}