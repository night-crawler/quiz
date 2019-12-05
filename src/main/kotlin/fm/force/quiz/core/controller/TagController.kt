package fm.force.quiz.core.controller

import fm.force.quiz.core.dto.CreateTagDTO
import fm.force.quiz.core.dto.PageDTO
import fm.force.quiz.core.dto.toDTO
import fm.force.quiz.core.entity.Tag
import fm.force.quiz.core.exception.NotFoundException
import fm.force.quiz.core.repository.JpaTagRepository
import fm.force.quiz.core.service.TagService
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*


data class QuerySort(
        val sort: Collection<String>?
)


@RestController
@RequestMapping("tags")
class TagController(
        val tagService: TagService,
        val jpaTagRepository: JpaTagRepository
) {
    @GetMapping("{tagId}")
    fun getTag(@PathVariable tagId: Long) = jpaTagRepository
            .findById(tagId)
            .orElseThrow { NotFoundException(tagId, Tag::class) }
            .toDTO()

    @PostMapping
    @ResponseStatus(value = HttpStatus.CREATED)
    fun createTag(@RequestBody createTagDTO: CreateTagDTO) =
            tagService.create(createTagDTO).toDTO()

    @GetMapping
    fun findTags(
            @RequestParam("p") page: Int?,
            @RequestParam("size") size: Int?,
            querySort: QuerySort): PageDTO {

        println(querySort)
        val pageRequest = PageRequest.of(
                page ?: 1 - 1, size ?: 25,
                Sort.by(
                        Sort.Order(Sort.Direction.ASC, "name"),
                        Sort.Order(Sort.Direction.DESC, "owner.email")
                )
        )
        val q = jpaTagRepository.findAll(pageRequest)
        return q.toDTO<Tag> { it.toDTO() }

    }
}
