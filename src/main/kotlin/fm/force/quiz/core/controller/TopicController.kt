package fm.force.quiz.core.controller

import fm.force.quiz.core.service.TopicService
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("topics")
class TopicController(topicService: TopicService) : TopicControllerType(topicService)
