package fm.force.quiz.core.service

import am.ik.yavi.builder.ValidatorBuilder
import fm.force.quiz.core.dto.PageDTO
import fm.force.quiz.core.dto.QuizSessionAnswerFullDTO
import fm.force.quiz.core.dto.QuizSessionAnswerPatchDTO
import fm.force.quiz.core.dto.toDTO
import fm.force.quiz.core.dto.toFullDTO
import fm.force.quiz.core.dto.toRestrictedDTO
import fm.force.quiz.core.entity.QuizSessionAnswer
import fm.force.quiz.core.repository.QuizSessionAnswerRepository
import org.springframework.data.domain.Page
import org.springframework.stereotype.Service

@Service
class QuizSessionAnswerService(quizSessionAnswerRepository: QuizSessionAnswerRepository) :
    QuizSessionAnswerServiceType(quizSessionAnswerRepository) {

    override var dtoValidator = ValidatorBuilder.of<QuizSessionAnswerPatchDTO>()
        .build()

    override fun serializePage(page: Page<QuizSessionAnswer>): PageDTO =
        page.toDTO { it.toRestrictedDTO() }

    override fun serializeEntity(entity: QuizSessionAnswer): QuizSessionAnswerFullDTO =
        entity.toFullDTO()

    override fun create(createDTO: QuizSessionAnswerPatchDTO): QuizSessionAnswer {
        TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
    }
}
