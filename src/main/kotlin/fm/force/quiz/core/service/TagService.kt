package fm.force.quiz.core.service

import am.ik.yavi.builder.ValidatorBuilder
import am.ik.yavi.builder.konstraint
import fm.force.quiz.core.entity.Tag
import org.springframework.stereotype.Service
import com.github.slugify.Slugify
import fm.force.quiz.configuration.properties.TagValidationProperties
import fm.force.quiz.core.dto.CreateTagDTO
import fm.force.quiz.core.exception.ValidationError
import fm.force.quiz.core.repository.JpaTagRepository
import fm.force.quiz.security.service.AuthenticationFacade


@Service
class TagService(
        val authenticationFacade: AuthenticationFacade,
        val jpaTagRepository: JpaTagRepository,
        val validationProps: TagValidationProperties
) {
    companion object {
        private val slugifier = Slugify()
        fun slugify(text: String): String = slugifier.slugify(text)
    }

    val tagValidator = ValidatorBuilder.of<Tag>()
            .konstraint(Tag::name) {
                greaterThanOrEqual(validationProps.minTagLength).message("Tag must be at least ${validationProps.minTagLength} characters long")
            }
            .konstraint(Tag::slug) {
                notBlank().message("There was a problem creating a slug")
            }
            .build()

    fun validate(tag: Tag) = tagValidator.validate(tag).throwIfInvalid { ValidationError(it) }

    fun create(tagDTO: CreateTagDTO): Tag {
        val tag = Tag(
                owner = authenticationFacade.user,
                name = tagDTO.name,
                slug = slugify(tagDTO.name)
        )
        validate(tag)
        return jpaTagRepository.save(tag)
    }
}
