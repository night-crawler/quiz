package fm.force.quiz.core.service

import am.ik.yavi.builder.ValidatorBuilder
import fm.force.quiz.common.SpecificationBuilder
import fm.force.quiz.configuration.properties.TopicValidationProperties
import fm.force.quiz.core.dto.*
import fm.force.quiz.core.entity.Topic
import fm.force.quiz.core.entity.Topic_
import fm.force.quiz.core.repository.JpaTopicRepository
import fm.force.quiz.core.validator.stringConstraint
import fm.force.quiz.security.service.AuthenticationFacade
import org.springframework.data.domain.Page
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Service
import java.time.Instant

@Service
class TopicService(
        validationProps: TopicValidationProperties,
        jpaTopicRepository: JpaTopicRepository,
        paginationService: PaginationService,
        sortingService: SortingService,
        authenticationFacade: AuthenticationFacade
) : AbstractPaginatedCRUDService<Topic, JpaTopicRepository, TopicPatchDTO, TopicFullDTO>(
        repository = jpaTopicRepository,
        authenticationFacade = authenticationFacade,
        sortingService = sortingService,
        paginationService = paginationService
) {
    override var entityValidator = ValidatorBuilder.of<Topic>()
            .stringConstraint(Topic::title, validationProps.minTitleLength..validationProps.maxTitleLength)
            .build()

    override fun create(createDTO: TopicPatchDTO): Topic {
        val topic = Topic(
                owner = authenticationFacade.user,
                title = createDTO.title
        )
        validateEntity(topic)
        return repository.save(topic)
    }

    override fun buildSingleArgumentSearchSpec(needle: String?): Specification<Topic> {
        if (needle.isNullOrEmpty())
            return emptySpecification

        return Specification
                .where(SpecificationBuilder.fk(authenticationFacade::user, Topic_.owner))
                .and(SpecificationBuilder.ciContains(needle, Topic_.title))
    }

    override fun patch(id: Long, patchDTO: TopicPatchDTO): Topic {
        val topic = getInstance(id)
        topic.title = patchDTO.title
        topic.updatedAt = Instant.now()
        validateEntity(topic)
        return repository.save(topic)
    }

    override fun serializePage(page: Page<Topic>): PageDTO = page.toDTO { it.toFullDTO() }
    override fun serializeEntity(entity: Topic): TopicFullDTO = entity.toFullDTO()
}
