package fm.force.quiz.core.controller

import fm.force.quiz.core.service.TopicService
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping


@Controller
@RequestMapping("topics")
class TopicController(
        private val topicService: TopicService
) {

    @GetMapping("{topicId}")
    fun getTopic(@PathVariable topicId: Long){

    }

}