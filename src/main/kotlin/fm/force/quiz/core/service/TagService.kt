package fm.force.quiz.core.service

import am.ik.yavi.builder.ValidatorBuilder
import am.ik.yavi.builder.konstraint
import fm.force.quiz.core.entity.Tag
import org.springframework.stereotype.Service
import com.github.slugify.Slugify
import fm.force.quiz.configuration.properties.TagValidationProperties
import fm.force.quiz.core.dto.*
import fm.force.quiz.core.entity.Tag_
import fm.force.quiz.core.exception.ValidationError
import fm.force.quiz.core.repository.JpaTagRepository
import fm.force.quiz.security.service.AuthenticationFacade
import org.springframework.data.domain.Page
import org.springframework.data.jpa.domain.Specification
import java.time.Instant


@Service
class TagService(
        private val validationProps: TagValidationProperties,
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

        val lowerCaseNeedle = needle.toLowerCase()

        val nameStartsWith = Specification<Tag> { root, _, builder ->
            builder.like(builder.lower(root[Tag_.name]), "$lowerCaseNeedle%") }

        val nameEndsWith = Specification<Tag> { root, _, builder ->
            builder.like(builder.lower(root[Tag_.name]), "%$lowerCaseNeedle") }

        val nameEquals = Specification<Tag> { root, _, builder ->
            builder.equal(builder.lower(root[Tag_.name]), lowerCaseNeedle) }

        val ownerEquals = Specification<Tag> { root, _, builder ->
            builder.equal(root[Tag_.owner], authenticationFacade.user) }

        return Specification.where(ownerEquals).and(
                Specification.where(nameEquals).or(nameStartsWith).or(nameEndsWith)
        )
    }

    private val validator = ValidatorBuilder.of<Tag>()
            .konstraint(Tag::name) {
                greaterThanOrEqual(validationProps.minTagLength).message("Tag must be at least ${validationProps.minTagLength} characters long")
            }
            .konstraint(Tag::name) {
                lessThanOrEqual(validationProps.maxTagLength).message("Tag must be maximum ${validationProps.maxTagLength} characters long")
            }
            .konstraint(Tag::slug) {
                notBlank().message("There was a problem creating a slug")
            }
            .build()

    fun validate(instance: Tag) = validator.validate(instance).throwIfInvalid { ValidationError(it) }

    override fun create(createDTO: PatchTagDTO): Tag {
        val tag = Tag(
                owner = authenticationFacade.user,
                name = createDTO.name,
                slug = slugify(createDTO.name)
        )
        validate(tag)
        return repository.save(tag)
    }

    override fun patch(id: Long, patchDTO: PatchTagDTO): Tag {
        val tag = getInstance(id)
        tag.name = patchDTO.name
        tag.slug = slugify(patchDTO.name)
        tag.updatedAt = Instant.now()
        validate(tag)
        return repository.save(tag)
    }

    override fun serializePage(page: Page<Tag>): PageDTO = page.toDTO { it.toDTO() }
    override fun serializeEntity(entity: Tag): TagDTO = entity.toDTO()
}
