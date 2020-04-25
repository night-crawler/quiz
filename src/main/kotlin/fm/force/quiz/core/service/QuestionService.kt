package fm.force.quiz.core.service

import am.ik.yavi.builder.ValidatorBuilder
import am.ik.yavi.builder.konstraintOnGroup
import fm.force.quiz.common.SpecificationBuilder
import fm.force.quiz.common.dto.PageDTO
import fm.force.quiz.common.dto.QuestionFullDTO
import fm.force.quiz.common.dto.QuestionPatchDTO
import fm.force.quiz.common.dto.SearchQueryDTO
import fm.force.quiz.common.mapper.toDTO
import fm.force.quiz.common.mapper.toFullDTO
import fm.force.quiz.configuration.properties.QuestionValidationProperties
import fm.force.quiz.core.entity.Question
import fm.force.quiz.core.entity.Question_
import fm.force.quiz.core.repository.AnswerRepository
import fm.force.quiz.core.repository.QuestionRepository
import fm.force.quiz.core.repository.TagRepository
import fm.force.quiz.core.repository.TopicRepository
import fm.force.quiz.core.validator.intConstraint
import fm.force.quiz.core.validator.mandatory
import fm.force.quiz.core.validator.optionalSubset
import fm.force.quiz.core.validator.ownedFksConstraint
import fm.force.quiz.core.validator.stringConstraint
import java.time.Instant
import org.springframework.data.domain.Page
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class QuestionService(
    private val answerRepository: AnswerRepository,
    private val tagRepository: TagRepository,
    private val topicRepository: TopicRepository,
    validationProps: QuestionValidationProperties,
    questionRepository: QuestionRepository
) : QuestionServiceType(repository = questionRepository) {

    override var dtoValidator = ValidatorBuilder.of<QuestionPatchDTO>()
        .konstraintOnGroup(CRUDConstraintGroup.CREATE) {
            mandatory(QuestionPatchDTO::title)
            mandatory(QuestionPatchDTO::text)
            mandatory(QuestionPatchDTO::help)
            mandatory(QuestionPatchDTO::answers)
            mandatory(QuestionPatchDTO::correctAnswers)
            optionalSubset(QuestionPatchDTO::answers, QuestionPatchDTO::correctAnswers)
        }

        .stringConstraint(QuestionPatchDTO::title, validationProps.textRange)
        .stringConstraint(QuestionPatchDTO::text, validationProps.textRange)
        .stringConstraint(QuestionPatchDTO::help, validationProps.helpRange)
        .ownedFksConstraint(QuestionPatchDTO::answers, answerRepository, validationProps.answersRange, ::ownerId)
        .ownedFksConstraint(QuestionPatchDTO::correctAnswers, answerRepository, validationProps.answersRange, ::ownerId)
        .ownedFksConstraint(QuestionPatchDTO::tags, tagRepository, validationProps.tagsRange, ::ownerId)
        .ownedFksConstraint(QuestionPatchDTO::topics, topicRepository, validationProps.topicsRange, ::ownerId)
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
                title = title!!,
                text = text!!,
                help = help!!,
                answers = answerRepository.findEntitiesById(answers).toMutableSet(),
                correctAnswers = answerRepository.findEntitiesById(correctAnswers).toMutableSet(),
                tags = tagRepository.findEntitiesById(tags).toMutableSet(),
                topics = topicRepository.findEntitiesById(topics).toMutableSet(),
                difficulty = difficulty ?: 0
            )
        }
        return repository.save(question)
    }

    @Transactional
    override fun patch(id: Long, patchDTO: QuestionPatchDTO): Question {
        validatePatch(patchDTO)

        val modifiedQuestion = getOwnedEntity(id).apply {
            if (patchDTO.title != null)
                title = patchDTO.title

            if (patchDTO.text != null)
                text = patchDTO.text

            if (patchDTO.answers != null)
                answers = answerRepository.findEntitiesById(patchDTO.answers).toMutableSet()

            if (patchDTO.correctAnswers != null)
                correctAnswers = answerRepository.findEntitiesById(patchDTO.correctAnswers).toMutableSet()

            if (patchDTO.tags != null)
                tags = tagRepository.findEntitiesById(patchDTO.tags).toMutableSet()

            if (patchDTO.topics != null)
                topics = topicRepository.findEntitiesById(patchDTO.topics).toMutableSet()

            if (patchDTO.difficulty != null)
                difficulty = patchDTO.difficulty
        }
        modifiedQuestion.updatedAt = Instant.now()

        validateEntity(modifiedQuestion)
        return repository.save(modifiedQuestion)
    }

    override fun buildSearchSpec(search: SearchQueryDTO?): Specification<Question> {
        val ownerEquals = SpecificationBuilder.fk(authenticationFacade::user, Question_.owner)
        val needle = search?.query
        if (needle.isNullOrEmpty()) return ownerEquals

        return ownerEquals.and(
            SpecificationBuilder
                .ciContains(needle, Question_.text)
                .or(SpecificationBuilder.ciContains(needle, Question_.title))
        )!!
    }

    @Transactional(readOnly = true)
    override fun serializePage(page: Page<Question>): PageDTO = page.toDTO { it.toFullDTO() }

    @Transactional(readOnly = true)
    override fun serializeEntity(entity: Question): QuestionFullDTO =
        repository.refresh(entity).toFullDTO()
}
