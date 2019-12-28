package fm.force.quiz.core.service

import am.ik.yavi.builder.ValidatorBuilder
import am.ik.yavi.builder.konstraint
import am.ik.yavi.builder.konstraintOnCondition
import am.ik.yavi.builder.konstraintOnGroup
import am.ik.yavi.core.ConstraintCondition
import fm.force.quiz.configuration.properties.DifficultyScaleValidationProperties
import fm.force.quiz.core.dto.PatchDifficultyScaleDTO
import fm.force.quiz.core.dto.DifficultyScaleDTO
import fm.force.quiz.core.dto.PageDTO
import fm.force.quiz.core.dto.toDTO
import fm.force.quiz.core.entity.DifficultyScale
import fm.force.quiz.core.entity.DifficultyScale_
import fm.force.quiz.core.exception.ValidationError
import fm.force.quiz.core.repository.JpaDifficultyScaleRepository
import fm.force.quiz.security.service.AuthenticationFacade
import org.springframework.data.domain.Page
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Service
import java.time.Instant

@Service
class DifficultyScaleService(
        authenticationFacade: AuthenticationFacade,
        jpaDifficultyScaleRepository: JpaDifficultyScaleRepository,
        paginationService: PaginationService,
        sortingService: SortingService,
        validationProps: DifficultyScaleValidationProperties
): AbstractPaginatedCRUDService<DifficultyScale, JpaDifficultyScaleRepository, PatchDifficultyScaleDTO, DifficultyScaleDTO>(
        repository = jpaDifficultyScaleRepository,
        authenticationFacade = authenticationFacade,
        paginationService = paginationService,
        sortingService = sortingService
) {
    private val msgWrongName = "Name length must be in range ${validationProps.minNameLength} - ${validationProps.maxNameLength}"
    private val msgWrongMax = "Scale max must be in range 1 - ${validationProps.allowedMax}"
    private val msgNotNul = "Must not be null"

    private val whenNameIsPresent = ConstraintCondition<PatchDifficultyScaleDTO> { dto, _ -> dto.name != null }
    private val whenMaxIsPresent = ConstraintCondition<PatchDifficultyScaleDTO> { dto, _ -> dto.max != null }

    val validator = ValidatorBuilder.of<PatchDifficultyScaleDTO>()
            .konstraintOnGroup(CRUDConstraintGroup.CREATE) {
                konstraint(PatchDifficultyScaleDTO::name) { notNull().message(msgNotNul) }
                konstraint(PatchDifficultyScaleDTO::max) { notNull().message(msgNotNul) }
            }

            .konstraintOnCondition(whenNameIsPresent) {
                konstraint(PatchDifficultyScaleDTO::name) {
                    greaterThanOrEqual(validationProps.minNameLength).message(msgWrongName)
                    lessThanOrEqual(validationProps.maxNameLength).message(msgWrongName)
                }
            }
            .konstraintOnCondition(whenMaxIsPresent) {
                konstraint(PatchDifficultyScaleDTO::max) {
                    greaterThanOrEqual(1).message(msgWrongMax)
                    lessThanOrEqual(validationProps.allowedMax).message(msgWrongMax)
                }
            }
            .build()

    fun validateCreate(createDTO: PatchDifficultyScaleDTO) = validator
            .validate(createDTO, CRUDConstraintGroup.CREATE)
            .throwIfInvalid { ValidationError(it) }

    fun validatePatch(patchDTO: PatchDifficultyScaleDTO) = validator
            .validate(patchDTO)
            .throwIfInvalid { ValidationError(it) }

    override fun buildSingleArgumentSearchSpec(needle: String?): Specification<DifficultyScale> {
        if (needle.isNullOrEmpty())
            return emptySpecification

        val lowerCaseNeedle = needle.toLowerCase()

        val ownerEquals = Specification<DifficultyScale> { root, _, builder ->
            builder.equal(root[DifficultyScale_.owner], authenticationFacade.user)
        }

        val nameContains = Specification<DifficultyScale> { root, _, builder ->
            builder.like(builder.lower(root[DifficultyScale_.name]), "%$lowerCaseNeedle%")
        }

        return Specification.where(ownerEquals).and(nameContains)
    }

    override fun serializePage(page: Page<DifficultyScale>): PageDTO = page.toDTO { it.toDTO() }

    override fun serializeEntity(entity: DifficultyScale): DifficultyScaleDTO = entity.toDTO()

    override fun create(createDTO: PatchDifficultyScaleDTO): DifficultyScale {
        validateCreate(createDTO)
        val entity = with(createDTO) { DifficultyScale(
            owner = authenticationFacade.user,
            name = name!!,
            max = max!!
        ) }
        return repository.save(entity)
    }

    override fun patch(id: Long, patchDTO: PatchDifficultyScaleDTO): DifficultyScale {
        validatePatch(patchDTO)
        val modified = getInstance(id).apply {
            if (patchDTO.name != null) name = patchDTO.name
            if (patchDTO.max != null) max = patchDTO.max
            updatedAt = Instant.now()
        }
        return repository.save(modified)
    }
}
