package fm.force.quiz.core.controller

import fm.force.quiz.core.dto.CreateQuestionDTO
import fm.force.quiz.core.entity.Question
import fm.force.quiz.core.exception.QuestionNotFound
import fm.force.quiz.core.repository.JpaQuestionRepository
import fm.force.quiz.core.service.QuestionValidationService
import fm.force.quiz.security.service.AuthenticationFacade
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/questions")
class QuestionController(
        private val jpaQuestionRepository: JpaQuestionRepository,
        private val questionValidationService: QuestionValidationService,
        private val authenticationFacade: AuthenticationFacade
) {
    @GetMapping("{questionId}")
    fun getQuestion(@PathVariable questionId: Long): Question {
        println("${authenticationFacade.user.id}")
        return jpaQuestionRepository.findById(questionId).orElseThrow { QuestionNotFound(questionId) }
    }

    @PostMapping
    fun createQuestion(@RequestBody question: CreateQuestionDTO) {
        val qwe = questionValidationService.validate(question)
    }
}