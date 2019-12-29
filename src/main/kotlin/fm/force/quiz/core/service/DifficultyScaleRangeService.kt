package fm.force.quiz.core.service

import am.ik.yavi.builder.ValidatorBuilder
import am.ik.yavi.builder.konstraint
import am.ik.yavi.builder.konstraintOnGroup
import fm.force.quiz.common.SpecificationBuilder
import fm.force.quiz.configuration.properties.DifficultyScaleRangeValidationProperties
import fm.force.quiz.core.dto.DifficultyScaleRangeDTO
import fm.force.quiz.core.dto.PageDTO
import fm.force.quiz.core.dto.PatchDifficultyScaleRangeDTO
import fm.force.quiz.core.dto.toDTO
import fm.force.quiz.core.entity.DifficultyScale
import fm.force.quiz.core.entity.DifficultyScaleRange
import fm.force.quiz.core.entity.DifficultyScaleRange_
import fm.force.quiz.core.exception.NotFoundException
import fm.force.quiz.core.exception.ValidationError
import fm.force.quiz.core.repository.JpaDifficultyScaleRangeRepository
import fm.force.quiz.core.repository.JpaDifficultyScaleRepository
import fm.force.quiz.core.validator.fkConstraint
import fm.force.quiz.core.validator.intConstraint
import fm.force.quiz.core.validator.mandatory
import fm.force.quiz.core.validator.stringConstraint
import fm.force.quiz.security.service.AuthenticationFacade
import org.springframework.data.domain.Page
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Service
import java.time.Instant
import java.util.function.Predicate
import javax.transaction.Transactional


@Service
class DifficultyScaleRangeService(
        private val jpaDifficultyScaleRepository: JpaDifficultyScaleRepository,
        authenticationFacade: AuthenticationFacade,
        jpaDifficultyScaleRangeRepository: JpaDifficultyScaleRangeRepository,
        paginationService: PaginationService,
        sortingService: SortingService,
        validationProps: DifficultyScaleRangeValidationProperties
) : AbstractPaginatedCRUDService<DifficultyScaleRange, JpaDifficultyScaleRangeRepository, PatchDifficultyScaleRangeDTO, DifficultyScaleRangeDTO>(
        repository = jpaDifficultyScaleRangeRepository,
        authenticationFacade = authenticationFacade,
        paginationService = paginationService,
        sortingService = sortingService
) {
    private val msgCannotBeModified = "Cannot be modified"
    private val msgMaxMustBeLessThanMin = "Max must be less than min"

    private val whenMinMaxSwapped = Predicate<DifficultyScaleRange> { it.min <= it.max }
    private val whenIntersects = Predicate<DifficultyScaleRange> {
        repository.findIntersecting(it.difficultyScale.id, it.min, it.max).isEmpty()
    }

    val integrityValidator = ValidatorBuilder.of<DifficultyScaleRange>()
            .constraintOnTarget(whenMinMaxSwapped, "max", "", msgMaxMustBeLessThanMin)
            .constraintOnTarget(whenIntersects, "max", "", msgMaxMustBeLessThanMin)
            .build()

    val dtoValidator = ValidatorBuilder.of<PatchDifficultyScaleRangeDTO>()
            .konstraintOnGroup(CRUDConstraintGroup.CREATE) {
                mandatory(PatchDifficultyScaleRangeDTO::title)
                mandatory(PatchDifficultyScaleRangeDTO::min)
                mandatory(PatchDifficultyScaleRangeDTO::max)
                mandatory(PatchDifficultyScaleRangeDTO::difficultyScale)
            }
            .konstraintOnGroup(CRUDConstraintGroup.UPDATE) {
                konstraint(PatchDifficultyScaleRangeDTO::difficultyScale) {
                    isNull().message(msgCannotBeModified)
                }
            }

            .stringConstraint(PatchDifficultyScaleRangeDTO::title, validationProps.minTitleLength..validationProps.maxTitleLength)
            .intConstraint(PatchDifficultyScaleRangeDTO::min, 0..validationProps.minUpper)
            .intConstraint(PatchDifficultyScaleRangeDTO::max, 1..validationProps.maxUpper)

            .fkConstraint(
                    PatchDifficultyScaleRangeDTO::difficultyScale,
                    jpaDifficultyScaleRepository,
                    getOwnerId = { authenticationFacade.user.id }
            )
            .build()

    fun validateCreate(createDTO: PatchDifficultyScaleRangeDTO) = dtoValidator
            .validate(createDTO, CRUDConstraintGroup.CREATE)
            .throwIfInvalid { ValidationError(it) }

    fun validatePatch(patchDTO: PatchDifficultyScaleRangeDTO) = dtoValidator
            .validate(patchDTO, CRUDConstraintGroup.UPDATE)
            .throwIfInvalid { ValidationError(it) }

    fun validateEntity(entity: DifficultyScaleRange) = integrityValidator
            .validate(entity)
            .throwIfInvalid { ValidationError(it) }

    override fun buildSingleArgumentSearchSpec(needle: String?): Specification<DifficultyScaleRange> {
        if (needle.isNullOrEmpty())
            return emptySpecification

        return Specification
                .where(SpecificationBuilder.fk(authenticationFacade::user, DifficultyScaleRange_.owner))
                .and(SpecificationBuilder.ciContains(needle, DifficultyScaleRange_.title))
    }

    override fun serializePage(page: Page<DifficultyScaleRange>): PageDTO = page.toDTO { it.toDTO() }

    override fun serializeEntity(entity: DifficultyScaleRange): DifficultyScaleRangeDTO = entity.toDTO()

    fun retrieveDifficultyScale(id: Long): DifficultyScale = jpaDifficultyScaleRepository
            .findByIdAndOwner(id, authenticationFacade.user)
            .orElseThrow { NotFoundException(id, DifficultyScale::class) }

    @Transactional
    override fun create(createDTO: PatchDifficultyScaleRangeDTO): DifficultyScaleRange {
        validateCreate(createDTO)
        val entity = with(createDTO) {
            DifficultyScaleRange(
                    owner = authenticationFacade.user,
                    difficultyScale = retrieveDifficultyScale(difficultyScale!!),
                    title = title!!,
                    min = min!!,
                    max = max!!
            )
        }
        validateEntity(entity)
        return repository.save(entity)
    }

    @Transactional
    override fun patch(id: Long, patchDTO: PatchDifficultyScaleRangeDTO): DifficultyScaleRange {
        validatePatch(patchDTO)
        val modified = getInstance(id).apply {
            if (patchDTO.max != null) max = patchDTO.max
            if (patchDTO.min != null) min = patchDTO.min
            if (patchDTO.title != null) title = patchDTO.title
            updatedAt = Instant.now()
        }
        validateEntity(modified)
        return repository.save(modified)
    }
}