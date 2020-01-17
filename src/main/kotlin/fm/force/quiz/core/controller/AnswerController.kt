package fm.force.quiz.core.controller

import fm.force.quiz.core.service.AnswerService
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("answers")
class AnswerController(answerService: AnswerService) : AnswerControllerType(answerService)
