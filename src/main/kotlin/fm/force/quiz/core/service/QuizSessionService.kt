package fm.force.quiz.core.service

import am.ik.yavi.builder.ValidatorBuilder
import am.ik.yavi.builder.konstraint
import am.ik.yavi.builder.konstraintOnGroup
import fm.force.quiz.common.SpecificationBuilder
import fm.force.quiz.common.dto.PageDTO
import fm.force.quiz.common.dto.QuizSessionFullDTO
import fm.force.quiz.common.dto.QuizSessionPatchDTO
import fm.force.quiz.common.dto.QuizSessionSearchDTO
import fm.force.quiz.common.mapper.toDTO
import fm.force.quiz.common.mapper.toFullDTO
import fm.force.quiz.core.entity.QuizQuestion
import fm.force.quiz.core.entity.QuizSession
import fm.force.quiz.core.entity.QuizSessionQuestion
import fm.force.quiz.core.entity.QuizSessionQuestionAnswer
import fm.force.quiz.core.entity.QuizSession_
import fm.force.quiz.core.exception.ValidationError
import fm.force.quiz.core.repository.DifficultyScaleRepository
import fm.force.quiz.core.repository.QuizRepository
import fm.force.quiz.core.repository.QuizSessionQuestionAnswerRepository
import fm.force.quiz.core.repository.QuizSessionQuestionRepository
import fm.force.quiz.core.repository.QuizSessionRepository
import fm.force.quiz.core.validator.fkConstraint
import fm.force.quiz.core.validator.instantConstraint
import java.time.Instant
import org.springframework.data.domain.Page
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class QuizSessionService(
    private val quizRepository: QuizRepository,
    private val difficultyScaleRepository: DifficultyScaleRepository,
    private val quizSessionQuestionAnswerRepository: QuizSessionQuestionAnswerRepository,
    private val quizSessionQuestionRepository: QuizSessionQuestionRepository,
    quizSessionRepository: QuizSessionRepository
) : QuizSessionServiceType(quizSessionRepository) {
    private val msgAlreadyCancelled = "Session has already been cancelled"
    private val msgAlreadyCompleted = "Session has already been completed"
    private val msgSessionIsExpired = "Session is expired"

    override var dtoValidator = ValidatorBuilder.of<QuizSessionPatchDTO>()
        .fkConstraint(QuizSessionPatchDTO::quiz, quizRepository)
        .build()

    override var entityValidator = ValidatorBuilder.of<QuizSession>()
        .konstraintOnGroup(TransitionConstraintGroup.CANCEL) {
            konstraint(QuizSession::isCancelled) { isFalse.message(msgAlreadyCancelled) }
            konstraint(QuizSession::isCompleted) { isFalse.message(msgAlreadyCompleted) }
        }
        .konstraintOnGroup(TransitionConstraintGroup.COMPLETE) {
            konstraint(QuizSession::isCancelled) { isFalse.message(msgAlreadyCancelled) }
            konstraint(QuizSession::isCompleted) { isFalse.message(msgAlreadyCompleted) }
        }
        .instantConstraint(QuizSession::validTill, errorTemplate = msgSessionIsExpired)
        .build()

    @Transactional
    override fun create(createDTO: QuizSessionPatchDTO): QuizSession {
        validateCreate(createDTO)

        val quiz = quizRepository.getEntity(createDTO.quiz)
        var entity = QuizSession(
            owner = authenticationFacade.user,
            quiz = quiz,
            difficultyScale = quiz.difficultyScale,
            isCancelled = false,
            isCompleted = false
        )
        entity = repository.save(entity)
        cloneQuizQuestions(entity)

        return entity
    }

    private fun cloneQuizQuestions(entity: QuizSession) {
        val clonedQuestions = mutableListOf<QuizSessionQuestion>()
        val clonedAnswers = mutableListOf<QuizSessionQuestionAnswer>()

        entity.quiz?.quizQuestions?.forEach { qq ->
            val (quizSessionQuestion, quizSessionQuestionAnswers) =
                cloneQuizQuestion(entity, qq)

            clonedQuestions.add(quizSessionQuestion)
            clonedAnswers.addAll(quizSessionQuestionAnswers)
        }

        quizSessionQuestionRepository.saveAll(clonedQuestions)
        quizSessionQuestionAnswerRepository.saveAll(clonedAnswers)
    }

    private fun cloneQuizQuestion(
        entity: QuizSession,
        quizQuestion: QuizQuestion
    ): Pair<QuizSessionQuestion, List<QuizSessionQuestionAnswer>> {
        val owner = authenticationFacade.user
        val quizSessionQuestion = QuizSessionQuestion(
            owner = owner,
            quizSession = entity,
            originalQuestion = quizQuestion.question,
            title = quizQuestion.question.title,
            text = quizQuestion.question.text,
            help = quizQuestion.question.help,
            seq = quizQuestion.seq
        )

        val correctAnswersMap = quizQuestion.question.correctAnswers.map { it.id to it }.toMap()
        val answers = quizQuestion.question.answers.map {
            QuizSessionQuestionAnswer(
                owner = owner,
                quizSession = entity,
                quizSessionQuestion = quizSessionQuestion,
                originalAnswer = it,
                text = it.text,
                isCorrect = correctAnswersMap[it.id] != null
            )
        }
        return quizSessionQuestion to answers
    }

    override fun buildSearchSpec(search: QuizSessionSearchDTO?): Specification<QuizSession> {
        val ownerEquals = SpecificationBuilder.fk(authenticationFacade::user, QuizSession_.owner)
        if (search == null) return ownerEquals

        var spec = ownerEquals

        with(SpecificationBuilder) {
            if (search.quiz != null)
                spec = spec.and(fk(quizRepository.getEntity((search.quiz)), QuizSession_.quiz))!!

            if (search.difficultyScale != null)
                spec = spec.and(
                    fk(
                        difficultyScaleRepository.getEntity((search.difficultyScale)),
                        QuizSession_.difficultyScale
                    )
                )!!

            if (search.isCancelled != null)
                spec = spec.and(equals(search.isCancelled, QuizSession_.isCancelled))!!

            if (search.isCompleted != null)
                spec = spec.and(equals(search.isCompleted, QuizSession_.isCompleted))!!
        }

        return spec
    }

    @Transactional(readOnly = true)
    override fun serializePage(page: Page<QuizSession>): PageDTO = page.toDTO { it.toFullDTO() }

    @Transactional(readOnly = true)
    override fun serializeEntity(entity: QuizSession): QuizSessionFullDTO =
        repository.refresh(entity).toFullDTO()

    @Transactional
    fun cancel(id: Long) = getOwnedEntity(id)
        .apply {
            entityValidator
                .validate(this, TransitionConstraintGroup.CANCEL)
                .throwIfInvalid { ValidationError(it) }
        }
        .apply {
            val now = Instant.now()
            isCancelled = true
            cancelledAt = now
            updatedAt = now
        }
        .let { repository.save(it) }

    @Transactional
    fun complete(id: Long) = getOwnedEntity(id)
        .apply {
            entityValidator
                .validate(this, TransitionConstraintGroup.COMPLETE)
                .throwIfInvalid { ValidationError(it) }
        }
        .apply {
            val now = Instant.now()
            isCompleted = true
            completedAt = now
            updatedAt = now
        }
        .let { repository.save(it) }

    // Nothing to patch
    @Transactional
    override fun patch(id: Long, patchDTO: QuizSessionPatchDTO): QuizSession =
        getOwnedEntity(id)

    @Transactional
    fun getRemainingQuestionIds(id: Long) = getOwnedEntity(id).let {
        quizSessionQuestionRepository.getRemainingQuestionIds(id)
    }

    @Transactional
    fun getRemainingQuestionCount(id: Long) = getOwnedEntity(id).let {
        quizSessionQuestionRepository.getRemainingQuestionCount(id)
    }
}
