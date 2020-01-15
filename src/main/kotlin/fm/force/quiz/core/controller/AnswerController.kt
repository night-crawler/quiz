package fm.force.quiz.core.controller

import fm.force.quiz.core.dto.AnswerFullDTO
import fm.force.quiz.core.dto.AnswerPatchDTO
import fm.force.quiz.core.dto.SearchQueryDTO
import fm.force.quiz.core.entity.Answer
import fm.force.quiz.core.repository.JpaAnswerRepository
import fm.force.quiz.core.service.AnswerService
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("answers")
class AnswerController(answerService: AnswerService)
    : AbstractCRUDController<Answer, JpaAnswerRepository, AnswerPatchDTO, AnswerFullDTO, SearchQueryDTO>(answerService)
