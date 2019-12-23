package io.kotlintest.provided.fm.force.quiz.core.service

import fm.force.quiz.common.getRandomString
import fm.force.quiz.configuration.properties.TopicValidationProperties
import fm.force.quiz.core.dto.CreateTopicDTO
import fm.force.quiz.core.dto.PaginationQuery
import fm.force.quiz.core.dto.SortQuery
import fm.force.quiz.core.exception.NotFoundException
import fm.force.quiz.core.exception.ValidationError
import fm.force.quiz.core.service.TopicService
import fm.force.quiz.factory.TestDataFactory
import fm.force.quiz.security.entity.User
import fm.force.quiz.security.service.AuthenticationFacade
import fm.force.quiz.security.service.JwtUserDetailsFactoryService
import io.kotlintest.specs.StringSpec
import io.kotlintest.TestCase
import io.kotlintest.data.forall
import io.kotlintest.matchers.collections.shouldHaveSize
import io.kotlintest.provided.fm.force.quiz.TestConfiguration
import io.kotlintest.provided.fm.force.quiz.YamlPropertyLoaderFactory
import io.kotlintest.shouldThrow
import io.kotlintest.tables.row
import org.mockito.Mockito
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.PropertySource
import org.springframework.test.context.ContextConfiguration

@PropertySource("classpath:application-test.yaml", factory = YamlPropertyLoaderFactory::class)
@ContextConfiguration(classes = [TestConfiguration::class])
class TopicServiceTest(
        private val jwtUserDetailsFactoryService: JwtUserDetailsFactoryService,
        private val testDataFactory: TestDataFactory,
        validationProps: TopicValidationProperties,
        topicService: TopicService
) : StringSpec() {
    @MockBean
    private lateinit var authFacade: AuthenticationFacade
    private lateinit var user: User
    private lateinit var alien: User

    override fun beforeTest(testCase: TestCase) {
        user = testDataFactory.getUser()
        alien = testDataFactory.getUser()

        Mockito.`when`(authFacade.principal).thenReturn(jwtUserDetailsFactoryService.createUserDetails(user))
        Mockito.`when`(authFacade.user).thenReturn(user)
    }

    init {
        "should validate topic title length" {
            forall(
                    row(CreateTopicDTO(getRandomString(validationProps.minTitleLength - 1))),
                    row(CreateTopicDTO(getRandomString(validationProps.maxTitleLength + 1)))
            ) {
                shouldThrow<ValidationError> { topicService.create(it) }
            }
        }

        "users must access only their own topics" {
            shouldThrow<NotFoundException> {
                topicService.getInstance(testDataFactory.getTopic(owner = alien).id)
            }
        }

        "should return pagination response" {
            (1..5).map { testDataFactory.getTopic(owner = user, title = "123-contains-$it") }
            val page = topicService.find(
                    PaginationQuery.default(),
                    SortQuery.byIdDesc(),
                    "conT"
            )
            page.content shouldHaveSize 5
        }
    }
}
