package fm.force.quiz.core.service

import fm.force.quiz.common.getRandomString
import fm.force.quiz.configuration.properties.TopicValidationProperties
import fm.force.quiz.core.dto.PaginationQuery
import fm.force.quiz.core.dto.SearchQueryDTO
import fm.force.quiz.core.dto.SortQuery
import fm.force.quiz.core.dto.TopicPatchDTO
import fm.force.quiz.core.exception.NotFoundException
import fm.force.quiz.core.exception.ValidationError
import io.kotlintest.data.forall
import io.kotlintest.matchers.collections.shouldHaveSize
import io.kotlintest.shouldBe
import io.kotlintest.shouldThrow
import io.kotlintest.tables.row

open class TopicServiceTest(
    validationProps: TopicValidationProperties,
    topicService: TopicService
) : AbstractCRUDServiceTest() {
    init {
        "should validate topic title length" {
            forall(
                row(TopicPatchDTO(getRandomString(validationProps.minTitleLength - 1))),
                row(TopicPatchDTO(getRandomString(validationProps.maxTitleLength + 1)))
            ) {
                shouldThrow<ValidationError> { topicService.create(it) }
            }
        }

        "users must access only their own topics" {
            shouldThrow<NotFoundException> {
                topicService.getOwnedEntity(testDataFactory.getTopic(owner = alien).id)
            }
        }

        "should return pagination response" {
            (1..5).map { testDataFactory.getTopic(owner = user, title = "123-contains-$it") }
            (1..5).map { testDataFactory.getTopic(owner = alien, title = "123-contains-$it") }

            val page = topicService.find(
                PaginationQuery.default(),
                SortQuery.byIdDesc(),
                SearchQueryDTO("conT")
            )
            page.content shouldHaveSize 5
        }

        "should get or create topic by name" {
            val (_, created) = topicService.getOrCreate(TopicPatchDTO("random-100500"))
            created shouldBe true

            val (_, dupCreated) = topicService.getOrCreate(TopicPatchDTO("random-100500"))
            dupCreated shouldBe false
        }
    }
}
