package fm.force.quiz.core.controller

import fm.force.quiz.core.dto.*
import fm.force.quiz.core.entity.Tag
import fm.force.quiz.core.exception.NotFoundException
import fm.force.quiz.core.repository.JpaTagRepository
import fm.force.quiz.core.repository.SearchCriteria
import fm.force.quiz.core.repository.TagSpecification
import fm.force.quiz.core.service.PaginationService
import fm.force.quiz.core.service.SortingService
import fm.force.quiz.core.service.TagService
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
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
            paginationQuery: PaginationQuery,
            sortQuery: SortQuery
//            sc: SearchCriteria
    ): PageDTO {
        val ts = TagSpecification(SearchCriteria("name", "=", "trash"))


        val pagination = paginationService.getPagination(paginationQuery)
        val sorting = sortingService.getSorting(sortQuery)
        val pageRequest = PageRequest.of(
                pagination.page, pagination.pageSize,
                sorting
//                Sort.by(
//                        Sort.Order(Sort.Direction.ASC, "name"),
//                        Sort.Order(Sort.Direction.DESC, "owner.email")
//                )
        )
        val q = jpaTagRepository.findAll(ts, pageRequest)
        return q.toDTO<Tag> { it.toDTO() }

    }
}
