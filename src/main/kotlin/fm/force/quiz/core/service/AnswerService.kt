package fm.force.quiz.core.service

import am.ik.yavi.builder.ValidatorBuilder
import am.ik.yavi.builder.konstraint
import fm.force.quiz.configuration.properties.AnswerValidationProperties
import fm.force.quiz.core.dto.AnswerDTO
import fm.force.quiz.core.dto.CreateAnswerDTO
import fm.force.quiz.core.dto.PageDTO
import fm.force.quiz.core.dto.toDTO
import fm.force.quiz.core.entity.Answer
import fm.force.quiz.core.entity.Answer_
import fm.force.quiz.core.exception.ValidationError
import fm.force.quiz.core.repository.JpaAnswerRepository
import fm.force.quiz.security.service.AuthenticationFacade
import org.springframework.data.domain.Page
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Service

@Service
class AnswerService(
        authenticationFacade: AuthenticationFacade,
        jpaAnswerRepository: JpaAnswerRepository,
        paginationService: PaginationService,
        sortingService: SortingService,
        validationProps: AnswerValidationProperties
) : AbstractPaginatedCRUDService<Answer, JpaAnswerRepository, CreateAnswerDTO, AnswerDTO>(
        repository = jpaAnswerRepository,
        authenticationFacade = authenticationFacade,
        paginationService = paginationService,
        sortingService = sortingService
) {
    val validator = ValidatorBuilder.of<Answer>()
            .konstraint(Answer::text) {
                greaterThanOrEqual(validationProps.minAnswerLength)
                        .message("Answer text must at least be ${validationProps.minAnswerLength} characters long")

                lessThanOrEqual(validationProps.maxAnswerLength)
                        .message("Answer text must be max ${validationProps.maxAnswerLength} characters long")
            }
            .build()

    fun validate(instance: Answer) = validator.validate(instance).throwIfInvalid { ValidationError(it) }

    override fun buildSingleArgumentSearchSpec(needle: String?): Specification<Answer> {
        if (needle.isNullOrEmpty())
            return emptySpecification

        val lowerCaseNeedle = needle.toLowerCase()

        val ownerEquals = Specification<Answer> { root, _, builder ->
            builder.equal(root[Answer_.owner], authenticationFacade.user) }

        val textContains = Specification<Answer> {  root, _, builder ->
            builder.like(builder.lower(root[Answer_.text]), "%$lowerCaseNeedle%") }

        return Specification.where(ownerEquals).and(textContains)
    }

    override fun serializePage(page: Page<Answer>): PageDTO = page.toDTO { it.toDTO() }

    override fun create(createDTO: CreateAnswerDTO): Answer {
        val answer = Answer(
                text = createDTO.text,
                owner = authenticationFacade.user
        )
        validate(answer)
        return repository.save(answer)
    }

    override fun serializeEntity(entity: Answer): AnswerDTO = entity.toDTO()
}