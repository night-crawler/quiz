package fm.force.quiz.controller

import fm.force.quiz.dto.GetQuestionsDTO
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("admin")
class AdminController {

    @GetMapping("questions")
    fun questions(@RequestParam(name="q", defaultValue = "") q: String) {
        println("Q WE? $q")
    }
}