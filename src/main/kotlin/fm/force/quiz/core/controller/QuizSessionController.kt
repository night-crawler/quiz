package fm.force.quiz.core.controller

import fm.force.quiz.common.dto.PageDTO
import fm.force.quiz.common.dto.PaginationQuery
import fm.force.quiz.common.dto.QuizSessionAnswerPatchDTO
import fm.force.quiz.common.dto.QuizSessionAnswerSearchDTO
import fm.force.quiz.common.dto.QuizSessionQuestionSearchDTO
import fm.force.quiz.common.dto.SortQuery
import fm.force.quiz.core.service.QuizSessionAnswerService
import fm.force.quiz.core.service.QuizSessionQuestionService
import fm.force.quiz.core.service.QuizSessionService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("quizSessions")
class QuizSessionController(
    private val quizSessionQuestionService: QuizSessionQuestionService,
    private val quizSessionAnswerService: QuizSessionAnswerService,
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

    @PostMapping("{sessionId}/doAnswer")
    fun doAnswer(@PathVariable sessionId: Long, @RequestBody createDTO: QuizSessionAnswerPatchDTO) =
        createDTO
            .apply { session = sessionId }
            .let { quizSessionAnswerService.create(it) }
            .let { quizSessionAnswerService.serializeEntity(it) }

    @GetMapping("{sessionId}/answers")
    fun findAnswers(
        @PathVariable sessionId: Long,
        paginationQuery: PaginationQuery,
        sortQuery: SortQuery
    ): PageDTO {
        val search = QuizSessionAnswerSearchDTO(quizSession = sessionId)
        return quizSessionAnswerService.find(paginationQuery, sortQuery, search)
    }
}
