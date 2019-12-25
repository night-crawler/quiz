package fm.force.quiz.core.service

import am.ik.yavi.builder.ValidatorBuilder
import am.ik.yavi.builder.konstraint
import fm.force.quiz.configuration.properties.QuestionValidationProperties
import fm.force.quiz.core.dto.CreateQuestionDTO
import fm.force.quiz.core.dto.PageDTO
import fm.force.quiz.core.dto.QuestionDTO
import fm.force.quiz.core.dto.toDTO
import fm.force.quiz.core.entity.Question
import fm.force.quiz.core.entity.Question_
import fm.force.quiz.core.exception.ValidationError
import fm.force.quiz.core.repository.JpaAnswerRepository
import fm.force.quiz.core.repository.JpaQuestionRepository
import fm.force.quiz.core.repository.JpaTagRepository
import fm.force.quiz.core.repository.JpaTopicRepository
import fm.force.quiz.core.validator.fkValidator
import fm.force.quiz.security.service.AuthenticationFacade
import org.springframework.data.domain.Page
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Service
import javax.transaction.Transactional


@Service
class QuestionService(
        val jpaAnswerRepository: JpaAnswerRepository,
        val jpaTagRepository: JpaTagRepository,
        val jpaTopicRepository: JpaTopicRepository,
        val validationProps: QuestionValidationProperties,
        authenticationFacade: AuthenticationFacade,
        jpaQuestionRepository: JpaQuestionRepository,
        paginationService: PaginationService,
        sortingService: SortingService
) : AbstractPaginatedCRUDService<Question, JpaQuestionRepository, CreateQuestionDTO, QuestionDTO>(
        repository = jpaQuestionRepository,
        authenticationFacade = authenticationFacade,
        paginationService = paginationService,
        sortingService = sortingService
) {
    // all field names must remain same in order to be able to be merged
    inner class CreateQuestionDTOWrapper(private val instance: CreateQuestionDTO) {
        private val ownerId = authenticationFacade.principal.id!!
        val correctAnswers get() = instance.correctAnswers - instance.answers == emptySet<Long>()
        val answers get() = jpaAnswerRepository.findOwnedIds(instance.answers, ownerId).toSet() == instance.answers

        val tags
            get() = if (instance.tags.isNotEmpty())
                jpaTagRepository.findOwnedIds(instance.tags, ownerId).toSet() == instance.tags
            else true

        // ! we cannot query empty lists like this, hibernate will fail
        val topics
            get() = if (instance.topics.isNotEmpty())
                jpaTopicRepository.findOwnedIds(instance.topics, ownerId).toSet() == instance.topics
            else true
    }

    val questionDTOValidator = ValidatorBuilder.of<CreateQuestionDTO>()
            .konstraint(CreateQuestionDTO::text) {
                greaterThan(validationProps.minTextLength).message("Must be at least ${validationProps.minTextLength} characters long")
            }

            .konstraint(CreateQuestionDTO::answers) {
                lessThan(validationProps.maxAnswers).message("Only ${validationProps.maxAnswers} answers are allowed")
                greaterThan(0).message("Provide at least one answer")
            }
            .forEach(CreateQuestionDTO::answers, "answers", fkValidator)

            .konstraint(CreateQuestionDTO::correctAnswers) {
                lessThan(validationProps.maxAnswers).message("Only ${validationProps.maxAnswers} correct answers are allowed")
                greaterThan(0).message("Provide at least one correct answer")
            }
            .forEach(CreateQuestionDTO::correctAnswers, "correctAnswers", fkValidator)

            .konstraint(CreateQuestionDTO::tags) {
                lessThan(validationProps.maxTags).message("Only ${validationProps.maxTags} tags are allowed")
            }
            .forEach(CreateQuestionDTO::tags, "tags", fkValidator)

            .forEach(CreateQuestionDTO::topics, "topics", fkValidator)

            .konstraint(CreateQuestionDTO::difficulty) {
                greaterThanOrEqual(0).message("Must be greater than 0")
            }
            .build()

    val questionDTOMiscValidator = ValidatorBuilder.of<CreateQuestionDTOWrapper>()
            .konstraint(CreateQuestionDTOWrapper::correctAnswers) {
                isTrue.message("Must be a subset of all answers")
            }
            .konstraint(CreateQuestionDTOWrapper::answers) {
                isTrue.message("Provided answers seem not to belong to you or do not exist")
            }
            .konstraint(CreateQuestionDTOWrapper::topics) {
                isTrue.message("Provided topics seem not to belong to you or do not exist")
            }
            .konstraint(CreateQuestionDTOWrapper::tags) {
                isTrue.message("Provided tags do not belong to you or do not exist")
            }
            .build()

    fun validate(question: CreateQuestionDTO) {
        questionDTOValidator
                .validate(question)
                .throwIfInvalid { ValidationError(it) }

        questionDTOMiscValidator
                .validate(CreateQuestionDTOWrapper(question))
                .throwIfInvalid { ValidationError(it) }
    }

    @Transactional
    override fun create(createDTO: CreateQuestionDTO): Question {
        validate(createDTO)

        val answersMap = jpaAnswerRepository
                .findAllById(createDTO.answers)
                .map { it.id to it }.toMap()

        val question = Question(
                owner = authenticationFacade.user,
                text = createDTO.text,
                answers = createDTO.answers.map { answersMap[it]!! }.toSet(),
                correctAnswers = createDTO.correctAnswers.map { answersMap[it]!! }.toSet(),
                tags = jpaTagRepository.findAllById(createDTO.tags).toSet(),
                topics = jpaTopicRepository.findAllById(createDTO.topics).toSet(),
                difficulty = createDTO.difficulty
        )

        return repository.save(question)
    }

    override fun buildSingleArgumentSearchSpec(needle: String?): Specification<Question> {
        if (needle.isNullOrEmpty())
            return emptySpecification

        val lowerCaseNeedle = needle.toLowerCase()

        val ownerEquals = Specification<Question> { root, _, builder ->
            builder.equal(root[Question_.owner], authenticationFacade.user) }

        val textLike = Specification<Question> { root, _, builder ->
            builder.like(builder.lower(root[Question_.text]), "%$lowerCaseNeedle%") }

        return Specification.where(ownerEquals).and(textLike)
    }

    override fun serializePage(page: Page<Question>): PageDTO = page.toDTO { it.toDTO() }
    override fun serializeEntity(entity: Question): QuestionDTO = entity.toDTO()
}
