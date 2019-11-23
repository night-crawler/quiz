package fm.force.quiz.core.service

import am.ik.yavi.builder.ValidatorBuilder
import am.ik.yavi.builder.konstraint
import am.ik.yavi.core.ConstraintViolation
import am.ik.yavi.core.ConstraintViolations
import fm.force.quiz.core.dto.CreateQuestionDTO
import fm.force.quiz.core.dto.ErrorResponse
import fm.force.quiz.core.dto.FieldError
import fm.force.quiz.core.exception.ValidationError
import fm.force.quiz.core.repository.JpaAnswerRepository
import fm.force.quiz.core.repository.JpaQuestionRepository
import fm.force.quiz.core.repository.JpaTagRepository
import fm.force.quiz.core.repository.JpaTopicRepository
import fm.force.quiz.core.validator.fkValidator
import fm.force.quiz.security.service.AuthenticationFacade
import org.springframework.stereotype.Service


@Service
class QuestionValidationService(
        val jpaQuestionRepository: JpaQuestionRepository,
        val jpaAnswerRepository: JpaAnswerRepository,
        val jpaTagRepository: JpaTagRepository,
        val jpaTopicRepository: JpaTopicRepository,
        val authenticationFacade: AuthenticationFacade
) {
    // all field names must remain same in order to be able to be merged
    inner class CreateQuestionDTOWrapper(private val instance: CreateQuestionDTO) {
        private val ownerId = authenticationFacade.principal.id!!
        val correctAnswers get() = instance.correctAnswers - instance.answers == emptySet<Long>()
        val answers get() = jpaAnswerRepository.findOwnedIds(instance.answers, ownerId).toSet() == instance.answers
        val tags get() = jpaTagRepository.findOwnedIds(instance.tags, ownerId).toSet() == instance.tags
        val topics get() = jpaTopicRepository.findOwnedIds(instance.tags, ownerId).toSet() == instance.tags
    }

    val questionDTOValidator = ValidatorBuilder.of<CreateQuestionDTO>()
            .konstraint(CreateQuestionDTO::text) {
                greaterThan(5).message("Must be at least 5 characters long")
            }

            .konstraint(CreateQuestionDTO::answers) {
                lessThan(10).message("Only 10 answers are allowed")
                greaterThan(0).message("Provide at least one answer")
            }
            .forEach(CreateQuestionDTO::answers, "answers", fkValidator)

            .konstraint(CreateQuestionDTO::correctAnswers) {
                lessThan(10).message("Only 10 correct answers are allowed")
                greaterThan(0).message("Provide at least one correct answer")
            }
            .forEach(CreateQuestionDTO::correctAnswers, "correctAnswers", fkValidator)

            .konstraint(CreateQuestionDTO::tags) {
                lessThan(20).message("Only 20 tags are allowed")
            }
            .forEach(CreateQuestionDTO::tags, "tags", fkValidator)

            .forEach(CreateQuestionDTO::topics, "topics", fkValidator)

            .konstraint(CreateQuestionDTO::difficulty) {
                greaterThanOrEqual(0).message("Must be greater than 0")
            }
            .build()

    val questionDTOMiscValidator = ValidatorBuilder.of<CreateQuestionDTOWrapper>()
            .konstraint(CreateQuestionDTOWrapper::correctAnswers) {
                isTrue.message("Must be a subset of all answers")
            }
            .konstraint(CreateQuestionDTOWrapper::answers) {
                isTrue.message("Provided answers seem not to belong to you or do not exist")
            }
            .konstraint(CreateQuestionDTOWrapper::topics) {
                isTrue.message("Provided topics seem not to belong to you or do not exist")
            }
            .konstraint(CreateQuestionDTOWrapper::tags) {
                isTrue.message("Provided tags seem not to belong to you or do not exist")
            }
            .build()

    fun validate(question: CreateQuestionDTO): String {
        var violations = questionDTOValidator.validate(question)
        if (!violations.isValid) {
            throw ValidationError(violations)
        }

        violations = questionDTOMiscValidator.validate(CreateQuestionDTOWrapper(question))
        if (!violations.isValid) {
            throw ValidationError(violations)
        }

        return "Good. Construct an object"
    }
}
