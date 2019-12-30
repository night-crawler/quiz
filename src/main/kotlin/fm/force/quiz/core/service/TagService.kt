package fm.force.quiz.core.service

import am.ik.yavi.builder.ValidatorBuilder
import com.github.slugify.Slugify
import fm.force.quiz.common.SpecificationBuilder
import fm.force.quiz.configuration.properties.TagValidationProperties
import fm.force.quiz.core.dto.PageDTO
import fm.force.quiz.core.dto.PatchTagDTO
import fm.force.quiz.core.dto.TagDTO
import fm.force.quiz.core.dto.toDTO
import fm.force.quiz.core.entity.Tag
import fm.force.quiz.core.entity.Tag_
import fm.force.quiz.core.exception.ValidationError
import fm.force.quiz.core.repository.JpaTagRepository
import fm.force.quiz.core.validator.stringConstraint
import fm.force.quiz.security.service.AuthenticationFacade
import org.springframework.data.domain.Page
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Service
import java.time.Instant


@Service
class TagService(
        validationProps: TagValidationProperties,
        jpaTagRepository: JpaTagRepository,
        paginationService: PaginationService,
        sortingService: SortingService,
        authenticationFacade: AuthenticationFacade
) : AbstractPaginatedCRUDService<Tag, JpaTagRepository, PatchTagDTO, TagDTO>(
        repository = jpaTagRepository,
        authenticationFacade = authenticationFacade,
        paginationService = paginationService,
        sortingService = sortingService
) {
    companion object {
        private val slugifier = Slugify()
        fun slugify(text: String): String = slugifier.slugify(text)
    }

    override fun buildSingleArgumentSearchSpec(needle: String?): Specification<Tag> {
        if (needle.isNullOrEmpty())
            return emptySpecification

        return Specification
                .where(SpecificationBuilder.fk(authenticationFacade::user, Tag_.owner))
                .and(Specification
                        .where(SpecificationBuilder.ciEquals(needle, Tag_.name))
                        .or(SpecificationBuilder.ciStartsWith(needle, Tag_.name))
                        .or(SpecificationBuilder.ciEndsWith(needle, Tag_.name))
                )
    }

    override var entityValidator = ValidatorBuilder.of<Tag>()
            .stringConstraint(Tag::name, validationProps.minTagLength..validationProps.maxTagLength)
            .build()

    private val slugValidator = ValidatorBuilder.of<Tag>()
            .stringConstraint(Tag::slug, validationProps.minTagLength..validationProps.maxSlugLength)
            .build()

    override fun validateEntity(entity: Tag) {
        entityValidator.validate(entity).throwIfInvalid { ValidationError(it) }
        slugValidator.validate(entity).throwIfInvalid { ValidationError(it) }
    }

    override fun create(createDTO: PatchTagDTO): Tag {
        val tag = Tag(
                owner = authenticationFacade.user,
                name = createDTO.name,
                slug = slugify(createDTO.name)
        )
        validateEntity(tag)
        return repository.save(tag)
    }

    override fun patch(id: Long, patchDTO: PatchTagDTO): Tag {
        val tag = getInstance(id)
        tag.name = patchDTO.name
        tag.slug = slugify(patchDTO.name)
        tag.updatedAt = Instant.now()
        validateEntity(tag)
        return repository.save(tag)
    }

    override fun serializePage(page: Page<Tag>): PageDTO = page.toDTO { it.toDTO() }
    override fun serializeEntity(entity: Tag): TagDTO = entity.toDTO()
}
