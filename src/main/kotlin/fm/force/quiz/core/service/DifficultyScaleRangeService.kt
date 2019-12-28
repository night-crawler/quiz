package fm.force.quiz.core.service

import am.ik.yavi.builder.ValidatorBuilder
import am.ik.yavi.builder.konstraint
import am.ik.yavi.builder.konstraintOnCondition
import am.ik.yavi.builder.konstraintOnGroup
import am.ik.yavi.core.ConstraintCondition
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
    private val msgWrongTitle = "Title length must be in range ${validationProps.minTitleLength} - ${validationProps.maxTitleLength}"
    private val msgWrongMin = "Min must be in range 0 - ${validationProps.minUpper}"
    private val msgWrongMax = "Max must be in range 1 - ${validationProps.maxUpper}"
    private val msgNotNul = "Must not be null"
    private val msgWrongDifficultyScale = "Unknown difficulty scale"
    private val msgDifficultyScaleNotFound = "Difficulty scale was not found"
    private val msgCannotBeModified = "Cannot be modified"
    private val msgMaxMustBeLessThanMin = "Max must be less than min"

    private val whenTitleIsPresent = ConstraintCondition<PatchDifficultyScaleRangeDTO> { dto, _ -> dto.title != null }
    private val whenMinIsPresent = ConstraintCondition<PatchDifficultyScaleRangeDTO> { dto, _ -> dto.min != null }
    private val whenMaxIsPresent = ConstraintCondition<PatchDifficultyScaleRangeDTO> { dto, _ -> dto.max != null }
    private val whenDifficultyScaleIsPresent = ConstraintCondition<PatchDifficultyScaleRangeDTO> { dto, _ -> dto.difficultyScale != null }

    private val whenDifficultyScaleDoesNotExist = Predicate<PatchDifficultyScaleRangeDTO> {
        if (it.difficultyScale == null) true
        else jpaDifficultyScaleRepository.findByIdAndOwner(it.difficultyScale, authenticationFacade.user).isPresent
    }

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
                konstraint(PatchDifficultyScaleRangeDTO::title) { notNull().message(msgNotNul) }
                konstraint(PatchDifficultyScaleRangeDTO::min) { notNull().message(msgNotNul) }
                konstraint(PatchDifficultyScaleRangeDTO::max) { notNull().message(msgNotNul) }
                konstraint(PatchDifficultyScaleRangeDTO::difficultyScale) { notNull().message(msgNotNul) }
            }
            .konstraintOnGroup(CRUDConstraintGroup.UPDATE) {
                konstraint(PatchDifficultyScaleRangeDTO::difficultyScale) {
                    isNull().message(msgCannotBeModified)
                }
            }

            .konstraintOnCondition(whenTitleIsPresent) {
                konstraint(PatchDifficultyScaleRangeDTO::title) {
                    greaterThanOrEqual(validationProps.minTitleLength).message(msgWrongTitle)
                    lessThanOrEqual(validationProps.maxTitleLength).message(msgWrongTitle)
                }
            }
            .konstraintOnCondition(whenMinIsPresent) {
                konstraint(PatchDifficultyScaleRangeDTO::min) {
                    greaterThanOrEqual(0).message(msgWrongMin)
                    lessThanOrEqual(validationProps.minUpper).message(msgWrongMin)
                }
            }
            .konstraintOnCondition(whenMaxIsPresent) {
                konstraint(PatchDifficultyScaleRangeDTO::max) {
                    greaterThanOrEqual(1).message(msgWrongMax)
                    lessThanOrEqual(validationProps.maxUpper).message(msgWrongMax)
                }
            }
            .konstraintOnCondition(whenDifficultyScaleIsPresent) {
                konstraint(PatchDifficultyScaleRangeDTO::difficultyScale) {
                    greaterThan(0).message(msgWrongDifficultyScale)
                }
                constraintOnTarget(whenDifficultyScaleDoesNotExist, "difficultyScale", "", msgDifficultyScaleNotFound)
            }
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

        val lowerCaseNeedle = needle.toLowerCase()

        val ownerEquals = Specification<DifficultyScaleRange> { root, _, builder ->
            builder.equal(root[DifficultyScaleRange_.owner], authenticationFacade.user)
        }

        val nameContains = Specification<DifficultyScaleRange> { root, _, builder ->
            builder.like(builder.lower(root[DifficultyScaleRange_.title]), "%$lowerCaseNeedle%")
        }

        return Specification.where(ownerEquals).and(nameContains)
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