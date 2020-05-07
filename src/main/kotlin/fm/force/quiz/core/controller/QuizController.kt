package fm.force.quiz.core.controller

import fm.force.quiz.common.dto.PaginationQuery
import fm.force.quiz.common.dto.QuizImportDTO
import fm.force.quiz.common.dto.QuizQuestionFullDTO
import fm.force.quiz.common.dto.QuizQuestionPatchDTO
import fm.force.quiz.common.dto.QuizQuestionSearchDTO
import fm.force.quiz.common.dto.QuizSessionPatchDTO
import fm.force.quiz.common.dto.SortQuery
import fm.force.quiz.common.mapper.toFullDTO
import fm.force.quiz.core.service.QuizImportService
import fm.force.quiz.core.service.QuizQuestionService
import fm.force.quiz.core.service.QuizService
import fm.force.quiz.core.service.QuizSessionService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("quizzes")
class QuizController(
    quizService: QuizService,
    private val quizQuestionService: QuizQuestionService,
    private val quizSessionService: QuizSessionService,
    private val quizImportService: QuizImportService
) : QuizControllerType(quizService) {

    override val service: QuizService = quizService

    @GetMapping("{quizId}/view")
    fun getRestricted(@PathVariable quizId: Long) =
        service.serializeRestrictedEntity(service.getEntity(quizId))

    @GetMapping("{quizId}/quizQuestions")
    fun findQuizQuestions(
        @PathVariable quizId: Long,
        paginationQuery: PaginationQuery,
        sortQuery: SortQuery
    ) = quizQuestionService.find(
        paginationQuery,
        sortQuery,
        QuizQuestionSearchDTO(quizId)
    )

    @PostMapping("{quizId}/quizQuestions")
    @ResponseStatus(HttpStatus.CREATED)
    fun createQuizQuestion(
        @PathVariable quizId: Long,
        @RequestBody createDTO: QuizQuestionPatchDTO
    ): QuizQuestionFullDTO {
        createDTO.quiz = quizId
        return quizQuestionService.serializeEntity(quizQuestionService.create(createDTO))
    }

    @GetMapping("{quizId}/quizQuestions/{quizQuestionId}")
    fun getQuizQuestion(
        @PathVariable quizId: Long,
        @PathVariable quizQuestionId: Long
    ) = quizQuestionService.getOwnedEntity(quizQuestionId).toFullDTO()

    @PatchMapping("{quizId}/quizQuestions/{quizQuestionId}")
    fun patchQuizQuestion(
        @PathVariable quizId: Long,
        @PathVariable quizQuestionId: Long,
        @RequestBody patchDTO: QuizQuestionPatchDTO
    ): QuizQuestionFullDTO {
        patchDTO.quiz = quizId
        return quizQuestionService.serializeEntity(quizQuestionService.patch(quizQuestionId, patchDTO))
    }

    @DeleteMapping("{quizId}/quizQuestions/{quizQuestionId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteQuizQuestion(
        @PathVariable quizId: Long,
        @PathVariable quizQuestionId: Long
    ) = quizQuestionService.deleteByQuizAndId(quizId, quizQuestionId)

    @PostMapping("{quizId}/startSession")
    @ResponseStatus(HttpStatus.CREATED)
    fun startSession(@PathVariable quizId: Long) =
        quizSessionService.create(QuizSessionPatchDTO(quizId)).let {
            quizSessionService.serializeEntity(it)
        }

    @PostMapping("import")
    @ResponseStatus(HttpStatus.CREATED)
    fun import(@RequestBody quizImportDTO: QuizImportDTO) =
         quizImportService.import(quizImportDTO.text)
             .let {
                 service.serializeEntity(it)
             }
}
