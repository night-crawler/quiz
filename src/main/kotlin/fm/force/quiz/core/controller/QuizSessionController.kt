package fm.force.quiz.core.controller

import fm.force.quiz.core.dto.PageDTO
import fm.force.quiz.core.dto.PaginationQuery
import fm.force.quiz.core.dto.QuizSessionQuestionSearchDTO
import fm.force.quiz.core.dto.SortQuery
import fm.force.quiz.core.service.QuizSessionQuestionService
import fm.force.quiz.core.service.QuizSessionService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("quizSessions")
class QuizSessionController(
    private val quizSessionQuestionService: QuizSessionQuestionService,
    quizSessionService: QuizSessionService
) : QuizSessionControllerType(quizSessionService) {

    override val service = quizSessionService

    @PostMapping("{sessionId}/cancel")
    fun cancel(@PathVariable sessionId: Long) =
        service.serializeEntity(service.cancel(sessionId))

    @PostMapping("{sessionId}/complete")
    fun complete(@PathVariable sessionId: Long) =
        service.serializeEntity(service.complete(sessionId))

    @GetMapping("{sessionId}/questions")
    fun findQuestions(
        @PathVariable sessionId: Long,
        paginationQuery: PaginationQuery,
        sortQuery: SortQuery,
        search: QuizSessionQuestionSearchDTO?
    ): PageDTO {
        val quizSessionQuestionSearchDTO = QuizSessionQuestionSearchDTO(
            quizSession = sessionId,
            originalQuestion = search?.originalQuestion,
            text = search?.text
        )
        return quizSessionQuestionService.find(paginationQuery, sortQuery, quizSessionQuestionSearchDTO)
    }
}
