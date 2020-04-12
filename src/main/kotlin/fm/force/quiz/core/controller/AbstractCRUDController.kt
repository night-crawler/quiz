package fm.force.quiz.core.controller

import fm.force.quiz.common.dto.DTOSearchMarker
import fm.force.quiz.common.dto.DTOSerializationMarker
import fm.force.quiz.common.dto.PageDTO
import fm.force.quiz.common.dto.PaginationQuery
import fm.force.quiz.common.dto.SortQuery
import fm.force.quiz.core.repository.CommonRepository
import fm.force.quiz.core.repository.CustomJpaRepository
import fm.force.quiz.core.service.AbstractPaginatedCRUDService
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.ResponseStatus

abstract class AbstractCRUDController<EntType, RepoType, PatchType, DTOType, SearchType>(
    open val service: AbstractPaginatedCRUDService<EntType, RepoType, PatchType, DTOType, SearchType>
)
    where RepoType : CustomJpaRepository<EntType, Long>,
          RepoType : CommonRepository<EntType>,
          RepoType : JpaSpecificationExecutor<EntType>,
          DTOType : DTOSerializationMarker,
          SearchType : DTOSearchMarker {
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    open fun create(@RequestBody createDTO: PatchType): DTOType = service.serializeEntity(service.create(createDTO))

    @GetMapping("{instanceId}")
    open fun get(@PathVariable instanceId: Long): DTOType = service.serializeEntity(service.getOwnedEntity(instanceId))

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
        search: SearchType
    ): PageDTO = service.find(paginationQuery, sortQuery, search)
}
