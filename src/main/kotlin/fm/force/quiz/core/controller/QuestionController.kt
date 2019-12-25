package fm.force.quiz.core.controller

import fm.force.quiz.core.dto.CreateQuestionDTO
import fm.force.quiz.core.dto.QuestionDTO
import fm.force.quiz.core.entity.Question
import fm.force.quiz.core.repository.JpaQuestionRepository
import fm.force.quiz.core.service.QuestionService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/questions")
class QuestionController(questionService: QuestionService)
    : AbstractCRUDController<Question, JpaQuestionRepository, CreateQuestionDTO, QuestionDTO>(questionService)
