package fm.force.quiz.core.controller

import fm.force.quiz.core.dto.TopicFullDTO
import fm.force.quiz.core.dto.TopicPatchDTO
import fm.force.quiz.core.entity.Topic
import fm.force.quiz.core.repository.JpaTopicRepository
import fm.force.quiz.core.service.TopicService
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("topics")
class TopicController(topicService: TopicService)
    : AbstractCRUDController<Topic, JpaTopicRepository, TopicPatchDTO, TopicFullDTO>(topicService)
