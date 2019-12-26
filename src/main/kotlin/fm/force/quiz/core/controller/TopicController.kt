package fm.force.quiz.core.controller

import fm.force.quiz.core.dto.*
import fm.force.quiz.core.entity.Topic
import fm.force.quiz.core.repository.JpaTopicRepository
import fm.force.quiz.core.service.TopicService
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("topics")
class TopicController(topicService: TopicService)
    : AbstractCRUDController<Topic, JpaTopicRepository, PatchTopicDTO, TopicDTO>(topicService)
