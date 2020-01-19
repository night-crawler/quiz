package fm.force.quiz.core.service

import am.ik.yavi.builder.ValidatorBuilder
import am.ik.yavi.builder.konstraintOnGroup
import fm.force.quiz.common.SpecificationBuilder
import fm.force.quiz.configuration.properties.DifficultyScaleValidationProperties
import fm.force.quiz.core.dto.DifficultyScaleFullDTO
import fm.force.quiz.core.dto.DifficultyScalePatchDTO
import fm.force.quiz.core.dto.PageDTO
import fm.force.quiz.core.dto.SearchQueryDTO
import fm.force.quiz.core.dto.toDTO
import fm.force.quiz.core.dto.toFullDTO
import fm.force.quiz.core.entity.DifficultyScale
import fm.force.quiz.core.entity.DifficultyScale_
import fm.force.quiz.core.repository.DifficultyScaleRepository
import fm.force.quiz.core.validator.intConstraint
import fm.force.quiz.core.validator.mandatory
import fm.force.quiz.core.validator.stringConstraint
import java.time.Instant
import org.springframework.data.domain.Page
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DifficultyScaleService(
    difficultyScaleRepository: DifficultyScaleRepository,
    validationProps: DifficultyScaleValidationProperties
) : DifficultyScaleServiceType(repository = difficultyScaleRepository) {
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

        return Specification
            .where(ownerEquals)
            .and(SpecificationBuilder.ciContains(needle, DifficultyScale_.name))
    }

    @Transactional(readOnly = true)
    override fun serializePage(page: Page<DifficultyScale>): PageDTO = page.toDTO { it.toFullDTO() }

    @Transactional(readOnly = true)
    override fun serializeEntity(entity: DifficultyScale): DifficultyScaleFullDTO =
        repository.refresh(entity).toFullDTO()

    override fun create(createDTO: DifficultyScalePatchDTO): DifficultyScale {
        validateCreate(createDTO)
        val entity = with(createDTO) {
            DifficultyScale(
                owner = authenticationFacade.user,
                name = name!!,
                max = max!!
            )
        }
        return repository.save(entity)
    }

    @Transactional
    override fun patch(id: Long, patchDTO: DifficultyScalePatchDTO): DifficultyScale {
        validatePatch(patchDTO)
        val modified = getOwnedEntity(id).apply {
            if (patchDTO.name != null) name = patchDTO.name
            if (patchDTO.max != null) max = patchDTO.max
            updatedAt = Instant.now()
        }
        return repository.save(modified)
    }
}
