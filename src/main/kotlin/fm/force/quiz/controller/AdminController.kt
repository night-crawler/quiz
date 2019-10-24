package fm.force.quiz.controller

import fm.force.quiz.entity.Question
import fm.force.quiz.repository.JpaQuestionRepository
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

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

    @GetMapping("dummy")
    fun dummy(): String {
        return "That's all"
    }
}