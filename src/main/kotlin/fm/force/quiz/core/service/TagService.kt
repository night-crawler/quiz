package fm.force.quiz.core.service

import am.ik.yavi.builder.ValidatorBuilder
import am.ik.yavi.builder.konstraint
import fm.force.quiz.core.entity.Tag
import org.springframework.stereotype.Service
import com.github.slugify.Slugify
import fm.force.quiz.configuration.properties.TagValidationProperties
import fm.force.quiz.core.dto.CreateTagDTO
import fm.force.quiz.core.dto.toDTO
import fm.force.quiz.core.entity.Tag_
import fm.force.quiz.core.exception.NotFoundException
import fm.force.quiz.core.exception.ValidationError
import fm.force.quiz.core.repository.JpaTagRepository
import fm.force.quiz.security.service.AuthenticationFacade
import org.springframework.data.jpa.domain.Specification


@Service
class TagService(
        private val authenticationFacade: AuthenticationFacade,
        private val jpaTagRepository: JpaTagRepository,
        private val validationProps: TagValidationProperties
) {
    companion object {
        private val slugifier = Slugify()
        fun slugify(text: String): String = slugifier.slugify(text)
    }

    private val emptySpecification = Specification<Tag> { _, _, _ -> null}

    fun buildSearchSpec(needle: String?): Specification<Tag> {
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

    fun validate(tag: Tag) = validator.validate(tag).throwIfInvalid { ValidationError(it) }

    fun create(tagDTO: CreateTagDTO): Tag {
        val tag = Tag(
                owner = authenticationFacade.user,
                name = tagDTO.name,
                slug = slugify(tagDTO.name)
        )
        validate(tag)
        return jpaTagRepository.save(tag)
    }

    fun getTag(tagId: Long) = jpaTagRepository
            .findByIdAndOwner(tagId, authenticationFacade.user)
            .orElseThrow { NotFoundException(tagId, Tag::class) }
            .toDTO()
}
