package fm.force.quiz.core.controller

import fm.force.quiz.core.dto.PageDTO
import fm.force.quiz.core.dto.PaginationQuery
import fm.force.quiz.core.dto.SortQuery
import fm.force.quiz.core.repository.CommonRepository
import fm.force.quiz.core.service.AbstractPaginatedCRUDService
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.web.bind.annotation.*

abstract class AbstractCRUDController<ENT, REPO, CRDTO, SERDTO>  (
        val service: AbstractPaginatedCRUDService<ENT, REPO, CRDTO, SERDTO>
)
        where REPO: JpaRepository<ENT, Long>,
              REPO: CommonRepository<ENT>,
              REPO: JpaSpecificationExecutor<ENT>
{
    @PostMapping
    open fun create(@RequestBody createDTO: CRDTO) : SERDTO = service.serializeEntity(service.create(createDTO))

    @GetMapping("{instanceId}")
    open fun get(@PathVariable instanceId: Long) : SERDTO = service.serializeEntity(service.getInstance(instanceId))

    @GetMapping
    open fun find(
            paginationQuery: PaginationQuery,
            sortQuery: SortQuery,
            @RequestParam("query") query: String?
    ) : PageDTO = service.find(paginationQuery, sortQuery, query)
}
