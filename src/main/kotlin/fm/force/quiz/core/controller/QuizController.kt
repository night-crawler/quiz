package fm.force.quiz.core.controller

import fm.force.quiz.core.dto.*
import fm.force.quiz.core.entity.Quiz
import fm.force.quiz.core.repository.JpaQuizRepository
import fm.force.quiz.core.service.QuizQuestionService
import fm.force.quiz.core.service.QuizService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("quizzes")
class QuizController(quizService: QuizService, private val quizQuestionService: QuizQuestionService)
    : AbstractCRUDController<Quiz, JpaQuizRepository, PatchQuizDTO, QuizFullDTO>(quizService) {

    @RequestMapping("{quizId}/quizQuestions")
    @PostMapping
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
    ): QuizQuestionFullDTO {
        return quizQuestionService.getInstance(quizQuestionId).toFullDTO()
    }

    @PatchMapping("{quizId}/quizQuestions/{quizQuestionId}")
    fun patchQuizQuestion(
            @PathVariable quizId: Long,
            @PathVariable quizQuestionId: Long,
            @RequestBody patchDTO: QuizQuestionPatchDTO
    ): QuizQuestionFullDTO {
        patchDTO.quiz = quizId
        return quizQuestionService.patch(quizQuestionId, patchDTO).toFullDTO()
    }

    @DeleteMapping("{quizId}/quizQuestions/{quizQuestionId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteQuizQuestion(
            @PathVariable quizId: Long,
            @PathVariable quizQuestionId: Long
    ) = quizQuestionService.deleteByQuizAndId(quizId, quizQuestionId)
}
