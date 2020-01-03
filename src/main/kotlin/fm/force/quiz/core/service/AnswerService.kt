package fm.force.quiz.core.service

import am.ik.yavi.builder.ValidatorBuilder
import fm.force.quiz.common.SpecificationBuilder
import fm.force.quiz.configuration.properties.AnswerValidationProperties
import fm.force.quiz.core.dto.*
import fm.force.quiz.core.entity.Answer
import fm.force.quiz.core.entity.Answer_
import fm.force.quiz.core.repository.JpaAnswerRepository
import fm.force.quiz.core.validator.stringConstraint
import org.springframework.data.domain.Page
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Service
import java.time.Instant

@Service
class AnswerService(
        jpaAnswerRepository: JpaAnswerRepository,
        validationProps: AnswerValidationProperties
) : AbstractPaginatedCRUDService<Answer, JpaAnswerRepository, AnswerPatchDTO, AnswerFullDTO>(
        repository = jpaAnswerRepository
) {
    override var entityValidator = ValidatorBuilder.of<Answer>()
            .stringConstraint(Answer::text, validationProps.minAnswerLength..validationProps.maxAnswerLength)
            .build()

    override fun buildSingleArgumentSearchSpec(needle: String?): Specification<Answer> {
        if (needle.isNullOrEmpty())
            return emptySpecification

        return Specification
                .where(SpecificationBuilder.fk(authenticationFacade::user, Answer_.owner))
                .and(SpecificationBuilder.ciContains(needle, Answer_.text))
    }

    override fun serializePage(page: Page<Answer>): PageDTO = page.toDTO { it.toFullDTO() }

    override fun create(createDTO: AnswerPatchDTO): Answer {
        val answer = Answer(
                text = createDTO.text,
                owner = authenticationFacade.user
        )
        validateEntity(answer)
        return repository.save(answer)
    }

    override fun patch(id: Long, patchDTO: AnswerPatchDTO): Answer {
        val answer = getInstance(id)
        answer.text = patchDTO.text
        answer.updatedAt = Instant.now()
        validateEntity(answer)
        return repository.save(answer)
    }

    override fun serializeEntity(entity: Answer): AnswerFullDTO = entity.toFullDTO()
}