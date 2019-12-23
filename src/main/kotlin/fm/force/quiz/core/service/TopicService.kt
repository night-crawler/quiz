package fm.force.quiz.core.service

import am.ik.yavi.builder.ValidatorBuilder
import am.ik.yavi.builder.konstraint
import fm.force.quiz.configuration.properties.TopicValidationProperties
import fm.force.quiz.core.dto.CreateTopicDTO
import fm.force.quiz.core.entity.Topic
import fm.force.quiz.core.exception.ValidationError
import fm.force.quiz.core.repository.JpaTopicRepository
import fm.force.quiz.security.service.AuthenticationFacade
import org.springframework.stereotype.Service

@Service
class TopicService(
        private val jpaTopicRepository: JpaTopicRepository,
        private val validationProps: TopicValidationProperties,
        private val authenticationFacade: AuthenticationFacade
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

    fun create(topicDTO: CreateTopicDTO): Topic {
        val topic = Topic(
                owner = authenticationFacade.user,
                title = topicDTO.title
        )
        validate(topic)
        return jpaTopicRepository.save(topic)
    }
}
