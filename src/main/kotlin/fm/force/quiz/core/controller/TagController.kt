package fm.force.quiz.core.controller

import fm.force.quiz.core.dto.*
import fm.force.quiz.core.service.TagService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("tags")
class TagController(private val tagService: TagService) {

    @GetMapping("{tagId}")
    fun get(@PathVariable tagId: Long) = tagService.get(tagId)

    @PostMapping
    @ResponseStatus(value = HttpStatus.CREATED)
    fun create(@RequestBody createTagDTO: CreateTagDTO) = tagService.create(createTagDTO).toDTO()

    @GetMapping
    fun find(
            paginationQuery: PaginationQuery,
            sortQuery: SortQuery,
            @RequestParam("query") query: String?
    ) = tagService.find(paginationQuery, sortQuery, query)
}
