package fm.force.quiz.core.service

import am.ik.yavi.builder.ValidatorBuilder
import am.ik.yavi.builder.konstraintOnGroup
import fm.force.quiz.common.SpecificationBuilder
import fm.force.quiz.configuration.properties.QuestionValidationProperties
import fm.force.quiz.core.dto.*
import fm.force.quiz.core.entity.Question
import fm.force.quiz.core.entity.Question_
import fm.force.quiz.core.repository.JpaAnswerRepository
import fm.force.quiz.core.repository.JpaQuestionRepository
import fm.force.quiz.core.repository.JpaTagRepository
import fm.force.quiz.core.repository.JpaTopicRepository
import fm.force.quiz.core.validator.*
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
        jpaQuestionRepository: JpaQuestionRepository
) : AbstractPaginatedCRUDService<Question, JpaQuestionRepository, QuestionPatchDTO, QuestionFullDTO>(
        repository = jpaQuestionRepository
) {

    override var dtoValidator = ValidatorBuilder.of<QuestionPatchDTO>()
            .konstraintOnGroup(CRUDConstraintGroup.CREATE) {
                mandatory(QuestionPatchDTO::text)
                mandatory(QuestionPatchDTO::answers)
                mandatory(QuestionPatchDTO::correctAnswers)
                optionalSubset(QuestionPatchDTO::answers, QuestionPatchDTO::correctAnswers)
            }

            .stringConstraint(QuestionPatchDTO::text, validationProps.minTextLength..Int.MAX_VALUE)
            .ownedFksConstraint(QuestionPatchDTO::answers, jpaAnswerRepository, 1..validationProps.maxAnswers, ::ownerId)
            .ownedFksConstraint(QuestionPatchDTO::correctAnswers, jpaAnswerRepository, 1..validationProps.maxAnswers, ::ownerId)
            .ownedFksConstraint(QuestionPatchDTO::tags, jpaTagRepository, 0..validationProps.maxTags, ::ownerId)
            .ownedFksConstraint(QuestionPatchDTO::topics, jpaTopicRepository, 0..validationProps.maxTopics, ::ownerId)
            .intConstraint(QuestionPatchDTO::difficulty, 0..Int.MAX_VALUE)
            .build()

    override var entityValidator = ValidatorBuilder.of<Question>()
            .optionalSubset(Question::answers, Question::correctAnswers)
            .build()

    @Transactional
    override fun create(createDTO: QuestionPatchDTO): Question {
        validateCreate(createDTO)
        // after validation text + answers are never null
        val question = with(createDTO) {
            Question(
                    owner = authenticationFacade.user,
                    text = text!!,
                    answers = jpaAnswerRepository.findEntitiesById(answers).toMutableSet(),
                    correctAnswers = jpaAnswerRepository.findEntitiesById(correctAnswers).toMutableSet(),
                    tags = jpaTagRepository.findEntitiesById(tags).toMutableSet(),
                    topics = jpaTopicRepository.findEntitiesById(topics).toMutableSet(),
                    difficulty = difficulty ?: 0
            )
        }
        return repository.save(question)
    }

    @Transactional
    override fun patch(id: Long, patchDTO: QuestionPatchDTO): Question {
        validatePatch(patchDTO)
        val question = getOwnedEntity(id)
        val modifiedQuestion = with(patchDTO) {
            if (text != null) question.text = text
            if (answers != null) question.answers = jpaAnswerRepository.findEntitiesById(answers).toMutableSet()
            if (correctAnswers != null) question.correctAnswers = jpaAnswerRepository.findEntitiesById(correctAnswers).toMutableSet()
            if (tags != null) question.tags = jpaTagRepository.findEntitiesById(tags).toMutableSet()
            if (topics != null) question.topics = jpaTopicRepository.findEntitiesById(topics).toMutableSet()
            if (difficulty != null) question.difficulty = difficulty

            question
        }
        modifiedQuestion.updatedAt = Instant.now()
        validateEntity(modifiedQuestion)
        return repository.save(modifiedQuestion)
    }

    override fun buildSingleArgumentSearchSpec(needle: String?): Specification<Question> {
        val ownerEquals = SpecificationBuilder.fk(authenticationFacade::user, Question_.owner)
        if (needle.isNullOrEmpty()) return ownerEquals

        return Specification
                .where(ownerEquals).and(SpecificationBuilder.ciContains(needle, Question_.text))
    }

    override fun serializePage(page: Page<Question>): PageDTO = page.toDTO { it.toFullDTO() }
    override fun serializeEntity(entity: Question): QuestionFullDTO = entity.toFullDTO()
}
