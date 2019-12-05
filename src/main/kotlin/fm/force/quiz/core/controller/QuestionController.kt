package fm.force.quiz.core.controller

import fm.force.quiz.core.dto.CreateQuestionDTO
import fm.force.quiz.core.entity.Question
import fm.force.quiz.core.exception.QuestionNotFound
import fm.force.quiz.core.repository.JpaQuestionRepository
import fm.force.quiz.core.service.QuestionService
import fm.force.quiz.security.service.AuthenticationFacade
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/questions")
class QuestionController(
        private val jpaQuestionRepository: JpaQuestionRepository,
        private val questionService: QuestionService,
        private val authenticationFacade: AuthenticationFacade
) {
    @GetMapping("{questionId}")
    fun getQuestion(@PathVariable questionId: Long): Question {
        return jpaQuestionRepository.findById(questionId).orElseThrow { QuestionNotFound(questionId) }
    }

    @PostMapping
    @ResponseStatus(value = HttpStatus.CREATED)
    fun createQuestion(@RequestBody question: CreateQuestionDTO) = questionService.create(question)
}
