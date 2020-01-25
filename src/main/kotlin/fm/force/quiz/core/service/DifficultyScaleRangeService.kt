package fm.force.quiz.core.service

import am.ik.yavi.builder.ValidatorBuilder
import am.ik.yavi.builder.konstraint
import am.ik.yavi.builder.konstraintOnGroup
import fm.force.quiz.common.SpecificationBuilder
import fm.force.quiz.configuration.properties.DifficultyScaleRangeValidationProperties
import fm.force.quiz.core.dto.DifficultyScaleRangeFullDTO
import fm.force.quiz.core.dto.DifficultyScaleRangePatchDTO
import fm.force.quiz.core.dto.DifficultyScaleRangeSearchDTO
import fm.force.quiz.core.dto.PageDTO
import fm.force.quiz.core.dto.toDTO
import fm.force.quiz.core.dto.toFullDTO
import fm.force.quiz.core.entity.DifficultyScaleRange
import fm.force.quiz.core.entity.DifficultyScaleRange_
import fm.force.quiz.core.repository.DifficultyScaleRangeRepository
import fm.force.quiz.core.repository.DifficultyScaleRepository
import fm.force.quiz.core.validator.intConstraint
import fm.force.quiz.core.validator.mandatory
import fm.force.quiz.core.validator.ownedFkConstraint
import fm.force.quiz.core.validator.stringConstraint
import java.time.Instant
import java.util.function.Predicate
import org.springframework.data.domain.Page
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DifficultyScaleRangeService(
    private val difficultyScaleRepository: DifficultyScaleRepository,
    difficultyScaleRangeRepository: DifficultyScaleRangeRepository,
    validationProps: DifficultyScaleRangeValidationProperties
) : DifficultyScaleRangeServiceType(repository = difficultyScaleRangeRepository) {

    private val msgCannotBeModified = "Cannot be modified"
    private val msgMaxMustBeLessThanMin = "Max must be less than min"
    private val msgMustNotIntersect = "Ranges must not intersect"

    private val whenMinMaxSwapped = Predicate<DifficultyScaleRange> { it.min <= it.max }
    private val whenIntersects = Predicate<DifficultyScaleRange> {
        repository.findIntersecting(it.difficultyScale.id, it.min, it.max, it.id).isEmpty()
    }

    override var entityValidator = ValidatorBuilder.of<DifficultyScaleRange>()
        .constraintOnTarget(whenMinMaxSwapped, "max", "", msgMaxMustBeLessThanMin)
        .constraintOnTarget(whenIntersects, "max", "", msgMustNotIntersect)
        .build()

    override var dtoValidator = ValidatorBuilder.of<DifficultyScaleRangePatchDTO>()
        .konstraintOnGroup(CRUDConstraintGroup.CREATE) {
            mandatory(DifficultyScaleRangePatchDTO::title)
            mandatory(DifficultyScaleRangePatchDTO::min)
            mandatory(DifficultyScaleRangePatchDTO::max)
            mandatory(DifficultyScaleRangePatchDTO::difficultyScale)
        }
        .konstraintOnGroup(CRUDConstraintGroup.UPDATE) {
            konstraint(DifficultyScaleRangePatchDTO::difficultyScale) {
                isNull.message(msgCannotBeModified)
            }
        }

        .stringConstraint(DifficultyScaleRangePatchDTO::title, validationProps.minTitleLength..validationProps.maxTitleLength)
        .intConstraint(DifficultyScaleRangePatchDTO::min, 0..validationProps.minUpper)
        .intConstraint(DifficultyScaleRangePatchDTO::max, 1..validationProps.maxUpper)

        .ownedFkConstraint(DifficultyScaleRangePatchDTO::difficultyScale, difficultyScaleRepository, ::ownerId)
        .build()

    override fun buildSearchSpec(search: DifficultyScaleRangeSearchDTO?): Specification<DifficultyScaleRange> {
        val ownerEquals = SpecificationBuilder.fk(authenticationFacade::user, DifficultyScaleRange_.owner)
        if (search == null) return ownerEquals

        var spec = ownerEquals

        with(SpecificationBuilder) {
            if (search.difficultyScale != null)
                spec = spec.and(fk(difficultyScaleRepository.getEntity(search.difficultyScale), DifficultyScaleRange_.difficultyScale))!!
            if (search.title != null) {
                spec = spec.and(ciContains(search.title, DifficultyScaleRange_.title))!!
            }
        }

        return spec
    }

    @Transactional(readOnly = true)
    override fun serializePage(page: Page<DifficultyScaleRange>): PageDTO = page.toDTO { it.toFullDTO() }

    @Transactional(readOnly = true)
    override fun serializeEntity(entity: DifficultyScaleRange): DifficultyScaleRangeFullDTO =
        repository.refresh(entity).toFullDTO()

    @Transactional
    override fun create(createDTO: DifficultyScaleRangePatchDTO): DifficultyScaleRange {
        validateCreate(createDTO)
        val entity = with(createDTO) {
            DifficultyScaleRange(
                owner = authenticationFacade.user,
                difficultyScale = difficultyScaleRepository.getEntity(difficultyScale!!),
                title = title!!,
                min = min!!,
                max = max!!
            )
        }
        validateEntity(entity)
        return repository.save(entity)
    }

    @Transactional
    override fun patch(id: Long, patchDTO: DifficultyScaleRangePatchDTO): DifficultyScaleRange {
        validatePatch(patchDTO)
        val modified = getOwnedEntity(id).apply {
            if (patchDTO.max != null) max = patchDTO.max
            if (patchDTO.min != null) min = patchDTO.min
            if (patchDTO.title != null) title = patchDTO.title
            updatedAt = Instant.now()
        }
        validateEntity(modified)
        return repository.save(modified)
    }
}
