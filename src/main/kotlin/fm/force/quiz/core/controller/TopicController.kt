package fm.force.quiz.core.controller

import fm.force.quiz.core.dto.SearchQueryDTO
import fm.force.quiz.core.dto.TopicFullDTO
import fm.force.quiz.core.dto.TopicPatchDTO
import fm.force.quiz.core.entity.Topic
import fm.force.quiz.core.repository.TopicRepository
import fm.force.quiz.core.service.TopicService
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("topics")
class TopicController(topicService: TopicService) :
    AbstractCRUDController<Topic, TopicRepository, TopicPatchDTO, TopicFullDTO, SearchQueryDTO>(topicService)
