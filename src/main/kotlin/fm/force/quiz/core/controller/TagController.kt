package fm.force.quiz.core.controller

import fm.force.quiz.core.dto.*
import fm.force.quiz.core.entity.Tag
import fm.force.quiz.core.entity.Tag_
import fm.force.quiz.core.exception.NotFoundException
import fm.force.quiz.core.repository.JpaTagRepository
import fm.force.quiz.core.service.PaginationService
import fm.force.quiz.core.service.SortingService
import fm.force.quiz.core.service.TagService
import org.springframework.data.domain.PageRequest
import org.springframework.data.jpa.domain.Specification
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
    val emptySpecification = Specification<Tag> { _, _, _ -> null}

    @GetMapping("{tagId}")
    fun getTag(@PathVariable tagId: Long) = jpaTagRepository
            .findById(tagId)
            .orElseThrow { NotFoundException(tagId, Tag::class) }
            .toDTO()

    @PostMapping
    @ResponseStatus(value = HttpStatus.CREATED)
    fun createTag(@RequestBody createTagDTO: CreateTagDTO) =
            tagService.create(createTagDTO).toDTO()


    fun getSearchSpec(needle: String?): Specification<Tag> {
        if (needle.isNullOrEmpty())
            return emptySpecification

        val lowerCaseNeedle = needle.toLowerCase()

        val nameStartsWith = Specification<Tag> { root, _, builder ->
            builder.like(builder.lower(root[Tag_.name]), "$lowerCaseNeedle%") }

        val nameEndsWith = Specification<Tag> { root, _, builder ->
            builder.like(builder.lower(root[Tag_.name]), "%$lowerCaseNeedle") }

        val nameEquals = Specification<Tag> { root, _, builder ->
            builder.equal(builder.lower(root[Tag_.name]), lowerCaseNeedle) }

        return Specification.where(nameEquals).or(nameStartsWith).or(nameEndsWith)
    }

    @GetMapping
    fun findTags(
            paginationQuery: PaginationQuery,
            sortQuery: SortQuery,
            @RequestParam("query") query: String?
    ): PageDTO {

        val pagination = paginationService.getPagination(paginationQuery)
        val sorting = sortingService.getSorting(sortQuery)
        val pageRequest = PageRequest.of(pagination.page, pagination.pageSize, sorting)
        val tagsPage = jpaTagRepository.findAll(getSearchSpec(query), pageRequest)
        return tagsPage.toDTO<Tag> { it.toDTO() }
    }
}
