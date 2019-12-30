package fm.force.quiz.core.service

import am.ik.yavi.builder.ValidatorBuilder
import am.ik.yavi.builder.konstraintOnGroup
import fm.force.quiz.common.SpecificationBuilder
import fm.force.quiz.configuration.properties.QuestionValidationProperties
import fm.force.quiz.core.dto.PageDTO
import fm.force.quiz.core.dto.PatchQuestionDTO
import fm.force.quiz.core.dto.QuestionDTO
import fm.force.quiz.core.dto.toDTO
import fm.force.quiz.core.entity.Question
import fm.force.quiz.core.entity.Question_
import fm.force.quiz.core.repository.JpaAnswerRepository
import fm.force.quiz.core.repository.JpaQuestionRepository
import fm.force.quiz.core.repository.JpaTagRepository
import fm.force.quiz.core.repository.JpaTopicRepository
import fm.force.quiz.core.validator.*
import fm.force.quiz.security.service.AuthenticationFacade
import org.springframework.data.domain.Page
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Service
import java.time.Instant
import javax.transaction.Transactional


@Service
class QuestionService(
        private val jpaAnswerRepository: JpaAnswerRepository,
        private val jpaTagRepository: JpaTagRepository,
        private val jpaTopicRepository: JpaTopicRepository,
        validationProps: QuestionValidationProperties,
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

    override var dtoValidator = ValidatorBuilder.of<PatchQuestionDTO>()
            .konstraintOnGroup(CRUDConstraintGroup.CREATE) {
                mandatory(PatchQuestionDTO::text)
                mandatory(PatchQuestionDTO::answers)
                mandatory(PatchQuestionDTO::correctAnswers)
                optionalSubset(PatchQuestionDTO::answers, PatchQuestionDTO::correctAnswers)
            }

            .stringConstraint(PatchQuestionDTO::text, validationProps.minTextLength..Int.MAX_VALUE)
            .fkListConstraint(
                    PatchQuestionDTO::answers, jpaAnswerRepository,
                    1..validationProps.maxAnswers,
                    getOwnerId = { authenticationFacade.user.id }
            )
            .fkListConstraint(
                    PatchQuestionDTO::correctAnswers, jpaAnswerRepository,
                    1..validationProps.maxAnswers,
                    getOwnerId = { authenticationFacade.user.id }
            )
            .fkListConstraint(
                    PatchQuestionDTO::tags, jpaTagRepository,
                    0..validationProps.maxTags,
                    getOwnerId = { authenticationFacade.user.id }
            )
            .fkListConstraint(
                    PatchQuestionDTO::topics, jpaTopicRepository,
                    0..validationProps.maxTopics,
                    getOwnerId = { authenticationFacade.user.id }
            )
            .intConstraint(PatchQuestionDTO::difficulty, 0..Int.MAX_VALUE)
            .build()

    override var entityValidator = ValidatorBuilder.of<Question>()
            .optionalSubset(Question::answers, Question::correctAnswers)
            .build()

    private fun retrieveAnswers(ids: Collection<Long>?) = ids?.let { jpaAnswerRepository.findAllById(it).toMutableSet() }
            ?: mutableSetOf()

    private fun retrieveTopics(ids: Collection<Long>?) = ids?.let { jpaTopicRepository.findAllById(it).toMutableSet() }
            ?: mutableSetOf()

    private fun retrieveTags(ids: Collection<Long>?) = ids?.let { jpaTagRepository.findAllById(it).toMutableSet() }
            ?: mutableSetOf()

    @Transactional
    override fun create(createDTO: PatchQuestionDTO): Question {
        validateCreate(createDTO)
        // after validation text + answers are never null
        val question = with(createDTO) {
            Question(
                    owner = authenticationFacade.user,
                    text = text!!,
                    answers = retrieveAnswers(answers),
                    correctAnswers = retrieveAnswers(correctAnswers),
                    tags = retrieveTags(tags),
                    topics = retrieveTopics(topics),
                    difficulty = difficulty ?: 0
            )
        }
        return repository.save(question)
    }

    @Transactional
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
        validateEntity(modifiedQuestion)
        return repository.save(modifiedQuestion)
    }

    override fun buildSingleArgumentSearchSpec(needle: String?): Specification<Question> {
        if (needle.isNullOrEmpty())
            return emptySpecification

        return Specification
                .where(SpecificationBuilder.fk(authenticationFacade::user, Question_.owner))
                .and(SpecificationBuilder.ciContains(needle, Question_.text))
    }

    override fun serializePage(page: Page<Question>): PageDTO = page.toDTO { it.toDTO() }
    override fun serializeEntity(entity: Question): QuestionDTO = entity.toDTO()
}
