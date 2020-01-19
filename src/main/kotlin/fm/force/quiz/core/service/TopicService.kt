package fm.force.quiz.core.service

import am.ik.yavi.builder.ValidatorBuilder
import fm.force.quiz.common.SpecificationBuilder
import fm.force.quiz.configuration.properties.TopicValidationProperties
import fm.force.quiz.core.dto.PageDTO
import fm.force.quiz.core.dto.SearchQueryDTO
import fm.force.quiz.core.dto.TopicFullDTO
import fm.force.quiz.core.dto.TopicPatchDTO
import fm.force.quiz.core.dto.toDTO
import fm.force.quiz.core.dto.toFullDTO
import fm.force.quiz.core.entity.Topic
import fm.force.quiz.core.entity.Topic_
import fm.force.quiz.core.repository.TopicRepository
import fm.force.quiz.core.validator.stringConstraint
import java.time.Instant
import org.springframework.data.domain.Page
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class TopicService(
    validationProps: TopicValidationProperties,
    topicRepository: TopicRepository
) : TopicServiceType(repository = topicRepository) {
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

    override fun buildSearchSpec(search: SearchQueryDTO?): Specification<Topic> {
        val ownerEquals = SpecificationBuilder.fk(authenticationFacade::user, Topic_.owner)
        val needle = search?.query
        if (needle.isNullOrEmpty())
            return ownerEquals

        return Specification
            .where(ownerEquals).and(SpecificationBuilder.ciContains(needle, Topic_.title))
    }

    @Transactional
    override fun patch(id: Long, patchDTO: TopicPatchDTO): Topic {
        val topic = getOwnedEntity(id)
        topic.title = patchDTO.title
        topic.updatedAt = Instant.now()
        validateEntity(topic)
        return repository.save(topic)
    }

    @Transactional(readOnly = true)
    override fun serializePage(page: Page<Topic>): PageDTO = page.toDTO { it.toFullDTO() }

    @Transactional(readOnly = true)
    override fun serializeEntity(entity: Topic): TopicFullDTO =
        repository.refresh(entity).toFullDTO()
}
