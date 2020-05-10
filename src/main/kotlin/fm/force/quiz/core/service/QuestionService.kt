package fm.force.quiz.core.service

import am.ik.yavi.builder.ValidatorBuilder
import am.ik.yavi.builder.konstraintOnGroup
import fm.force.quiz.common.SpecificationBuilder
import fm.force.quiz.common.dto.PageDTO
import fm.force.quiz.common.dto.QuestionFullDTO
import fm.force.quiz.common.dto.QuestionPatchDTO
import fm.force.quiz.common.dto.QuestionSearchQueryDTO
import fm.force.quiz.common.mapper.toDTO
import fm.force.quiz.common.mapper.toFullDTO
import fm.force.quiz.configuration.properties.QuestionValidationProperties
import fm.force.quiz.core.entity.Question
import fm.force.quiz.core.entity.Question_
import fm.force.quiz.core.entity.Tag_
import fm.force.quiz.core.entity.Topic_
import fm.force.quiz.core.repository.AnswerRepository
import fm.force.quiz.core.repository.QuestionRepository
import fm.force.quiz.core.repository.TagRepository
import fm.force.quiz.core.repository.TopicRepository
import fm.force.quiz.core.validator.intConstraint
import fm.force.quiz.core.validator.mandatory
import fm.force.quiz.core.validator.optionalSubset
import fm.force.quiz.core.validator.ownedFksConstraint
import fm.force.quiz.core.validator.stringConstraint
import org.slf4j.LoggerFactory
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

            if (patchDTO.help != null)
                help = patchDTO.help

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

    override fun buildSearchSpec(search: QuestionSearchQueryDTO?): Specification<Question> {
        var spec = SpecificationBuilder.fk(authenticationFacade::user, Question_.owner)
        if (search == null) {
            return spec
        }

        if (!search.query.isNullOrEmpty()) {
            spec = spec.and(
                SpecificationBuilder
                    .ciContains(search.query, Question_.text)
                    .or(SpecificationBuilder.ciContains(search.query, Question_.title))
            )!!
        }

        if (!search.tagSlugs.isNullOrEmpty()) {
            val tagSlugs = search.tagSlugs.split(",").map { it.trim() }
            val tagsSpec = Specification<Question> { root, query, _ ->
                query.distinct(true)
                val questionTag = root.join(Question_.tags)
                questionTag.get(Tag_.slug).`in`(tagSlugs)
            }
            spec = spec.and(tagsSpec)!!
        }

        if (!search.topicSlugs.isNullOrEmpty()) {
            val topicSlugs = search.topicSlugs.split(",").map { it.trim() }
            val topicsSpec = Specification<Question> { root, query, _ ->
                query.distinct(true)
                val questionTopic = root.join(Question_.topics)
                questionTopic.get(Topic_.slug).`in`(topicSlugs)
            }
            spec = spec.and(topicsSpec)!!
        }

        return spec
    }

    @Transactional(readOnly = true)
    override fun serializePage(page: Page<Question>): PageDTO = page.toDTO { it.toFullDTO() }

    @Transactional(readOnly = true)
    override fun serializeEntity(entity: Question): QuestionFullDTO =
        repository.refresh(entity).toFullDTO()
}
