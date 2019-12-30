package fm.force.quiz.core.service

import am.ik.yavi.builder.ValidatorBuilder
import am.ik.yavi.builder.konstraintOnGroup
import fm.force.quiz.common.SpecificationBuilder
import fm.force.quiz.configuration.properties.DifficultyScaleValidationProperties
import fm.force.quiz.core.dto.DifficultyScaleDTO
import fm.force.quiz.core.dto.PageDTO
import fm.force.quiz.core.dto.PatchDifficultyScaleDTO
import fm.force.quiz.core.dto.toDTO
import fm.force.quiz.core.entity.DifficultyScale
import fm.force.quiz.core.entity.DifficultyScale_
import fm.force.quiz.core.repository.JpaDifficultyScaleRepository
import fm.force.quiz.core.validator.intConstraint
import fm.force.quiz.core.validator.mandatory
import fm.force.quiz.core.validator.stringConstraint
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
) : AbstractPaginatedCRUDService<DifficultyScale, JpaDifficultyScaleRepository, PatchDifficultyScaleDTO, DifficultyScaleDTO>(
        repository = jpaDifficultyScaleRepository,
        authenticationFacade = authenticationFacade,
        paginationService = paginationService,
        sortingService = sortingService
) {
    override var dtoValidator = ValidatorBuilder.of<PatchDifficultyScaleDTO>()
            .konstraintOnGroup(CRUDConstraintGroup.CREATE) {
                mandatory(PatchDifficultyScaleDTO::name)
                mandatory(PatchDifficultyScaleDTO::max)
            }
            .stringConstraint(PatchDifficultyScaleDTO::name, validationProps.minNameLength..validationProps.maxNameLength)
            .intConstraint(PatchDifficultyScaleDTO::max, 1..validationProps.allowedMax)
            .build()

    override fun buildSingleArgumentSearchSpec(needle: String?): Specification<DifficultyScale> {
        if (needle.isNullOrEmpty())
            return emptySpecification

        return Specification
                .where(SpecificationBuilder.fk(authenticationFacade::user, DifficultyScale_.owner))
                .and(SpecificationBuilder.ciContains(needle, DifficultyScale_.name))
    }

    override fun serializePage(page: Page<DifficultyScale>): PageDTO = page.toDTO { it.toDTO() }

    override fun serializeEntity(entity: DifficultyScale): DifficultyScaleDTO = entity.toDTO()

    override fun create(createDTO: PatchDifficultyScaleDTO): DifficultyScale {
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
