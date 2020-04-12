package fm.force.quiz.core.service

import am.ik.yavi.core.Validator
import fm.force.quiz.common.dto.DTOSearchMarker
import fm.force.quiz.common.dto.DTOSerializationMarker
import fm.force.quiz.common.dto.PageDTO
import fm.force.quiz.common.dto.PaginationQuery
import fm.force.quiz.common.dto.SortQuery
import fm.force.quiz.core.exception.NotFoundException
import fm.force.quiz.core.exception.ValidationError
import fm.force.quiz.core.repository.CommonRepository
import fm.force.quiz.core.repository.CustomJpaRepository
import fm.force.quiz.security.service.AuthenticationFacade
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.jpa.domain.Specification
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.transaction.annotation.Transactional

abstract class AbstractPaginatedCRUDService<EntType, RepoType, PatchType, DTOType, SearchType>(
    val repository: RepoType
)
    where RepoType : CustomJpaRepository<EntType, Long>,
          RepoType : CommonRepository<EntType>,
          RepoType : JpaSpecificationExecutor<EntType>,
          DTOType : DTOSerializationMarker,
          SearchType : DTOSearchMarker {
    val logger: Logger = LoggerFactory.getLogger(this::class.java)

    @Autowired
    lateinit var authenticationFacade: AuthenticationFacade

    @Autowired
    lateinit var sortingService: SortingService

    @Autowired
    lateinit var paginationService: PaginationService

    val ownerId: Long get() = authenticationFacade.user.id

    open lateinit var entityValidator: Validator<EntType>
    open lateinit var dtoValidator: Validator<PatchType>

    open fun validateEntity(entity: EntType) = entityValidator
        .validate(entity)
        .throwIfInvalid {
            logger.debug("Entity validation failed: {}", entity)
            ValidationError(it)
        }

    open fun validatePatch(patchDTO: PatchType) = dtoValidator
        .validate(patchDTO, CRUDConstraintGroup.UPDATE)
        .throwIfInvalid {
            logger.debug("DTO validation failed: {}", patchDTO)
            ValidationError(it)
        }

    open fun validateCreate(createDTO: PatchType) = dtoValidator
        .validate(createDTO, CRUDConstraintGroup.CREATE)
        .throwIfInvalid {
            logger.debug("DTO validation failed: {}", createDTO)
            ValidationError(it)
        }

    open fun buildSearchSpec(search: SearchType?): Specification<EntType> = throw NotImplementedError()
    abstract fun serializePage(page: Page<EntType>): PageDTO
    abstract fun serializeEntity(entity: EntType): DTOType
    abstract fun create(createDTO: PatchType): EntType

    open fun getOwnedEntity(id: Long): EntType = repository
        .findByIdAndOwner(id, authenticationFacade.user)
        .orElseThrow { NotFoundException(id, this::class) }

    open fun getEntity(id: Long): EntType = repository
        .findById(id)
        .orElseThrow { NotFoundException(id, this::class) }

    @Transactional(readOnly = true)
    open fun find(
        paginationQuery: PaginationQuery,
        sortQuery: SortQuery,
        search: SearchType?
    ): PageDTO {
        val pagination = paginationService.getPagination(paginationQuery)
        val sorting = sortingService.getSorting(sortQuery)
        val pageRequest = PageRequest.of(pagination.page, pagination.pageSize, sorting)
        val page = repository.findAll(buildSearchSpec(search), pageRequest)
        return serializePage(page)
    }

    open fun delete(id: Long) =
        getOwnedEntity(id).let { repository.delete(it) }

    open fun patch(id: Long, patchDTO: PatchType): EntType =
        throw NotImplementedError("Patch method is not implemented")
}
