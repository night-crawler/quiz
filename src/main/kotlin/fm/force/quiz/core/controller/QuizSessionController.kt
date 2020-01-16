package fm.force.quiz.core.controller

import fm.force.quiz.core.dto.QuizSessionFullDTO
import fm.force.quiz.core.dto.QuizSessionPatchDTO
import fm.force.quiz.core.dto.QuizSessionSearchDTO
import fm.force.quiz.core.entity.QuizSession
import fm.force.quiz.core.repository.QuizSessionRepository
import fm.force.quiz.core.service.QuizSessionService
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("quizSessions")
class QuizSessionController(quizSessionService: QuizSessionService) :
    AbstractCRUDController<QuizSession, QuizSessionRepository, QuizSessionPatchDTO, QuizSessionFullDTO, QuizSessionSearchDTO>(quizSessionService) {

    override val service = quizSessionService

    @PostMapping("{sessionId}/cancel")
    fun cancel(@PathVariable sessionId: Long) = service.cancel(sessionId)

    @PostMapping("{sessionId}/complete")
    fun complete(@PathVariable sessionId: Long) = service.complete(sessionId)
}
