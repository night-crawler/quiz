package fm.force.quiz.core.service

import am.ik.yavi.builder.ValidatorBuilder
import fm.force.quiz.common.SpecificationBuilder
import fm.force.quiz.common.dto.PageDTO
import fm.force.quiz.common.dto.TagFullDTO
import fm.force.quiz.common.dto.TagPatchDTO
import fm.force.quiz.common.dto.TagSearchQueryDTO
import fm.force.quiz.common.mapper.toDTO
import fm.force.quiz.common.mapper.toFullDTO
import fm.force.quiz.configuration.properties.TagValidationProperties
import fm.force.quiz.core.entity.Tag
import fm.force.quiz.core.entity.Tag_
import fm.force.quiz.core.exception.NotFoundException
import fm.force.quiz.core.exception.ValidationError
import fm.force.quiz.core.repository.TagRepository
import fm.force.quiz.core.validator.stringConstraint
import java.time.Instant
import org.springframework.data.domain.Page
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

val TagSearchQueryDTO.cleanedSlugs: List<String>
    get() = when {
        slugs == null -> listOf()
        slugs.size > 10 -> listOf()
        else -> slugs.map { it.trim() }.filterNot { it.isEmpty() }
    }

@Service
class TagService(
    validationProps: TagValidationProperties,
    private val tagRepository: TagRepository,
    private val slugifyService: SlugifyService
) : TagServiceType(repository = tagRepository) {

    override fun buildSearchSpec(search: TagSearchQueryDTO?): Specification<Tag> {
        var spec = SpecificationBuilder.fk(authenticationFacade::user, Tag_.owner)
        if (search == null) {
            return spec
        }
        if (!search.query.isNullOrEmpty()) {
            spec = spec.and(
                SpecificationBuilder
                    .ciEquals(search.query, Tag_.name)
                    .or(SpecificationBuilder.ciStartsWith(search.query, Tag_.name))
                    ?.or(SpecificationBuilder.ciEndsWith(search.query, Tag_.name))
            )!!
        }

        val cleanedSlugs = search.cleanedSlugs
        if (cleanedSlugs.isNotEmpty()) {
            val slugsSpec = Specification<Tag> { root, _, _ ->
                root[Tag_.slug].`in`(cleanedSlugs.map { it.toLowerCase() })
            }
            spec = spec.and(slugsSpec)!!
        }
        return spec
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

    fun getByName(patchDTO: TagPatchDTO): Tag =
        tagRepository
            .findByNameAndOwner(patchDTO.name, authenticationFacade.user)
            .orElseThrow { NotFoundException(patchDTO.name, this::class) }

    @Transactional
    fun getOrCreate(patchDTO: TagPatchDTO): Pair<Tag, Boolean> = try {
        getByName(patchDTO) to false
    } catch (ex: NotFoundException) {
        create(patchDTO) to true
    }

    override fun create(createDTO: TagPatchDTO): Tag {
        val tag = Tag(
            owner = authenticationFacade.user,
            name = createDTO.name,
            slug = slugifyService.slugify(createDTO.name)
        )
        validateEntity(tag)
        return repository.save(tag)
    }

    @Transactional
    override fun patch(id: Long, patchDTO: TagPatchDTO): Tag {
        val tag = getOwnedEntity(id)
        tag.name = patchDTO.name
        tag.slug = slugifyService.slugify(patchDTO.name)
        tag.updatedAt = Instant.now()
        validateEntity(tag)
        return repository.save(tag)
    }

    @Transactional(readOnly = true)
    override fun serializePage(page: Page<Tag>): PageDTO = page.toDTO { it.toFullDTO() }

    @Transactional(readOnly = true)
    override fun serializeEntity(entity: Tag): TagFullDTO =
        repository.refresh(entity).toFullDTO()
}
