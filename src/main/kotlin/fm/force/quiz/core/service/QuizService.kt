package fm.force.quiz.core.service

import am.ik.yavi.builder.ValidatorBuilder
import am.ik.yavi.builder.konstraintOnGroup
import fm.force.quiz.common.SpecificationBuilder
import fm.force.quiz.common.dto.PageDTO
import fm.force.quiz.common.dto.QuizFullDTO
import fm.force.quiz.common.dto.QuizPatchDTO
import fm.force.quiz.common.dto.SearchQueryDTO
import fm.force.quiz.common.mapper.toDTO
import fm.force.quiz.common.mapper.toFullDTO
import fm.force.quiz.common.mapper.toRestrictedDTO
import fm.force.quiz.configuration.properties.QuizValidationProperties
import fm.force.quiz.core.entity.Quiz
import fm.force.quiz.core.entity.QuizQuestion
import fm.force.quiz.core.entity.Quiz_
import fm.force.quiz.core.repository.DifficultyScaleRepository
import fm.force.quiz.core.repository.QuestionRepository
import fm.force.quiz.core.repository.QuizQuestionRepository
import fm.force.quiz.core.repository.QuizRepository
import fm.force.quiz.core.repository.QuizSessionRepository
import fm.force.quiz.core.repository.TagRepository
import fm.force.quiz.core.repository.TopicRepository
import fm.force.quiz.core.validator.mandatory
import fm.force.quiz.core.validator.ownedFkConstraint
import fm.force.quiz.core.validator.ownedFksConstraint
import fm.force.quiz.core.validator.stringConstraint
import java.time.Instant
import org.springframework.data.domain.Page
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class QuizService(
    private val tagRepository: TagRepository,
    private val topicRepository: TopicRepository,
    private val questionRepository: QuestionRepository,
    private val difficultyScaleRepository: DifficultyScaleRepository,
    private val quizQuestionRepository: QuizQuestionRepository,
    private val quizSessionRepository: QuizSessionRepository,
    validationProps: QuizValidationProperties,
    quizRepository: QuizRepository
) : QuizServiceType(repository = quizRepository) {
    override var dtoValidator = ValidatorBuilder.of<QuizPatchDTO>()
        .konstraintOnGroup(CRUDConstraintGroup.CREATE) {
            mandatory(QuizPatchDTO::title)
        }
        .stringConstraint(QuizPatchDTO::title, validationProps.minTitleLength..validationProps.maxTitleLength)
        .ownedFksConstraint(QuizPatchDTO::questions, questionRepository, 0..validationProps.maxQuestions, ::ownerId)
        .ownedFksConstraint(QuizPatchDTO::tags, tagRepository, 0..validationProps.maxTags, ::ownerId)
        .ownedFksConstraint(QuizPatchDTO::topics, topicRepository, 0..validationProps.maxTopics, ::ownerId)
        .ownedFkConstraint(QuizPatchDTO::difficultyScale, difficultyScaleRepository, ::ownerId)
        .build()

    override fun buildSearchSpec(search: SearchQueryDTO?): Specification<Quiz> {
        val ownerEquals = SpecificationBuilder.fk(authenticationFacade::user, Quiz_.owner)
        val needle = search?.query
        if (needle.isNullOrEmpty()) return ownerEquals

        return ownerEquals.and(SpecificationBuilder.ciContains(needle, Quiz_.title))!!
    }

    @Transactional(readOnly = true)
    override fun serializePage(page: Page<Quiz>): PageDTO = page.toDTO { it.toFullDTO() }

    @Transactional(readOnly = true)
    override fun serializeEntity(entity: Quiz): QuizFullDTO =
        repository.refresh(entity).toFullDTO()

    @Transactional(readOnly = true)
    fun serializeRestrictedEntity(entity: Quiz) =
        repository.refresh(entity).toRestrictedDTO()

    @Transactional
    override fun create(createDTO: QuizPatchDTO): Quiz {
        validateCreate(createDTO)
        var entity = with(createDTO) {
            Quiz(
                owner = authenticationFacade.user,
                title = title!!,
                topics = topicRepository.findEntitiesById(topics).toMutableSet(),
                tags = tagRepository.findEntitiesById(tags).toMutableSet(),
                difficultyScale = difficultyScale?.let { difficultyScaleRepository.getEntity(it) }
            )
        }
        entity = repository.save(entity)
        createDTO.questions?.let {
            entity.quizQuestions = createQuizQuestions(entity, it)
        }
        return entity
    }

    @Transactional
    fun createQuizQuestions(quiz: Quiz, questionIds: Collection<Long>): MutableList<QuizQuestion> {
        val questionsMap = questionRepository
            .findEntitiesById(questionIds)
            .map { it.id to it }
            .toMap()

        val quizQuestions = questionIds
            .distinct()
            .mapNotNull { questionsMap[it] }
            .mapIndexed { index, question ->
                QuizQuestion(
                    owner = authenticationFacade.user,
                    quiz = quiz,
                    question = question,
                    seq = index
                )
            }.toMutableList()
        quizQuestionRepository.saveAll(quizQuestions)
        return quizQuestions
    }

    @Transactional
    override fun patch(id: Long, patchDTO: QuizPatchDTO): Quiz {
        validatePatch(patchDTO)
        val modified = getOwnedEntity(id).apply {
            if (patchDTO.title != null)
                title = patchDTO.title

            if (patchDTO.topics != null)
                topics = topicRepository.findEntitiesById(patchDTO.topics).toMutableSet()

            if (patchDTO.tags != null)
                tags = tagRepository.findEntitiesById(patchDTO.tags).toMutableSet()

            if (patchDTO.difficultyScale != null)
                difficultyScale = difficultyScaleRepository.getEntity(patchDTO.difficultyScale)

            if (patchDTO.questions != null) {
                quizQuestionRepository.deleteAll(quizQuestions)
                quizQuestionRepository.flush()
                quizQuestions = createQuizQuestions(this, patchDTO.questions)
            }

            updatedAt = Instant.now()
        }
        return repository.save(modified)
    }
}
