package fm.force.quiz.controller

import fm.force.quiz.dto.GetQuestionsDTO
import fm.force.quiz.entity.Question
import fm.force.quiz.repository.JpaQuestionRepository
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("admin")
class AdminController(
        private val questionRepository: JpaQuestionRepository
) {

    @GetMapping("questions")
    fun questions(@RequestParam(name="q", defaultValue = "") q: String): MutableList<Question> {

        questionRepository.save(Question(text="123"))
        return questionRepository.findAll()
    }
}