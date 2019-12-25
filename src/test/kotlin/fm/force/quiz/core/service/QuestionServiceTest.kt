package io.kotlintest.provided.fm.force.quiz.core.service

import fm.force.quiz.core.dto.CreateQuestionDTO
import fm.force.quiz.core.dto.PaginationQuery
import fm.force.quiz.core.dto.SortQuery
import fm.force.quiz.core.entity.Answer
import fm.force.quiz.core.entity.Tag
import fm.force.quiz.core.entity.Topic
import fm.force.quiz.core.exception.ValidationError
import fm.force.quiz.core.service.QuestionService
import fm.force.quiz.factory.TestDataFactory
import fm.force.quiz.security.service.JwtUserDetailsFactoryService
import io.kotlintest.TestCase
import io.kotlintest.data.forall
import io.kotlintest.matchers.collections.shouldHaveSize
import io.kotlintest.provided.fm.force.quiz.TestConfiguration
import io.kotlintest.provided.fm.force.quiz.YamlPropertyLoaderFactory
import io.kotlintest.shouldThrow
import io.kotlintest.tables.row
import org.springframework.context.annotation.PropertySource
import org.springframework.test.context.ContextConfiguration

@PropertySource("classpath:application-test.yaml", factory = YamlPropertyLoaderFactory::class)
@ContextConfiguration(classes = [TestConfiguration::class])
open class QuestionServiceTest(
        testDataFactory: TestDataFactory,
        jwtUserDetailsFactoryService: JwtUserDetailsFactoryService,
        questionService: QuestionService
) : GenericCRUDServiceTest(testDataFactory = testDataFactory, jwtUserDetailsFactoryService = jwtUserDetailsFactoryService) {
    private lateinit var answer: Answer
    private lateinit var tag: Tag
    private lateinit var topic: Topic

    override fun beforeTest(testCase: TestCase) {
        super.beforeTest(testCase)
        answer = testDataFactory.getAnswer(owner = user)
        tag = testDataFactory.getTag(owner = user)
        topic = testDataFactory.getTopic(owner = user)
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
                            answers = setOf(answer.id), correctAnswers = setOf(answer.id),
                            tags = emptySet(), topics = emptySet())),
                    row(CreateQuestionDTO(
                            text = "valid sample",
                            answers = setOf(answer.id), correctAnswers = setOf(answer.id),
                            tags = setOf(tag.id), topics = setOf(topic.id)))
            ) {
                questionService.validate(it)
            }
        }

        "should paginate" {
            (1..5).map { testDataFactory.getQuestion(owner = user, text = "Surprise question $it") }
            (1..5).map { testDataFactory.getQuestion(owner = alien, text = "Surprise alien question $it") }

            val page = questionService.find(PaginationQuery.default(), SortQuery.byIdDesc(), "SurPRise")
            page.content shouldHaveSize 5
        }
    }
}
