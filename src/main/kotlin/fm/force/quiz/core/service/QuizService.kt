package fm.force.quiz.core.service

import am.ik.yavi.builder.ValidatorBuilder
import am.ik.yavi.builder.konstraintOnGroup
import fm.force.quiz.common.SpecificationBuilder
import fm.force.quiz.configuration.properties.QuizValidationProperties
import fm.force.quiz.core.dto.*
import fm.force.quiz.core.entity.Quiz
import fm.force.quiz.core.entity.QuizQuestion
import fm.force.quiz.core.entity.Quiz_
import fm.force.quiz.core.repository.*
import fm.force.quiz.core.validator.mandatory
import fm.force.quiz.core.validator.ownedFkConstraint
import fm.force.quiz.core.validator.ownedFksConstraint
import fm.force.quiz.core.validator.stringConstraint
import org.springframework.data.domain.Page
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Service
import java.time.Instant
import javax.transaction.Transactional


@Service
class QuizService(
        private val jpaTagRepository: JpaTagRepository,
        private val jpaTopicRepository: JpaTopicRepository,
        private val jpaQuestionRepository: JpaQuestionRepository,
        private val jpaDifficultyScaleRepository: JpaDifficultyScaleRepository,
        private val jpaQuizQuestionRepository: JpaQuizQuestionRepository,
        validationProps: QuizValidationProperties,
        jpaQuizRepository: JpaQuizRepository
) : AbstractPaginatedCRUDService<Quiz, JpaQuizRepository, QuizPatchDTO, QuizFullDTO>(
        repository = jpaQuizRepository
) {
    override var dtoValidator = ValidatorBuilder.of<QuizPatchDTO>()
            .konstraintOnGroup(CRUDConstraintGroup.CREATE) {
                mandatory(QuizPatchDTO::title)
            }
            .stringConstraint(QuizPatchDTO::title, validationProps.minTitleLength..validationProps.maxTitleLength)
            .ownedFksConstraint(QuizPatchDTO::questions, jpaQuestionRepository, 0..validationProps.maxQuestions, ::ownerId)
            .ownedFksConstraint(QuizPatchDTO::tags, jpaTagRepository, 0..validationProps.maxTags, ::ownerId)
            .ownedFksConstraint(QuizPatchDTO::topics, jpaTopicRepository, 0..validationProps.maxTopics, ::ownerId)
            .ownedFkConstraint(QuizPatchDTO::difficultyScale, jpaDifficultyScaleRepository, ::ownerId)
            .build()

    override fun buildSingleArgumentSearchSpec(needle: String?): Specification<Quiz> {
        val ownerEquals = SpecificationBuilder.fk(authenticationFacade::user, Quiz_.owner)
        if (needle.isNullOrEmpty()) return ownerEquals

        return Specification
                .where(ownerEquals).and(SpecificationBuilder.ciContains(needle, Quiz_.title))
    }

    override fun serializePage(page: Page<Quiz>): PageDTO = page.toDTO { it.toFullDTO() }

    override fun serializeEntity(entity: Quiz): QuizFullDTO = entity.toFullDTO()

    @Transactional
    override fun create(createDTO: QuizPatchDTO): Quiz {
        validateCreate(createDTO)
        var entity = with(createDTO) {
            Quiz(
                    owner = authenticationFacade.user,
                    title = title!!,
                    topics = jpaTopicRepository.findEntitiesById(topics).toMutableSet(),
                    tags = jpaTagRepository.findEntitiesById(tags).toMutableSet(),
                    difficultyScale = difficultyScale?.let { jpaDifficultyScaleRepository.getEntity(it) }
            )
        }
        entity = repository.save(entity)
        val quizQuestions = jpaQuestionRepository
                .findEntitiesById(createDTO.questions)
                .mapIndexed { index, question ->
                    QuizQuestion(
                            owner = authenticationFacade.user,
                            quiz = entity,
                            question = question,
                            seq = index
                    )
                }.toMutableList()
        jpaQuizQuestionRepository.saveAll(quizQuestions)
        entity.quizQuestions = quizQuestions
        return entity
    }

    override fun patch(id: Long, patchDTO: QuizPatchDTO): Quiz {
        validatePatch(patchDTO)
        val modified = getOwnedEntity(id).apply {
            if (patchDTO.title != null) title = patchDTO.title
            if (patchDTO.topics != null) topics = jpaTopicRepository.findEntitiesById(patchDTO.topics).toMutableSet()
            if (patchDTO.tags != null) tags = jpaTagRepository.findEntitiesById(patchDTO.tags).toMutableSet()
            if (patchDTO.difficultyScale != null) difficultyScale = jpaDifficultyScaleRepository.getEntity(patchDTO.difficultyScale)
            updatedAt = Instant.now()
        }
        return repository.save(modified)
    }
}