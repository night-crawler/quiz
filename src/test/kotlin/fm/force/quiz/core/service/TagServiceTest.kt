package fm.force.quiz.core.service

import fm.force.quiz.common.getRandomString
import fm.force.quiz.configuration.properties.TagValidationProperties
import fm.force.quiz.core.dto.PaginationQuery
import fm.force.quiz.core.dto.SearchQueryDTO
import fm.force.quiz.core.dto.SortQuery
import fm.force.quiz.core.dto.TagPatchDTO
import fm.force.quiz.core.entity.Tag
import fm.force.quiz.core.exception.NotFoundException
import fm.force.quiz.core.exception.ValidationError
import fm.force.quiz.core.repository.JpaTagRepository
import io.kotlintest.data.forall
import io.kotlintest.matchers.collections.shouldHaveSize
import io.kotlintest.shouldBe
import io.kotlintest.shouldNotBe
import io.kotlintest.shouldThrow
import io.kotlintest.tables.row

open class TagServiceTest(
        jpaTagRepository: JpaTagRepository,
        tagService: TagService,
        validationProps: TagValidationProperties
) : AbstractCRUDServiceTest() {

    init {
        "should throw ValidationError on too short or too long tag names" {
            forall(
                    row(TagPatchDTO(getRandomString(validationProps.maxTagLength + 1))),
                    row(TagPatchDTO(getRandomString(validationProps.minTagLength - 1)))
            ) {
                shouldThrow<ValidationError> { tagService.create(it) }
            }
        }

        "should create a new tag" {
            val tag = tagService.create(TagPatchDTO(getRandomString(validationProps.minTagLength)))
            tag.id shouldNotBe null
            tag.owner shouldBe user
        }

        "all users must have access to their own tags only" {
            jpaTagRepository.saveAll(listOf(
                    Tag(owner = alien, name = "prefix-alien-1", slug = "prefix-alien-1"),
                    Tag(owner = alien, name = "prefix-alien-2", slug = "prefix-alien-2"),
                    Tag(owner = alien, name = "prefix-alien-3", slug = "prefix-alien-3"),

                    Tag(owner = user, name = "prefix-user-1", slug = "prefix-user-1")
            ))

            val spec = tagService.buildSearchSpec(SearchQueryDTO("prefix"))
            val foundTags = jpaTagRepository.findAll(spec)
            foundTags shouldHaveSize 1
        }

        "users must not be able to access foreign tags by id" {
            val alienTag = jpaTagRepository.save(Tag(owner = alien, name = "charade", slug = "charade"))
            val ownTag = jpaTagRepository.save(Tag(owner = user, name = "all mine", slug = "all-mine"))

            shouldThrow<NotFoundException> { tagService.getOwnedEntity(alienTag.id) }
            tagService.getOwnedEntity(ownTag.id).id shouldNotBe null
        }

        "should return paginated find responses" {
            (1..5).map {
                testDataFactory.getTag(owner = user, name = "test-prefix-${it * 100}-suffix")
            }

            val p1 = tagService.find(
                    PaginationQuery(null, null),
                    SortQuery(listOf("-id")),
                    SearchQueryDTO("test-prefix")
            )
            p1.content shouldHaveSize 5
        }
    }
}
