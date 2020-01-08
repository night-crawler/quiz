package fm.force.quiz.core.service

import am.ik.yavi.builder.ValidatorBuilder
import am.ik.yavi.builder.konstraintOnGroup
import fm.force.quiz.core.dto.*
import fm.force.quiz.core.entity.QuizQuestion
import fm.force.quiz.core.exception.NotFoundException
import fm.force.quiz.core.repository.JpaQuestionRepository
import fm.force.quiz.core.repository.JpaQuizQuestionRepository
import fm.force.quiz.core.repository.JpaQuizRepository
import fm.force.quiz.core.validator.fkConstraint
import fm.force.quiz.core.validator.intConstraint
import fm.force.quiz.core.validator.mandatory
import org.springframework.data.domain.Page
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Service
import java.lang.IllegalArgumentException
import java.util.function.Predicate
import javax.transaction.Transactional


@Service
class QuizQuestionService(
        jpaQuizQuestionRepository: JpaQuizQuestionRepository,
        private val jpaQuizRepository: JpaQuizRepository,
        private val jpaQuestionRepository: JpaQuestionRepository
) : AbstractPaginatedCRUDService<QuizQuestion, JpaQuizQuestionRepository, QuizQuestionPatchDTO, QuizQuestionFullDTO>(
        jpaQuizQuestionRepository
) {
    private val msgSeqTooBigPredicate = "Sequence number is too big"
    private val seqTooBigPredicate = Predicate<QuizQuestionPatchDTO> {
        if (it.seq == null || it.quiz == null || it.seq!! < 0) true
        else jpaQuizQuestionRepository.countByQuizId(it.quiz!!) >= it.seq!!
    }

    override var dtoValidator = ValidatorBuilder.of<QuizQuestionPatchDTO>()
            .konstraintOnGroup(CRUDConstraintGroup.CREATE) {
                mandatory(QuizQuestionPatchDTO::question)
                mandatory(QuizQuestionPatchDTO::quiz)
                mandatory(QuizQuestionPatchDTO::seq)
            }
            .konstraintOnGroup(CRUDConstraintGroup.UPDATE) {
                mandatory(QuizQuestionPatchDTO::seq)
            }
            .fkConstraint(QuizQuestionPatchDTO::id, jpaQuizQuestionRepository, ::ownerId)
            .fkConstraint(QuizQuestionPatchDTO::question, jpaQuestionRepository, ::ownerId)
            .fkConstraint(QuizQuestionPatchDTO::quiz, jpaQuizRepository, ::ownerId)
            .intConstraint(QuizQuestionPatchDTO::seq, -1..Int.MAX_VALUE) {
                constraintOnTarget(seqTooBigPredicate, "seq", "", msgSeqTooBigPredicate)
            }
            .build()

    override fun serializePage(page: Page<QuizQuestion>): PageDTO = page.toDTO { it.toFullDTO() }

    override fun serializeEntity(entity: QuizQuestion): QuizQuestionFullDTO = entity.toFullDTO()

    private fun createInternal(quizId: Long, questionId: Long, seq: Int): QuizQuestion {
        repository.updateSeqAfter(quizId, seq, 1)
        val entity = QuizQuestion(
                quiz = jpaQuizRepository.findById(quizId).get(),
                question = jpaQuestionRepository.findById(questionId).get(),
                owner = authenticationFacade.user,
                seq = seq
        )
        return repository.save(entity)
    }

    @Transactional
    override fun create(createDTO: QuizQuestionPatchDTO): QuizQuestion {
        validateCreate(createDTO)
        var seq = createDTO.seq!!
        val quiz = createDTO.quiz!!
        val question = createDTO.question!!

        val maxSeq = repository.countByQuizId(quiz)
        if (seq == -1) seq = maxSeq

        return createInternal(quiz, question, seq)
    }

    @Transactional
    override fun delete(id: Long) {
        val current = getInstance(id)
        repository.updateSeqAfter(current.quiz.id, current.seq, -1)
        repository.delete(current)
    }

    @Transactional
    override fun patch(id: Long, patchDTO: QuizQuestionPatchDTO): QuizQuestion {
        validatePatch(patchDTO)
        val seq = patchDTO.seq!!
        val current = repository.findByIdAndOwner(id, authenticationFacade.user).orElseThrow {
            NotFoundException(id, QuizQuestion::class)
        }
        val curSeq = current.seq
        if (curSeq == seq) return current

        delete(current.id)

        val newSeq = when {
            seq == -1 -> repository.countByQuizId(current.quiz.id)
            seq > curSeq -> seq - 1
            seq < curSeq -> seq
            else -> throw IllegalArgumentException("Should never happen")
        }
        return createInternal(current.quiz.id, current.question.id, newSeq)
    }

    override fun buildSingleArgumentSearchSpec(needle: String?): Specification<QuizQuestion> {
        throw NotImplementedError()
    }
}