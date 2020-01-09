package fm.force.quiz.core.controller

import fm.force.quiz.core.dto.QuestionFullDTO
import fm.force.quiz.core.dto.QuestionPatchDTO
import fm.force.quiz.core.entity.Question
import fm.force.quiz.core.repository.JpaQuestionRepository
import fm.force.quiz.core.service.QuestionService
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/questions")
class QuestionController(questionService: QuestionService)
    : AbstractCRUDController<Question, JpaQuestionRepository, QuestionPatchDTO, QuestionFullDTO>(questionService)
