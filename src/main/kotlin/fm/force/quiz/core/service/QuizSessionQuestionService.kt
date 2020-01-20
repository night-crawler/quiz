package fm.force.quiz.core.service

import fm.force.quiz.common.SpecificationBuilder
import fm.force.quiz.core.dto.PageDTO
import fm.force.quiz.core.dto.QuizSessionQuestionPatchDTO
import fm.force.quiz.core.dto.QuizSessionQuestionRestrictedDTO
import fm.force.quiz.core.dto.QuizSessionQuestionSearchDTO
import fm.force.quiz.core.dto.toDTO
import fm.force.quiz.core.dto.toRestrictedDTO
import fm.force.quiz.core.entity.QuizSessionQuestion
import fm.force.quiz.core.entity.QuizSessionQuestion_
import fm.force.quiz.core.repository.QuestionRepository
import fm.force.quiz.core.repository.QuizSessionQuestionRepository
import fm.force.quiz.core.repository.QuizSessionRepository
import org.springframework.data.domain.Page
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class QuizSessionQuestionService(
    private val questionRepository: QuestionRepository,
    private val quizSessionRepository: QuizSessionRepository,
    quizSessionQuestionRepository: QuizSessionQuestionRepository
) : QuizSessionQuestionType(quizSessionQuestionRepository) {

    override fun buildSearchSpec(search: QuizSessionQuestionSearchDTO?): Specification<QuizSessionQuestion> {
        val ownerEquals = SpecificationBuilder.fk(authenticationFacade::user, QuizSessionQuestion_.owner)
        if (search == null) return ownerEquals

        var spec = Specification.where(ownerEquals)
        with(SpecificationBuilder) {
            val quizSessionId = search.quizSession
            if (search.originalQuestion != null)
                spec = spec.and(fk(questionRepository.getEntity(search.originalQuestion), QuizSessionQuestion_.originalQuestion))
            if (quizSessionId != null)
                spec = spec.and(fk(quizSessionRepository.getEntity(quizSessionId), QuizSessionQuestion_.quizSession))
            if (search.text != null)
                spec = spec.and(ciContains(search.text, QuizSessionQuestion_.text))
        }

        return spec
    }

    @Transactional
    override fun serializePage(page: Page<QuizSessionQuestion>): PageDTO =
        page.toDTO { it.toRestrictedDTO() }

    @Transactional
    override fun serializeEntity(entity: QuizSessionQuestion): QuizSessionQuestionRestrictedDTO =
        repository.refresh(entity).toRestrictedDTO()

    @Transactional
    override fun create(createDTO: QuizSessionQuestionPatchDTO) =
        throw NotImplementedError("QuizSessionQuestion cannot be created")
}
