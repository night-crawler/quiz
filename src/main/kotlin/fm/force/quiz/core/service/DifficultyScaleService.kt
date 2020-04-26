package fm.force.quiz.core.service

import am.ik.yavi.builder.ValidatorBuilder
import am.ik.yavi.builder.konstraintOnGroup
import fm.force.quiz.common.SpecificationBuilder
import fm.force.quiz.common.dto.DifficultyScaleFullDTO
import fm.force.quiz.common.dto.DifficultyScalePatchDTO
import fm.force.quiz.common.dto.DifficultyScaleRangePatchDTO
import fm.force.quiz.common.dto.PageDTO
import fm.force.quiz.common.dto.SearchQueryDTO
import fm.force.quiz.common.mapper.toDTO
import fm.force.quiz.common.mapper.toFullDTO
import fm.force.quiz.configuration.properties.DifficultyScaleRangeValidationProperties
import fm.force.quiz.configuration.properties.DifficultyScaleValidationProperties
import fm.force.quiz.core.entity.DifficultyScale
import fm.force.quiz.core.entity.DifficultyScaleRange
import fm.force.quiz.core.entity.DifficultyScale_
import fm.force.quiz.core.exception.ArbitraryValidationError
import fm.force.quiz.core.exception.NestedValidationError
import fm.force.quiz.core.repository.DifficultyScaleRangeRepository
import fm.force.quiz.core.repository.DifficultyScaleRepository
import fm.force.quiz.core.validator.intConstraint
import fm.force.quiz.core.validator.mandatory
import fm.force.quiz.core.validator.stringConstraint
import org.springframework.data.domain.Page
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant


@Service
class DifficultyScaleService(
    difficultyScaleRepository: DifficultyScaleRepository,
    private val validationProps: DifficultyScaleValidationProperties,
    rangeValidationProps: DifficultyScaleRangeValidationProperties,
    private val rangeRepository: DifficultyScaleRangeRepository
) : DifficultyScaleServiceType(repository = difficultyScaleRepository) {
    private val rangeDtoValidator = ValidatorBuilder.of<DifficultyScaleRangePatchDTO>()
        .mandatory(DifficultyScaleRangePatchDTO::title)
        .mandatory(DifficultyScaleRangePatchDTO::min)
        .mandatory(DifficultyScaleRangePatchDTO::max)
        .stringConstraint(DifficultyScaleRangePatchDTO::title, rangeValidationProps.titleRange)
        .intConstraint(DifficultyScaleRangePatchDTO::min, rangeValidationProps.minRange)
        .intConstraint(DifficultyScaleRangePatchDTO::max, rangeValidationProps.maxRange)
        .build()
    override var dtoValidator = ValidatorBuilder.of<DifficultyScalePatchDTO>()
        .konstraintOnGroup(CRUDConstraintGroup.CREATE) {
            mandatory(DifficultyScalePatchDTO::name)
            mandatory(DifficultyScalePatchDTO::max)
        }
        .stringConstraint(DifficultyScalePatchDTO::name, validationProps.minNameLength..validationProps.maxNameLength)
        .intConstraint(DifficultyScalePatchDTO::max, 1..validationProps.allowedMax)
        .build()

    override fun buildSearchSpec(search: SearchQueryDTO?): Specification<DifficultyScale> {
        val ownerEquals = SpecificationBuilder.fk(authenticationFacade::user, DifficultyScale_.owner)
        val needle = search?.query
        if (needle.isNullOrEmpty()) return ownerEquals

        return ownerEquals.and(SpecificationBuilder.ciContains(needle, DifficultyScale_.name))!!
    }

    @Transactional(readOnly = true)
    override fun serializePage(page: Page<DifficultyScale>): PageDTO = page.toDTO { it.toFullDTO() }

    @Transactional(readOnly = true)
    override fun serializeEntity(entity: DifficultyScale): DifficultyScaleFullDTO =
        repository.refresh(entity).toFullDTO()

    override fun validateCreate(createDTO: DifficultyScalePatchDTO) {
        super.validateCreate(createDTO)
        validateRanges(createDTO)
    }

    override fun validatePatch(patchDTO: DifficultyScalePatchDTO) {
        super.validatePatch(patchDTO)
        patchDTO.ranges?.let {
            validateRanges(patchDTO)
        }
    }

    private fun validateRanges(patchDTO: DifficultyScalePatchDTO) {
        if (patchDTO.ranges.isNullOrEmpty() || patchDTO.ranges.size !in 1..validationProps.maxRanges) {
            throw ArbitraryValidationError(
                fieldName = DifficultyScalePatchDTO::ranges.name,
                violatedValue = null,
                message = "Provide 1..${validationProps.maxRanges} ranges"
            )
        }
        patchDTO.ranges.forEachIndexed { index, range ->
            val violations = rangeDtoValidator.validate(range)
            if (violations.isNotEmpty()) {
                throw NestedValidationError(
                    prefix = "${DifficultyScalePatchDTO::ranges.name}[$index]",
                    violations = violations
                )
            }
        }
    }

    @Transactional
    override fun delete(id: Long) {
        getOwnedEntity(id).let {
            rangeRepository.deleteAll(it.difficultyScaleRanges)
            repository.flush()
            repository.delete(it)
        }
    }

    @Transactional
    override fun create(createDTO: DifficultyScalePatchDTO): DifficultyScale {
        validateCreate(createDTO)
        var entity = with(createDTO) {
            DifficultyScale(
                owner = authenticationFacade.user,
                name = name!!,
                max = max!!
            )
        }
        entity = repository.save(entity)
        createDTO.ranges?.let {
            entity.difficultyScaleRanges = createRanges(entity, it)
        }
        return entity
    }

    @Transactional
    fun createRanges(
        entity: DifficultyScale,
        ranges: Collection<DifficultyScaleRangePatchDTO>
    ): MutableSet<DifficultyScaleRange> {
        val difficultyScaleRanges = ranges.map {
            DifficultyScaleRange(
                owner = authenticationFacade.user,
                difficultyScale = entity,
                title = it.title!!,
                min = it.min!!,
                max = it.max!!
            )
        }.toMutableSet()
        rangeRepository.saveAll(difficultyScaleRanges)
        return difficultyScaleRanges
    }

    @Transactional
    override fun patch(id: Long, patchDTO: DifficultyScalePatchDTO): DifficultyScale {
        validatePatch(patchDTO)
        val modified = getOwnedEntity(id).apply {
            if (patchDTO.name != null) name = patchDTO.name
            if (patchDTO.max != null) max = patchDTO.max
            if (patchDTO.ranges != null) {
                rangeRepository.deleteAll(difficultyScaleRanges)
                rangeRepository.flush()
                difficultyScaleRanges = createRanges(this, patchDTO.ranges)
            }
            updatedAt = Instant.now()
        }
        return repository.save(modified)
    }
}
