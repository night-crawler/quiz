package io.kotlintest.provided.fm.force.quiz.core.service

import fm.force.quiz.core.dto.CreateQuestionDTO
import fm.force.quiz.core.entity.Answer
import fm.force.quiz.core.entity.Tag
import fm.force.quiz.core.entity.Topic
import fm.force.quiz.core.exception.ValidationError
import fm.force.quiz.core.service.QuestionService
import fm.force.quiz.factory.TestDataFactory
import fm.force.quiz.security.entity.User
import fm.force.quiz.security.service.AuthenticationFacade
import fm.force.quiz.security.service.JwtUserDetailsFactoryService
import io.kotlintest.TestCase
import io.kotlintest.data.forall
import io.kotlintest.provided.fm.force.quiz.TestConfiguration
import io.kotlintest.provided.fm.force.quiz.YamlPropertyLoaderFactory
import io.kotlintest.shouldThrow
import io.kotlintest.specs.StringSpec
import io.kotlintest.tables.row
import org.mockito.Mockito
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.PropertySource
import org.springframework.test.context.ContextConfiguration

@PropertySource("classpath:application-test.yaml", factory = YamlPropertyLoaderFactory::class)
@ContextConfiguration(classes = [TestConfiguration::class])
open class QuestionServiceTest(
        private val testDataFactory: TestDataFactory,
        private val jwtUserDetailsFactoryService: JwtUserDetailsFactoryService,
        questionService: QuestionService
) : StringSpec() {

    @MockBean
    lateinit var authFacade: AuthenticationFacade

    private lateinit var user: User
    private lateinit var answer: Answer
    private lateinit var tag: Tag
    private lateinit var topic: Topic

    override fun beforeTest(testCase: TestCase) {
        user = testDataFactory.getUser()
        answer = testDataFactory.getAnswer(owner = user)
        tag = testDataFactory.getTag(owner = user)
        topic = testDataFactory.getTopic(owner = user)
        Mockito.`when`(authFacade.principal).thenReturn(jwtUserDetailsFactoryService.createUserDetails(user))
    }

    init {
        "should validate" {

            forall(
                    row(CreateQuestionDTO("sample", setOf(1, 2), setOf(3, 4), setOf(5), setOf(6))),
                    row(CreateQuestionDTO("", setOf(1, 22), setOf(1, 33), setOf(44), setOf(66))),
                    row(CreateQuestionDTO("", emptySet(), emptySet(), emptySet(), emptySet()))
            ) {
                shouldThrow<ValidationError> { questionService.validate(it) }
            }

            forall(
                    row(CreateQuestionDTO(
                            text = "valid sample",
                            answers = setOf(answer.id!!), correctAnswers = setOf(answer.id!!),
                            tags = emptySet(), topics = emptySet())),
                    row(CreateQuestionDTO(
                            text = "valid sample",
                            answers = setOf(answer.id!!), correctAnswers = setOf(answer.id!!),
                            tags = setOf(tag.id!!), topics = setOf(topic.id!!)))
            ) {
                questionService.validate(it)
            }

        }
    }
}