package fm.force.quiz.core.service

import am.ik.yavi.core.Validator
import fm.force.quiz.core.dto.*
import fm.force.quiz.core.exception.NotFoundException
import fm.force.quiz.core.exception.ValidationError
import fm.force.quiz.core.repository.CommonRepository
import fm.force.quiz.core.repository.CustomJpaRepository
import fm.force.quiz.security.service.AuthenticationFacade
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.jpa.domain.Specification
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import javax.transaction.Transactional

abstract class AbstractPaginatedCRUDService<EntType, RepoType, PatchType, DTOType : DTOSerializationMarker>(
        val repository: RepoType,
        val authenticationFacade: AuthenticationFacade,
        private val paginationService: PaginationService,
        private val sortingService: SortingService
)
        where RepoType : CustomJpaRepository<EntType, Long>,
              RepoType : CommonRepository<EntType>,
              RepoType : JpaSpecificationExecutor<EntType> {

    val emptySpecification = Specification<EntType> { _, _, _ -> null }

    open lateinit var entityValidator: Validator<EntType>
    open lateinit var dtoValidator: Validator<PatchType>

    open fun validateEntity(entity: EntType) = entityValidator
            .validate(entity)
            .throwIfInvalid { ValidationError(it) }

    open fun validatePatch(patchDTO: PatchType) = dtoValidator
            .validate(patchDTO, CRUDConstraintGroup.UPDATE)
            .throwIfInvalid { ValidationError(it) }

    open fun validateCreate(createDTO: PatchType) = dtoValidator
            .validate(createDTO, CRUDConstraintGroup.CREATE)
            .throwIfInvalid { ValidationError(it) }

    abstract fun buildSingleArgumentSearchSpec(needle: String?): Specification<EntType>
    abstract fun serializePage(page: Page<EntType>): PageDTO
    abstract fun serializeEntity(entity: EntType): DTOType
    abstract fun create(createDTO: PatchType): EntType

    open fun getInstance(id: Long): EntType = repository
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
    open fun patch(id: Long, patchDTO: PatchType): EntType = throw NotImplementedError("Patch method is not implemented")
}
