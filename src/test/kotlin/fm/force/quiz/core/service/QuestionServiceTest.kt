package io.kotlintest.provided.fm.force.quiz.core.service

import fm.force.quiz.core.dto.CreateQuestionDTO
import fm.force.quiz.core.entity.Answer
import fm.force.quiz.core.entity.Tag
import fm.force.quiz.core.entity.Topic
import fm.force.quiz.core.exception.ValidationError
import fm.force.quiz.core.repository.JpaAnswerRepository
import fm.force.quiz.core.repository.JpaTagRepository
import fm.force.quiz.core.repository.JpaTopicRepository
import fm.force.quiz.core.service.QuestionService
import fm.force.quiz.security.entity.User
import fm.force.quiz.security.repository.JpaUserRepository
import fm.force.quiz.security.service.AuthenticationFacade
import fm.force.quiz.security.service.JwtUserDetailsFactoryService
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
        questionService: QuestionService,
        jpaUserRepository: JpaUserRepository,
        jwtUserDetailsFactoryService: JwtUserDetailsFactoryService,
        jpaAnswerRepository: JpaAnswerRepository,
        jpaTagRepository: JpaTagRepository,
        jpaTopicRepository: JpaTopicRepository
) : StringSpec() {

    @MockBean
    lateinit var authFacade: AuthenticationFacade

    private val user = jpaUserRepository.save(User(username = "user@example.com", email = "user@example.com"))
    private val answer = jpaAnswerRepository.save(Answer(text ="sample text", owner = user))
    private val tag = jpaTagRepository.save(Tag(owner = user, name = "sample", slug = "sample"))
    private val topic = jpaTopicRepository.save(Topic(owner = user, title = "sample topic"))

    init {
        "should validate" {
            Mockito.`when`(authFacade.principal).thenReturn(jwtUserDetailsFactoryService.createUserDetails(user))

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