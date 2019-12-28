package fm.force.quiz.core.service

import fm.force.quiz.core.dto.PageDTO
import fm.force.quiz.core.dto.PaginationQuery
import fm.force.quiz.core.dto.SortQuery
import fm.force.quiz.core.exception.NotFoundException
import fm.force.quiz.core.repository.CommonRepository
import fm.force.quiz.security.service.AuthenticationFacade
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.jpa.domain.Specification
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import javax.transaction.Transactional

abstract class AbstractPaginatedCRUDService<ENT, REPO, CRDTO, SERDTO>(
        val repository: REPO,
        val authenticationFacade: AuthenticationFacade,
        private val paginationService: PaginationService,
        private val sortingService: SortingService
)
    where REPO: JpaRepository<ENT, Long>,
          REPO: CommonRepository<ENT>,
          REPO: JpaSpecificationExecutor<ENT> {

    val emptySpecification = Specification<ENT> { _, _, _ -> null }

    abstract fun buildSingleArgumentSearchSpec(needle: String?): Specification<ENT>
    abstract fun serializePage(page: Page<ENT>): PageDTO
    abstract fun serializeEntity(entity: ENT) : SERDTO
    abstract fun create(createDTO: CRDTO) : ENT

    open fun getInstance(id: Long): ENT = repository
            .findByIdAndOwner(id, authenticationFacade.user)
            .orElseThrow { NotFoundException(id, this::class) }

    @Transactional
    open fun find(
            paginationQuery: PaginationQuery,
            sortQuery: SortQuery,
            query: String?
    ): PageDTO {
        val pagination = paginationService.getPagination(paginationQuery)
        val sorting = sortingService.getSorting(sortQuery)
        val pageRequest = PageRequest.of(pagination.page, pagination.pageSize, sorting)
        val page = repository.findAll(buildSingleArgumentSearchSpec(query), pageRequest)
        return serializePage(page)
    }

    open fun delete(id: Long) = getInstance(id).let { repository.delete(it) }
    open fun patch(id: Long, patchDTO: CRDTO): ENT = throw NotImplementedError("Patch method is not implemented")
}
