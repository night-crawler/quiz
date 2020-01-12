package fm.force.quiz.core.service

import am.ik.yavi.builder.ValidatorBuilder
import am.ik.yavi.builder.konstraint
import am.ik.yavi.builder.konstraintOnGroup
import fm.force.quiz.common.SpecificationBuilder
import fm.force.quiz.core.dto.*
import fm.force.quiz.core.entity.QuizSession
import fm.force.quiz.core.entity.QuizSession_
import fm.force.quiz.core.exception.ValidationError
import fm.force.quiz.core.repository.JpaDifficultyScaleRepository
import fm.force.quiz.core.repository.JpaQuizRepository
import fm.force.quiz.core.repository.JpaQuizSessionRepository
import fm.force.quiz.core.validator.fkConstraint
import fm.force.quiz.core.validator.instantConstraint
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Service
import java.time.Instant
import javax.transaction.Transactional


@Service
class QuizSessionService(
        private val jpaQuizRepository: JpaQuizRepository,
        private val jpaDifficultyScaleRepository: JpaDifficultyScaleRepository,
        jpaQuizSessionRepository: JpaQuizSessionRepository
) : AbstractPaginatedCRUDService<QuizSession, JpaQuizSessionRepository, QuizSessionPatchDTO, QuizSessionFullDTO>(jpaQuizSessionRepository) {

    override var dtoValidator = ValidatorBuilder.of<QuizSessionPatchDTO>()
            .fkConstraint(QuizSessionPatchDTO::quiz, jpaQuizRepository)
            .build()

    override var entityValidator = ValidatorBuilder.of<QuizSession>()
            .konstraintOnGroup(TransitionConstraintGroup.CANCEL) {
                konstraint(QuizSession::isCancelled) { isFalse.message("Session has already been cancelled, cannot cancel") }
                konstraint(QuizSession::isCompleted) { isFalse.message("Session has already been completed, cannot cancel") }
            }
            .konstraintOnGroup(TransitionConstraintGroup.COMPLETE) {
                konstraint(QuizSession::isCancelled) { isFalse.message("Session has already been cancelled, cannot complete") }
                konstraint(QuizSession::isCompleted) { isFalse.message("Session has already been completed, cannot complete") }
            }
            .instantConstraint(QuizSession::validTill, errorTemplate = "Session is no more valid")
            .build()

    override fun create(createDTO: QuizSessionPatchDTO): QuizSession {
        validateCreate(createDTO)
        val entity = with(createDTO) {
            QuizSession(
                    owner = authenticationFacade.user,
                    quiz = jpaQuizRepository.getEntity(quiz),
                    isCancelled = false,
                    isCompleted = false
            )
        }
        return repository.save(entity)
    }

    fun buildSearchSpec(search: QuizSessionSearchDTO?): Specification<QuizSession> {
        val ownerEquals = SpecificationBuilder.fk(authenticationFacade::user, QuizSession_.owner)
        if (search == null) return ownerEquals

        var spec = Specification.where(ownerEquals)

        with(SpecificationBuilder) {
            if (search.quiz != null)
                spec = spec.and(fk({ jpaQuizRepository.getEntity((search.quiz)) }, QuizSession_.quiz))

            if (search.difficultyScale != null)
                spec = spec.and(fk({ jpaDifficultyScaleRepository.getEntity((search.difficultyScale)) }, QuizSession_.difficultyScale))

            if (search.isCancelled != null)
                spec = spec.and(equals(search.isCancelled, QuizSession_.isCancelled))

            if (search.isCompleted != null)
                spec = spec.and(equals(search.isCompleted, QuizSession_.isCompleted))
        }

        return spec
    }

    @Transactional
    fun find(
            paginationQuery: PaginationQuery,
            sortQuery: SortQuery,
            search: QuizSessionSearchDTO?
    ): PageDTO {
        val pagination = paginationService.getPagination(paginationQuery)
        val sorting = sortingService.getSorting(sortQuery)
        val pageRequest = PageRequest.of(pagination.page, pagination.pageSize, sorting)
        val page = repository.findAll(buildSearchSpec(search), pageRequest)
        return serializePage(page)
    }

    override fun serializePage(page: Page<QuizSession>): PageDTO = page.toDTO { it.toFullDTO() }

    override fun serializeEntity(entity: QuizSession): QuizSessionFullDTO = entity.toFullDTO()

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
}