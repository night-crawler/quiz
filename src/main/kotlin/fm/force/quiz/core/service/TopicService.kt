package fm.force.quiz.core.service

import am.ik.yavi.builder.ValidatorBuilder
import am.ik.yavi.builder.konstraint
import fm.force.quiz.configuration.properties.TopicValidationProperties
import fm.force.quiz.core.dto.*
import fm.force.quiz.core.entity.Topic
import fm.force.quiz.core.entity.Topic_
import fm.force.quiz.core.exception.ValidationError
import fm.force.quiz.core.repository.JpaTopicRepository
import fm.force.quiz.security.service.AuthenticationFacade
import org.springframework.data.domain.Page
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Service
import java.time.Instant

@Service
class TopicService(
        private val validationProps: TopicValidationProperties,
        jpaTopicRepository: JpaTopicRepository,
        paginationService: PaginationService,
        sortingService: SortingService,
        authenticationFacade: AuthenticationFacade
) : AbstractPaginatedCRUDService<Topic, JpaTopicRepository, PatchTopicDTO, TopicDTO> (
        repository = jpaTopicRepository,
        authenticationFacade = authenticationFacade,
        sortingService = sortingService,
        paginationService = paginationService
) {
    var validator = ValidatorBuilder.of<Topic>()
            .konstraint(Topic::title) {
                greaterThanOrEqual(validationProps.minTitleLength)
                        .message("Topic title must be at least ${validationProps.minTitleLength} characters long")

                lessThanOrEqual(validationProps.maxTitleLength)
                        .message("Topic title must be maximum ${validationProps.maxTitleLength} characters long")
            }
            .build()

    fun validate(topic: Topic) = validator.validate(topic).throwIfInvalid { ValidationError(it) }

    override fun create(createDTO: PatchTopicDTO): Topic {
        val topic = Topic(
                owner = authenticationFacade.user,
                title = createDTO.title
        )
        validate(topic)
        return repository.save(topic)
    }

    override fun buildSingleArgumentSearchSpec(needle: String?): Specification<Topic> {
        if (needle.isNullOrEmpty())
            return emptySpecification

        val lowerCaseNeedle = needle.toLowerCase()

        val titleContains = Specification<Topic> { root, _, builder ->
            builder.like(builder.lower(root[Topic_.title]), "%$lowerCaseNeedle%") }

        val ownerEquals = Specification<Topic> { root, _, builder ->
            builder.equal(root[Topic_.owner], authenticationFacade.user) }

        return Specification.where(titleContains).and(ownerEquals)
    }

    override fun patch(id: Long, patchDTO: PatchTopicDTO): Topic {
        val topic = getInstance(id)
        topic.title = patchDTO.title
        topic.updatedAt = Instant.now()
        validate(topic)
        return repository.save(topic)
    }

    override fun serializePage(page: Page<Topic>): PageDTO = page.toDTO { it.toDTO() }
    override fun serializeEntity(entity: Topic): TopicDTO = entity.toDTO()
}
