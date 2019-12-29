package fm.force.quiz.core.controller

import fm.force.quiz.core.dto.PatchQuizDTO
import fm.force.quiz.core.dto.QuizDTO
import fm.force.quiz.core.entity.Quiz
import fm.force.quiz.core.repository.JpaQuizRepository
import fm.force.quiz.core.service.QuizService
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("quizzes")
class QuizController(service: QuizService)
    : AbstractCRUDController<Quiz, JpaQuizRepository, PatchQuizDTO, QuizDTO>(service)
