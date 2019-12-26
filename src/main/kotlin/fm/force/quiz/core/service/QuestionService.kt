package fm.force.quiz.core.service

import am.ik.yavi.builder.ValidatorBuilder
import am.ik.yavi.builder.konstraint
import am.ik.yavi.builder.konstraintOnCondition
import am.ik.yavi.builder.konstraintOnGroup
import am.ik.yavi.core.ConstraintCondition
import fm.force.quiz.configuration.properties.QuestionValidationProperties
import fm.force.quiz.core.dto.PatchQuestionDTO
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
import java.time.Instant
import java.util.function.Predicate
import javax.transaction.Transactional


@Service
class QuestionService(
        private val jpaAnswerRepository: JpaAnswerRepository,
        private val jpaTagRepository: JpaTagRepository,
        private val jpaTopicRepository: JpaTopicRepository,
        private val validationProps: QuestionValidationProperties,
        authenticationFacade: AuthenticationFacade,
        jpaQuestionRepository: JpaQuestionRepository,
        paginationService: PaginationService,
        sortingService: SortingService
) : AbstractPaginatedCRUDService<Question, JpaQuestionRepository, PatchQuestionDTO, QuestionDTO>(
        repository = jpaQuestionRepository,
        authenticationFacade = authenticationFacade,
        paginationService = paginationService,
        sortingService = sortingService
) {
    private val msgTextTooShort = "Must be at least ${validationProps.minTextLength} characters long"
    private val msgTooManyAnswers = "Only ${validationProps.maxAnswers} answers are allowed"
    private val msgTooManyTags = "Only ${validationProps.maxTags} tags are allowed"
    private val msgNegativeDifficulty = "Must be greater than 0"
    private val msgAnswersEmpty = "Provide at least one answer"
    private val msgNotNull = "Must be present"
    private val msgWrongCorrectAnswers = "Correct answers must be a subset of answers"
    private val msgWrongAnswers = "Some entered answers do not belong to you or do not exist"
    private val msgWrongTags = "Some entered tags do not belong to you or do not exist"
    private val msgWrongTopics = "Some entered topics do not belong to you or do not exist"

    private val whenTextIsPresent = ConstraintCondition<PatchQuestionDTO> { dto, _ -> dto.text != null }
    private val whenAnswersArePresent = ConstraintCondition<PatchQuestionDTO> { dto, _ -> dto.answers != null }
    private val whenCorrectAnswersArePresent = ConstraintCondition<PatchQuestionDTO> { dto, _ -> dto.correctAnswers != null }
    private val whenTagsArePresent = ConstraintCondition<PatchQuestionDTO> { dto, _ -> dto.tags != null }
    private val whenTopicsArePresent = ConstraintCondition<PatchQuestionDTO> { dto, _ -> dto.topics != null }
    private val whenDifficultyIsPresent = ConstraintCondition<PatchQuestionDTO> { dto, _ -> dto.difficulty != null }

    private val whenAnswerIdsDoNotMatch = Predicate<PatchQuestionDTO> {
        if (it.correctAnswers == null || it.answers == null) true
        else (it.correctAnswers - it.answers).isEmpty()
    }
    private val whenAnswersDoNotExist = Predicate<PatchQuestionDTO> {
        if (it.answers.isNullOrEmpty()) true
        else jpaAnswerRepository.findOwnedIds(it.answers, authenticationFacade.user.id).toSet() == it.answers
    }
    private val whenTopicsDoNotExist = Predicate<PatchQuestionDTO> {
        if (it.topics.isNullOrEmpty()) true
        else jpaTopicRepository.findOwnedIds(it.topics, authenticationFacade.user.id).toSet() == it.topics
    }
    private val whenTagsDoNotExist = Predicate<PatchQuestionDTO> {
        if (it.tags.isNullOrEmpty()) true
        else jpaTagRepository.findOwnedIds(it.tags, authenticationFacade.user.id).toSet() == it.tags
    }
    private val whenAnswersDoNotMatch = Predicate<Question> {
        (it.correctAnswers - it.answers).isEmpty()
    }

    private val validator = ValidatorBuilder.of<PatchQuestionDTO>()
            // Called only when CREATE constraint group is passed to validate()
            .konstraintOnGroup(CRUDConstraintGroup.CREATE) {
                konstraint(PatchQuestionDTO::text) { notNull().message(msgNotNull) }
                konstraint(PatchQuestionDTO::answers) { notNull().message(msgNotNull) }
                konstraint(PatchQuestionDTO::correctAnswers) { notNull().message(msgNotNull) }

                constraintOnTarget(whenAnswerIdsDoNotMatch, "correctAnswers", "", msgWrongCorrectAnswers)
            }

            // everything else is used when updating
            .konstraintOnCondition(whenTextIsPresent) {
                konstraint(PatchQuestionDTO::text) {
                    greaterThan(validationProps.minTextLength).message(msgTextTooShort)
                }
            }

            .konstraintOnCondition(whenAnswersArePresent) {
                konstraint(PatchQuestionDTO::answers) {
                    lessThan(validationProps.maxAnswers).message(msgTooManyAnswers)
                    greaterThan(0).message(msgAnswersEmpty)
                }.forEach(PatchQuestionDTO::answers, "answers", fkValidator)
                constraintOnTarget(whenAnswersDoNotExist, "answers", "", msgWrongAnswers)
            }

            .konstraintOnCondition(whenCorrectAnswersArePresent) {
                konstraint(PatchQuestionDTO::correctAnswers) {
                    lessThan(validationProps.maxAnswers).message(msgTooManyAnswers)
                    greaterThan(0).message(msgAnswersEmpty)
                }.forEach(PatchQuestionDTO::correctAnswers, "correctAnswers", fkValidator)
            }

            .konstraintOnCondition(whenTagsArePresent) {
                konstraint(PatchQuestionDTO::tags) {
                    lessThan(validationProps.maxTags).message(msgTooManyTags)
                }.forEach(PatchQuestionDTO::tags, "tags", fkValidator)
                constraintOnTarget(whenTagsDoNotExist, "tags", "", msgWrongTags)
            }

            .konstraintOnCondition(whenTopicsArePresent) {
                forEach(PatchQuestionDTO::topics, "topics", fkValidator)
                constraintOnTarget(whenTopicsDoNotExist, "topics", "", msgWrongTopics)
            }

            .konstraintOnCondition(whenDifficultyIsPresent) {
                konstraint(PatchQuestionDTO::difficulty) {
                    greaterThanOrEqual(0).message(msgNegativeDifficulty)
                }
            }
            .build()

    val integrityValidator = ValidatorBuilder.of<Question>()
            .constraintOnTarget(whenAnswersDoNotMatch, "correctAnswers", "", msgWrongCorrectAnswers)
            .build()

    fun validatePatch(updateDTO: PatchQuestionDTO) = validator
            // everything else is supposed to be the update group, thus no need to pass anything else to validate()
            .validate(updateDTO)
            .throwIfInvalid { ValidationError(it) }

    fun validateCreate(createDTO: PatchQuestionDTO) = validator
            .validate(createDTO, CRUDConstraintGroup.CREATE)
            .throwIfInvalid { ValidationError(it) }

    fun validateQuestion(question: Question) = integrityValidator
            .validate(question)
            .throwIfInvalid { ValidationError(it) }

    private fun retrieveAnswers(ids: Collection<Long>?) = ids?.let { jpaAnswerRepository.findAllById(it).toMutableSet() } ?: mutableSetOf()
    private fun retrieveTopics(ids: Collection<Long>?) = ids?.let { jpaTopicRepository.findAllById(it).toMutableSet() } ?: mutableSetOf()
    private fun retrieveTags(ids: Collection<Long>?) = ids?.let { jpaTagRepository.findAllById(it).toMutableSet() } ?: mutableSetOf()

    @Transactional
    override fun create(createDTO: PatchQuestionDTO): Question {
        validateCreate(createDTO)
        // after validation text + answers are never null
        val question = with(createDTO) { Question(
                owner = authenticationFacade.user,
                text = text!!,
                answers = retrieveAnswers(answers),
                correctAnswers = retrieveAnswers(correctAnswers),
                tags = retrieveTags(tags),
                topics = retrieveTopics(topics),
                difficulty = difficulty ?: 0
        )}
        return repository.save(question)
    }

    override fun patch(id: Long, patchDTO: PatchQuestionDTO): Question {
        validatePatch(patchDTO)
        val question = getInstance(id)
        val modifiedQuestion = with(patchDTO) {
            if (text != null) question.text = text
            if (answers != null) question.answers = retrieveAnswers(answers)
            if (correctAnswers != null) question.correctAnswers = retrieveAnswers(correctAnswers)
            if (tags != null) question.tags = retrieveTags(tags)
            if (topics != null) question.topics = retrieveTopics(topics)
            if (difficulty != null) question.difficulty = difficulty

            question
        }
        modifiedQuestion.updatedAt = Instant.now()
        validateQuestion(modifiedQuestion)
        return repository.save(modifiedQuestion)
    }

    override fun buildSingleArgumentSearchSpec(needle: String?): Specification<Question> {
        if (needle.isNullOrEmpty())
            return emptySpecification

        val lowerCaseNeedle = needle.toLowerCase()

        val ownerEquals = Specification<Question> { root, _, builder ->
            builder.equal(root[Question_.owner], authenticationFacade.user)
        }

        val textLike = Specification<Question> { root, _, builder ->
            builder.like(builder.lower(root[Question_.text]), "%$lowerCaseNeedle%")
        }

        return Specification.where(ownerEquals).and(textLike)
    }

    override fun serializePage(page: Page<Question>): PageDTO = page.toDTO { it.toDTO() }
    override fun serializeEntity(entity: Question): QuestionDTO = entity.toDTO()
}
