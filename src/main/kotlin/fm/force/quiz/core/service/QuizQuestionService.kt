package fm.force.quiz.core.service

import am.ik.yavi.builder.ValidatorBuilder
import am.ik.yavi.builder.konstraintOnGroup
import fm.force.quiz.common.SpecificationBuilder
import fm.force.quiz.core.dto.PageDTO
import fm.force.quiz.core.dto.QuizQuestionFullDTO
import fm.force.quiz.core.dto.QuizQuestionPatchDTO
import fm.force.quiz.core.dto.QuizQuestionSearchDTO
import fm.force.quiz.core.dto.toDTO
import fm.force.quiz.core.dto.toFullDTO
import fm.force.quiz.core.entity.QuizQuestion
import fm.force.quiz.core.entity.QuizQuestion_
import fm.force.quiz.core.exception.NotFoundException
import fm.force.quiz.core.repository.QuestionRepository
import fm.force.quiz.core.repository.QuizQuestionRepository
import fm.force.quiz.core.repository.QuizRepository
import fm.force.quiz.core.validator.intConstraint
import fm.force.quiz.core.validator.mandatory
import fm.force.quiz.core.validator.ownedFkConstraint
import java.util.function.Predicate
import org.springframework.data.domain.Page
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class QuizQuestionService(
    quizQuestionRepository: QuizQuestionRepository,
    private val quizRepository: QuizRepository,
    private val questionRepository: QuestionRepository
) : QuizQuestionServiceType(quizQuestionRepository) {

    private val msgSeqTooBigPredicate = "Sequence number is too big"
    private val seqTooBigPredicate = Predicate<QuizQuestionPatchDTO> {
        if (it.seq == null || it.quiz == null || it.seq!! < 0) true
        else quizQuestionRepository.countByQuizId(it.quiz!!) >= it.seq!!
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
        .ownedFkConstraint(QuizQuestionPatchDTO::id, quizQuestionRepository, ::ownerId)
        .ownedFkConstraint(QuizQuestionPatchDTO::question, questionRepository, ::ownerId)
        .ownedFkConstraint(QuizQuestionPatchDTO::quiz, quizRepository, ::ownerId)
        .intConstraint(QuizQuestionPatchDTO::seq, -1..Int.MAX_VALUE) {
            constraintOnTarget(seqTooBigPredicate, "seq", "", msgSeqTooBigPredicate)
        }
        .build()

    @Transactional(readOnly = true)
    override fun serializePage(page: Page<QuizQuestion>): PageDTO = page.toDTO { it.toFullDTO() }

    @Transactional(readOnly = true)
    override fun serializeEntity(entity: QuizQuestion): QuizQuestionFullDTO =
        repository.refresh(entity).toFullDTO()

    private fun createInternal(quizId: Long, questionId: Long, seq: Int): QuizQuestion {
        repository.updateSeqBetween(quizId, seq, Int.MAX_VALUE, 1)
        val entity = QuizQuestion(
            quiz = quizRepository.findById(quizId).get(),
            question = questionRepository.findById(questionId).get(),
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
    override fun buildSearchSpec(search: QuizQuestionSearchDTO?): Specification<QuizQuestion> {
        val ownerEquals = SpecificationBuilder.fk(authenticationFacade::user, QuizQuestion_.owner)
        return Specification.where(ownerEquals)
    }

    @Transactional
    override fun delete(id: Long) = deletePrivate(getOwnedEntity(id))

    @Transactional
    fun deleteByQuizAndId(quizId: Long, id: Long) = deletePrivate(getInstanceByQuizIdAndId(quizId, id))

    private fun deletePrivate(instance: QuizQuestion) = with(repository) {
        updateSeqBetween(instance.quiz.id, instance.seq, Int.MAX_VALUE, -1)
        delete(instance)
    }

    private fun getInstanceByQuizIdAndId(quizId: Long, id: Long) = repository
        .getByQuizIdAndId(quizId, id)
        .orElseThrow { NotFoundException(id, QuizQuestion::class) }

    @Transactional
    override fun patch(id: Long, patchDTO: QuizQuestionPatchDTO): QuizQuestion {
        // Oh, Lord! I hate this off-by-one stuff :(
        validatePatch(patchDTO)
        var seqTo = patchDTO.seq!!
        val current = repository.findByIdAndOwner(id, authenticationFacade.user).orElseThrow {
            NotFoundException(id, QuizQuestion::class)
        }
        val seqFrom = current.seq
        if (seqFrom == seqTo)
            return current

        val count = repository.countByQuizId(current.quiz.id)
        if (count == 1)
            return current

        if (seqTo == -1)
            seqTo = count - 1

        if (seqTo > seqFrom) {
            // in case of moving 1 -> 3 in [0 1 2 3 4]
            // should not take the lower bound into account - it's being updated manually
            repository.updateSeqBetween(current.quiz.id, seqFrom + 1, seqTo, -1)
        } else {
            // in case if moving 3 -> 1 in [0 1 2 3 4]
            // reverse the range [1; 3]
            // no need to touch current upper bound
            repository.updateSeqBetween(current.quiz.id, seqTo, seqFrom - 1, 1)
        }
        current.seq = seqTo
        return repository.save(current)
    }
}
