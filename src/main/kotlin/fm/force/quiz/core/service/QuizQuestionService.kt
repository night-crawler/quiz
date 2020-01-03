package fm.force.quiz.core.service

import am.ik.yavi.builder.ValidatorBuilder
import am.ik.yavi.builder.konstraintOnGroup
import fm.force.quiz.core.dto.*
import fm.force.quiz.core.entity.QuizQuestion
import fm.force.quiz.core.repository.JpaQuestionRepository
import fm.force.quiz.core.repository.JpaQuizQuestionRepository
import fm.force.quiz.core.repository.JpaQuizRepository
import fm.force.quiz.core.validator.fkConstraint
import fm.force.quiz.core.validator.intConstraint
import fm.force.quiz.core.validator.mandatory
import org.springframework.data.domain.Page
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Service
import javax.transaction.Transactional


@Service
class QuizQuestionService(
        jpaQuizQuestionRepository: JpaQuizQuestionRepository,
        private val jpaQuizRepository: JpaQuizRepository,
        private val jpaQuestionRepository: JpaQuestionRepository
) : AbstractPaginatedCRUDService<QuizQuestion, JpaQuizQuestionRepository, QuizQuestionPatchDTO, QuizQuestionFullDTO>(
        jpaQuizQuestionRepository
) {
    override var dtoValidator = ValidatorBuilder.of<QuizQuestionPatchDTO>()
            .konstraintOnGroup(CRUDConstraintGroup.CREATE) {
                mandatory(QuizQuestionPatchDTO::question)
                mandatory(QuizQuestionPatchDTO::quiz)
                mandatory(QuizQuestionPatchDTO::seq)
            }
            .fkConstraint(QuizQuestionPatchDTO::id, jpaQuizQuestionRepository, ::ownerId)
            .fkConstraint(QuizQuestionPatchDTO::question, jpaQuizQuestionRepository, ::ownerId)
            .fkConstraint(QuizQuestionPatchDTO::quiz, jpaQuizRepository, ::ownerId)
            .intConstraint(QuizQuestionPatchDTO::seq, -1..Int.MAX_VALUE)
            .build()

    override fun serializePage(page: Page<QuizQuestion>): PageDTO = page.toDTO { it.toFullDTO() }

    override fun serializeEntity(entity: QuizQuestion): QuizQuestionFullDTO = entity.toFullDTO()

    @Transactional
    override fun create(createDTO: QuizQuestionPatchDTO): QuizQuestion {
        validateCreate(createDTO)
        val entity = with(createDTO) {
            QuizQuestion(
                    quiz = jpaQuizRepository.findById(quiz!!).get(),
                    question = jpaQuestionRepository.findById(question!!).get(),
                    owner = authenticationFacade.user,
                    seq = seq!!
            )
        }
        return repository.save(entity)
    }

    override fun buildSingleArgumentSearchSpec(needle: String?): Specification<QuizQuestion> {
        throw NotImplementedError()
    }
}