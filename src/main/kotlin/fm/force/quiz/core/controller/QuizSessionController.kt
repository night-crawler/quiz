package fm.force.quiz.core.controller

import fm.force.quiz.core.service.QuizSessionService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("quizSessions")
class QuizSessionController(quizSessionService: QuizSessionService) : QuizSessionControllerType(quizSessionService) {

    override val service = quizSessionService

    @GetMapping("{sessionId}/questions")
    fun getQuestions(@PathVariable sessionId: Long) {
    }

    @PostMapping("{sessionId}/cancel")
    fun cancel(@PathVariable sessionId: Long) = service.cancel(sessionId)

    @PostMapping("{sessionId}/complete")
    fun complete(@PathVariable sessionId: Long) = service.complete(sessionId)
}
