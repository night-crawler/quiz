package fm.force.quiz.core.controller

import fm.force.quiz.core.dto.CreateTopicDTO
import fm.force.quiz.core.dto.PaginationQuery
import fm.force.quiz.core.dto.SortQuery
import fm.force.quiz.core.dto.toDTO
import fm.force.quiz.core.service.TopicService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("topics")
class TopicController(private val topicService: TopicService) {

    @GetMapping("{topicId}")
    fun get(@PathVariable topicId: Long) = topicService.get(topicId)

    @PostMapping
    @ResponseStatus(value = HttpStatus.CREATED)
    fun create(@RequestBody createTopicDTO: CreateTopicDTO) = topicService.create(createTopicDTO).toDTO()

    @GetMapping
    fun find(
            paginationQuery: PaginationQuery,
            sortQuery: SortQuery,
            @RequestParam("query") query: String?
    ) = topicService.find(paginationQuery, sortQuery, query)
}
