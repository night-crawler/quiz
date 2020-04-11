package fm.force.quiz.core.controller

import fm.force.quiz.core.dto.TopicFullDTO
import fm.force.quiz.core.dto.TopicPatchDTO
import fm.force.quiz.core.service.TopicService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("topics")
class TopicController(topicService: TopicService) : TopicControllerType(topicService) {
    override val service: TopicService = topicService

    @PostMapping("getOrCreate")
    @ResponseBody
    fun getOrCreateTopic(@RequestBody topicPatchDTO: TopicPatchDTO): ResponseEntity<TopicFullDTO> {
        val (topic, isCreated) = service.getOrCreate(topicPatchDTO)
        val topicFullDTO = service.serializeEntity(topic)
        val status = if (isCreated) HttpStatus.CREATED else HttpStatus.OK
        return ResponseEntity(topicFullDTO, status)
    }

}
