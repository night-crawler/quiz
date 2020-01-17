package fm.force.quiz.core.controller

import fm.force.quiz.core.service.QuestionService
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/questions")
class QuestionController(questionService: QuestionService) : QuestionControllerType(questionService)
