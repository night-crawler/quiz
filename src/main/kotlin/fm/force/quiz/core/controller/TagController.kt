package fm.force.quiz.core.controller

import fm.force.quiz.core.dto.*
import fm.force.quiz.core.entity.Tag
import fm.force.quiz.core.repository.JpaTagRepository
import fm.force.quiz.core.service.PaginationService
import fm.force.quiz.core.service.SortingService
import fm.force.quiz.core.service.TagService
import org.springframework.data.domain.PageRequest
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("tags")
class TagController(
        private val tagService: TagService,
        private val jpaTagRepository: JpaTagRepository,
        private val paginationService: PaginationService,
        private val sortingService: SortingService
) {

    @GetMapping("{tagId}")
    fun getTag(@PathVariable tagId: Long) = tagService.getTag(tagId)

    @PostMapping
    @ResponseStatus(value = HttpStatus.CREATED)
    fun createTag(@RequestBody createTagDTO: CreateTagDTO) =
            tagService.create(createTagDTO).toDTO()

    @GetMapping
    fun findTags(
            paginationQuery: PaginationQuery,
            sortQuery: SortQuery,
            @RequestParam("query") query: String?
    ): PageDTO {

        val pagination = paginationService.getPagination(paginationQuery)
        val sorting = sortingService.getSorting(sortQuery)
        val pageRequest = PageRequest.of(pagination.page, pagination.pageSize, sorting)
        val tagsPage = jpaTagRepository.findAll(tagService.buildSearchSpec(query), pageRequest)
        return tagsPage.toDTO<Tag> { it.toDTO() }
    }
}
