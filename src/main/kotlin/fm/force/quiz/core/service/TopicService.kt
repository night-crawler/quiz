package fm.force.quiz.core.service

import am.ik.yavi.builder.ValidatorBuilder
import fm.force.quiz.common.SpecificationBuilder
import fm.force.quiz.common.dto.PageDTO
import fm.force.quiz.common.dto.TopicFullDTO
import fm.force.quiz.common.dto.TopicPatchDTO
import fm.force.quiz.common.dto.TopicSearchQueryDTO
import fm.force.quiz.common.mapper.toDTO
import fm.force.quiz.common.mapper.toFullDTO
import fm.force.quiz.configuration.properties.TopicValidationProperties
import fm.force.quiz.core.entity.Tag
import fm.force.quiz.core.entity.Tag_
import fm.force.quiz.core.entity.Topic
import fm.force.quiz.core.entity.Topic_
import fm.force.quiz.core.exception.NotFoundException
import fm.force.quiz.core.repository.TopicRepository
import fm.force.quiz.core.validator.stringConstraint
import java.time.Instant
import org.springframework.data.domain.Page
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

val TopicSearchQueryDTO.cleanedSlugs: List<String>
    get() = when {
        slugs == null -> listOf()
        slugs.size > 10 -> listOf()
        else -> slugs.map { it.trim() }.filterNot { it.isEmpty() }
    }

@Service
class TopicService(
    validationProps: TopicValidationProperties,
    private val topicRepository: TopicRepository,
    private val slugifyService: SlugifyService
) : TopicServiceType(repository = topicRepository) {
    override var entityValidator = ValidatorBuilder.of<Topic>()
        .stringConstraint(Topic::title, validationProps.minTitleLength..validationProps.maxTitleLength)
        .build()

    override fun create(createDTO: TopicPatchDTO): Topic {
        val topic = Topic(
            owner = authenticationFacade.user,
            title = createDTO.title,
            slug = slugifyService.slugify(createDTO.title)
        )
        validateEntity(topic)
        return repository.save(topic)
    }

    override fun buildSearchSpec(search: TopicSearchQueryDTO?): Specification<Topic> {
        var spec = SpecificationBuilder.fk(authenticationFacade::user, Topic_.owner)
        if (search == null)
            return spec

        if (!search.query.isNullOrEmpty()) {
            spec = spec.and(SpecificationBuilder.ciContains(search.query, Topic_.title))!!
        }

        val cleanedSlugs = search.cleanedSlugs
        if (cleanedSlugs.isNotEmpty()) {
            val slugsSpec = Specification<Topic> { root, _, _ ->
                root[Topic_.slug].`in`(cleanedSlugs.map { it.toLowerCase() })
            }
            spec = spec.and(slugsSpec)!!
        }

        return spec
    }

    @Transactional
    override fun patch(id: Long, patchDTO: TopicPatchDTO): Topic {
        val topic = getOwnedEntity(id).apply {
            title = patchDTO.title
            updatedAt = Instant.now()
            slug = slugifyService.slugify(patchDTO.title)
        }
        validateEntity(topic)
        return repository.save(topic)
    }

    fun getByTitle(patchDTO: TopicPatchDTO): Topic =
        topicRepository
            .findByTitleAndOwner(patchDTO.title, authenticationFacade.user)
            .orElseThrow { NotFoundException(patchDTO.title, this::class) }

    @Transactional
    fun getOrCreate(patchDTO: TopicPatchDTO): Pair<Topic, Boolean> = try {
        getByTitle(patchDTO) to false
    } catch (ex: NotFoundException) {
        create(patchDTO) to true
    }

    @Transactional(readOnly = true)
    override fun serializePage(page: Page<Topic>): PageDTO = page.toDTO { it.toFullDTO() }

    @Transactional(readOnly = true)
    override fun serializeEntity(entity: Topic): TopicFullDTO =
        repository.refresh(entity).toFullDTO()
}
