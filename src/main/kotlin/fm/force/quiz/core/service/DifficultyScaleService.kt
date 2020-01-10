package fm.force.quiz.core.service

import am.ik.yavi.builder.ValidatorBuilder
import am.ik.yavi.builder.konstraintOnGroup
import fm.force.quiz.common.SpecificationBuilder
import fm.force.quiz.configuration.properties.DifficultyScaleValidationProperties
import fm.force.quiz.core.dto.*
import fm.force.quiz.core.entity.DifficultyScale
import fm.force.quiz.core.entity.DifficultyScale_
import fm.force.quiz.core.repository.JpaDifficultyScaleRepository
import fm.force.quiz.core.validator.intConstraint
import fm.force.quiz.core.validator.mandatory
import fm.force.quiz.core.validator.stringConstraint
import org.springframework.data.domain.Page
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Service
import java.time.Instant

@Service
class DifficultyScaleService(
        jpaDifficultyScaleRepository: JpaDifficultyScaleRepository,
        validationProps: DifficultyScaleValidationProperties
) : AbstractPaginatedCRUDService<DifficultyScale, JpaDifficultyScaleRepository, DifficultyScalePatchDTO, DifficultyScaleFullDTO>(
        repository = jpaDifficultyScaleRepository
) {
    override var dtoValidator = ValidatorBuilder.of<DifficultyScalePatchDTO>()
            .konstraintOnGroup(CRUDConstraintGroup.CREATE) {
                mandatory(DifficultyScalePatchDTO::name)
                mandatory(DifficultyScalePatchDTO::max)
            }
            .stringConstraint(DifficultyScalePatchDTO::name, validationProps.minNameLength..validationProps.maxNameLength)
            .intConstraint(DifficultyScalePatchDTO::max, 1..validationProps.allowedMax)
            .build()

    override fun buildSingleArgumentSearchSpec(needle: String?): Specification<DifficultyScale> {
        if (needle.isNullOrEmpty())
            return emptySpecification

        return Specification
                .where(SpecificationBuilder.fk(authenticationFacade::user, DifficultyScale_.owner))
                .and(SpecificationBuilder.ciContains(needle, DifficultyScale_.name))
    }

    override fun serializePage(page: Page<DifficultyScale>): PageDTO = page.toDTO { it.toFullDTO() }

    override fun serializeEntity(entity: DifficultyScale): DifficultyScaleFullDTO = entity.toFullDTO()

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

    override fun patch(id: Long, patchDTO: DifficultyScalePatchDTO): DifficultyScale {
        validatePatch(patchDTO)
        val modified = getOwnedInstance(id).apply {
            if (patchDTO.name != null) name = patchDTO.name
            if (patchDTO.max != null) max = patchDTO.max
            updatedAt = Instant.now()
        }
        return repository.save(modified)
    }
}
