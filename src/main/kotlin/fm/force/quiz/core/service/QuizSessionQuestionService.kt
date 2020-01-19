package fm.force.quiz.core.service

import fm.force.quiz.core.dto.PageDTO
import fm.force.quiz.core.dto.QuizSessionQuestionPatchDTO
import fm.force.quiz.core.dto.QuizSessionQuestionRestrictedDTO
import fm.force.quiz.core.repository.QuizSessionQuestionRepository
import org.springframework.data.domain.Page
import org.springframework.stereotype.Service

@Service
class QuizSessionQuestionService(
    quizSessionQuestionRepository: QuizSessionQuestionRepository
) : QuizSessionQuestion(quizSessionQuestionRepository) {
    override fun serializePage(page: Page<fm.force.quiz.core.entity.QuizSessionQuestion>): PageDTO {
        TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
    }

    override fun serializeEntity(entity: fm.force.quiz.core.entity.QuizSessionQuestion): QuizSessionQuestionRestrictedDTO {
        TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
    }

    override fun create(createDTO: QuizSessionQuestionPatchDTO): fm.force.quiz.core.entity.QuizSessionQuestion {
        TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
    }
}
