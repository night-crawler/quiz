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
import fm.force.quiz.core.validator.fkConstraint
import fm.force.quiz.core.validator.fkListConstraint
import fm.force.quiz.core.validator.mandatory
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
) : AbstractPaginatedCRUDService<Quiz, JpaQuizRepository, PatchQuizDTO, QuizFullDTO>(
        repository = jpaQuizRepository
) {
    override var dtoValidator = ValidatorBuilder.of<PatchQuizDTO>()
            .konstraintOnGroup(CRUDConstraintGroup.CREATE) {
                mandatory(PatchQuizDTO::title)
            }
            .stringConstraint(PatchQuizDTO::title, validationProps.minTitleLength..validationProps.maxTitleLength)
            .fkListConstraint(PatchQuizDTO::questions, jpaQuestionRepository, 0..validationProps.maxQuestions, ::ownerId)
            .fkListConstraint(PatchQuizDTO::tags, jpaTagRepository, 0..validationProps.maxTags, ::ownerId)
            .fkListConstraint(PatchQuizDTO::topics, jpaTopicRepository, 0..validationProps.maxTopics, ::ownerId)
            .fkConstraint(PatchQuizDTO::difficultyScale, jpaDifficultyScaleRepository, ::ownerId)
            .build()

    override fun buildSingleArgumentSearchSpec(needle: String?): Specification<Quiz> {
        if (needle.isNullOrEmpty())
            return emptySpecification

        return Specification
                .where(SpecificationBuilder.fk(authenticationFacade::user, Quiz_.owner))
                .and(SpecificationBuilder.ciContains(needle, Quiz_.title))
    }

    override fun serializePage(page: Page<Quiz>): PageDTO = page.toDTO { it.toFullDTO() }

    override fun serializeEntity(entity: Quiz): QuizFullDTO = entity.toFullDTO()

    private fun retrieveTopics(ids: Collection<Long>?) = ids?.let { jpaTopicRepository.findAllById(it).toMutableSet() }
            ?: mutableSetOf()

    private fun retrieveTags(ids: Collection<Long>?) = ids?.let { jpaTagRepository.findAllById(it).toMutableSet() }
            ?: mutableSetOf()

    private fun retrieveQuestions(ids: Collection<Long>?) = ids?.let { jpaQuestionRepository.findAllById(it).toMutableSet() }
            ?: mutableSetOf()

    private fun retrieveDifficultyScale(id: Long?) = id?.let { jpaDifficultyScaleRepository.findById(id).orElse(null) }

    @Transactional
    override fun create(createDTO: PatchQuizDTO): Quiz {
        validateCreate(createDTO)
        var entity = with(createDTO) {
            Quiz(
                    owner = authenticationFacade.user,
                    title = title!!,
                    topics = retrieveTopics(topics),
                    tags = retrieveTags(tags),
                    difficultyScale = retrieveDifficultyScale(difficultyScale)
            )
        }
        entity = repository.save(entity)
        val quizQuestions = retrieveQuestions(createDTO.questions).mapIndexed { index, question ->
            QuizQuestion(
                    owner = authenticationFacade.user,
                    quiz = entity,
                    question = question,
                    seq = index
            )
        }
        jpaQuizQuestionRepository.saveAll(quizQuestions)
        return entity
    }

    override fun patch(id: Long, patchDTO: PatchQuizDTO): Quiz {
        validatePatch(patchDTO)
        val modified = getInstance(id).apply {
            if (patchDTO.title != null) title = patchDTO.title
            if (patchDTO.topics != null) topics = retrieveTopics(patchDTO.topics)
            if (patchDTO.tags != null) tags = retrieveTags(patchDTO.tags)
            if (patchDTO.difficultyScale != null) difficultyScale = retrieveDifficultyScale(patchDTO.difficultyScale)
            updatedAt = Instant.now()
        }
        return repository.save(modified)
    }
}