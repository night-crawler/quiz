package fm.force.quiz.core.controller

import fm.force.quiz.core.dto.*
import fm.force.quiz.core.entity.Answer
import fm.force.quiz.core.repository.JpaAnswerRepository
import fm.force.quiz.core.service.AnswerService
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("answers")
class AnswerController(answerService: AnswerService)
    : AbstractCRUDController<Answer, JpaAnswerRepository, CreateAnswerDTO, AnswerDTO>(answerService)
