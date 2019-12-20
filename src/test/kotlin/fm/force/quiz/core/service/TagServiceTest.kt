package io.kotlintest.provided.fm.force.quiz.core.service

import fm.force.quiz.common.getRandomString
import fm.force.quiz.configuration.properties.TagValidationProperties
import fm.force.quiz.core.dto.CreateTagDTO
import fm.force.quiz.core.entity.Tag
import fm.force.quiz.core.exception.NotFoundException
import fm.force.quiz.core.exception.ValidationError
import fm.force.quiz.core.repository.JpaTagRepository
import fm.force.quiz.core.service.TagService
import fm.force.quiz.factory.TestDataFactory
import fm.force.quiz.security.entity.User
import fm.force.quiz.security.service.AuthenticationFacade
import fm.force.quiz.security.service.JwtUserDetailsFactoryService
import io.kotlintest.TestCase
import io.kotlintest.data.forall
import io.kotlintest.matchers.collections.shouldHaveSize
import io.kotlintest.provided.fm.force.quiz.TestConfiguration
import io.kotlintest.provided.fm.force.quiz.YamlPropertyLoaderFactory
import io.kotlintest.specs.StringSpec
import io.kotlintest.shouldBe
import io.kotlintest.shouldNotBe
import io.kotlintest.shouldThrow
import io.kotlintest.tables.row
import org.mockito.Mockito
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.PropertySource
import org.springframework.test.context.ContextConfiguration

@PropertySource("classpath:application-test.yaml", factory = YamlPropertyLoaderFactory::class)
@ContextConfiguration(classes = [TestConfiguration::class])
open class TagServiceTest(
        private val jwtUserDetailsFactoryService: JwtUserDetailsFactoryService,
        private val testDataFactory: TestDataFactory,
        jpaTagRepository: JpaTagRepository,
        tagService: TagService,
        validationProps: TagValidationProperties
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
        "should throw ValidationError on too short or too long tag names" {
            forall(
                    row(CreateTagDTO(getRandomString(validationProps.maxTagLength + 1))),
                    row(CreateTagDTO(getRandomString(validationProps.minTagLength - 1)))
            ) {
                shouldThrow<ValidationError> { tagService.create(it) }
            }
        }

        "should create a new tag" {
            val tag = tagService.create(CreateTagDTO(getRandomString(validationProps.minTagLength)))
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

            val spec = tagService.buildSearchSpec("prefix")
            val foundTags = jpaTagRepository.findAll(spec)
            foundTags shouldHaveSize 1
        }

        "users must not be able to access foreign tags by id" {
            val tag = jpaTagRepository.save(Tag(owner = alien, name = "charade", slug = "charade"))
            val ownTag = jpaTagRepository.save(Tag(owner = user, name = "all mine", slug = "all-mine"))

            shouldThrow<NotFoundException> { tagService.getTag(tag.id!!) }
            tagService.getTag(ownTag.id!!).id shouldNotBe null
        }
    }
}
