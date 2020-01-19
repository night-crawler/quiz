package fm.force.quiz.core.service

import am.ik.yavi.builder.ValidatorBuilder
import fm.force.quiz.common.SpecificationBuilder
import fm.force.quiz.configuration.properties.AnswerValidationProperties
import fm.force.quiz.core.dto.AnswerFullDTO
import fm.force.quiz.core.dto.AnswerPatchDTO
import fm.force.quiz.core.dto.PageDTO
import fm.force.quiz.core.dto.SearchQueryDTO
import fm.force.quiz.core.dto.toDTO
import fm.force.quiz.core.dto.toFullDTO
import fm.force.quiz.core.entity.Answer
import fm.force.quiz.core.entity.Answer_
import fm.force.quiz.core.repository.AnswerRepository
import fm.force.quiz.core.validator.stringConstraint
import java.time.Instant
import org.springframework.data.domain.Page
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AnswerService(
    answerRepository: AnswerRepository,
    validationProps: AnswerValidationProperties
) : AnswerServiceType(repository = answerRepository) {

    override var entityValidator = ValidatorBuilder.of<Answer>()
        .stringConstraint(Answer::text, validationProps.minAnswerLength..validationProps.maxAnswerLength)
        .build()

    override fun buildSearchSpec(search: SearchQueryDTO?): Specification<Answer> {
        val ownerEquals = SpecificationBuilder.fk(authenticationFacade::user, Answer_.owner)
        val needle = search?.query
        if (needle.isNullOrEmpty()) return ownerEquals

        return Specification
            .where(ownerEquals).and(SpecificationBuilder.ciContains(search.query, Answer_.text))
    }

    @Transactional(readOnly = true)
    override fun serializePage(page: Page<Answer>): PageDTO = page.toDTO { it.toFullDTO() }

    @Transactional
    override fun create(createDTO: AnswerPatchDTO): Answer {
        val answer = Answer(
            text = createDTO.text,
            owner = authenticationFacade.user
        )
        validateEntity(answer)
        return repository.save(answer)
    }

    @Transactional
    override fun patch(id: Long, patchDTO: AnswerPatchDTO): Answer {
        val answer = getOwnedEntity(id)
        answer.text = patchDTO.text
        answer.updatedAt = Instant.now()
        validateEntity(answer)
        return repository.save(answer)
    }

    @Transactional(readOnly = true)
    override fun serializeEntity(entity: Answer): AnswerFullDTO = repository.refresh(entity).toFullDTO()
}
