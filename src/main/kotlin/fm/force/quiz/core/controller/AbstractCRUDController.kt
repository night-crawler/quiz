package fm.force.quiz.core.controller

import fm.force.quiz.core.dto.DTOSerializationMarker
import fm.force.quiz.core.dto.PageDTO
import fm.force.quiz.core.dto.PaginationQuery
import fm.force.quiz.core.dto.SortQuery
import fm.force.quiz.core.repository.CommonRepository
import fm.force.quiz.core.repository.CustomJpaRepository
import fm.force.quiz.core.service.AbstractPaginatedCRUDService
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

abstract class AbstractCRUDController<EntType, RepoType, PatchType, DTOType : DTOSerializationMarker>(
        val service: AbstractPaginatedCRUDService<EntType, RepoType, PatchType, DTOType>
)
        where RepoType : CustomJpaRepository<EntType, Long>,
              RepoType : CommonRepository<EntType>,
              RepoType : JpaSpecificationExecutor<EntType> {
    @PostMapping
    open fun create(@RequestBody createDTO: PatchType): DTOType = service.serializeEntity(service.create(createDTO))

    @GetMapping("{instanceId}")
    open fun get(@PathVariable instanceId: Long): DTOType = service.serializeEntity(service.getOwnedInstance(instanceId))

    @DeleteMapping("{instanceId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    open fun delete(@PathVariable instanceId: Long) = service.delete(instanceId)

    @PatchMapping("{instanceId}")
    open fun patch(@PathVariable instanceId: Long, @RequestBody patchDTO: PatchType) =
            service.serializeEntity(service.patch(instanceId, patchDTO))

    @GetMapping
    open fun find(
            paginationQuery: PaginationQuery,
            sortQuery: SortQuery,
            @RequestParam("query") query: String?
    ): PageDTO = service.find(paginationQuery, sortQuery, query)
}
