package fm.force.quiz.core.service

import am.ik.yavi.builder.ValidatorBuilder
import am.ik.yavi.builder.konstraint
import fm.force.quiz.configuration.properties.QuestionValidationProperties
import fm.force.quiz.core.dto.CreateQuestionDTO
import fm.force.quiz.core.entity.Question
import fm.force.quiz.core.exception.ValidationError
import fm.force.quiz.core.repository.JpaAnswerRepository
import fm.force.quiz.core.repository.JpaQuestionRepository
import fm.force.quiz.core.repository.JpaTagRepository
import fm.force.quiz.core.repository.JpaTopicRepository
import fm.force.quiz.core.validator.fkValidator
import fm.force.quiz.security.service.AuthenticationFacade
import org.springframework.stereotype.Service
import javax.transaction.Transactional


@Service
class QuestionService(
        val jpaQuestionRepository: JpaQuestionRepository,
        val jpaAnswerRepository: JpaAnswerRepository,
        val jpaTagRepository: JpaTagRepository,
        val jpaTopicRepository: JpaTopicRepository,
        val authenticationFacade: AuthenticationFacade,
        val validationProps: QuestionValidationProperties
) {
    // all field names must remain same in order to be able to be merged
    inner class CreateQuestionDTOWrapper(private val instance: CreateQuestionDTO) {
        private val ownerId = authenticationFacade.principal.id!!
        val correctAnswers get() = instance.correctAnswers - instance.answers == emptySet<Long>()
        val answers get() = jpaAnswerRepository.findOwnedIds(instance.answers, ownerId).toSet() == instance.answers

        val tags
            get() = if (instance.tags.isNotEmpty())
                jpaTagRepository.findOwnedIds(instance.tags, ownerId).toSet() == instance.tags
            else true

        // ! we cannot query empty lists like this, hibernate will fail
        val topics
            get() = if (instance.topics.isNotEmpty())
                jpaTopicRepository.findOwnedIds(instance.topics, ownerId).toSet() == instance.topics
            else true
    }

    val questionDTOValidator = ValidatorBuilder.of<CreateQuestionDTO>()
            .konstraint(CreateQuestionDTO::text) {
                greaterThan(validationProps.minTextLength).message("Must be at least ${validationProps.minTextLength} characters long")
            }

            .konstraint(CreateQuestionDTO::answers) {
                lessThan(validationProps.maxAnswers).message("Only ${validationProps.maxAnswers} answers are allowed")
                greaterThan(0).message("Provide at least one answer")
            }
            .forEach(CreateQuestionDTO::answers, "answers", fkValidator)

            .konstraint(CreateQuestionDTO::correctAnswers) {
                lessThan(validationProps.maxAnswers).message("Only ${validationProps.maxAnswers} correct answers are allowed")
                greaterThan(0).message("Provide at least one correct answer")
            }
            .forEach(CreateQuestionDTO::correctAnswers, "correctAnswers", fkValidator)

            .konstraint(CreateQuestionDTO::tags) {
                lessThan(validationProps.maxTags).message("Only ${validationProps.maxTags} tags are allowed")
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
                isTrue.message("Provided tags do not belong to you or do not exist")
            }
            .build()

    fun validate(question: CreateQuestionDTO) {
        questionDTOValidator
                .validate(question)
                .throwIfInvalid { ValidationError(it) }

        questionDTOMiscValidator
                .validate(CreateQuestionDTOWrapper(question))
                .throwIfInvalid { ValidationError(it) }
    }

    @Transactional
    fun create(createQuestionDTO: CreateQuestionDTO): Question {
        validate(createQuestionDTO)

        val answersMap = jpaAnswerRepository
                .findAllById(createQuestionDTO.answers)
                .map { it.id to it }.toMap()

        val question = Question(
                owner = authenticationFacade.user,
                text = createQuestionDTO.text,
                answers = createQuestionDTO.answers.map { answersMap[it]!! }.toSet(),
                correctAnswers = createQuestionDTO.correctAnswers.map { answersMap[it]!! }.toSet(),
                tags = jpaTagRepository.findAllById(createQuestionDTO.tags).toSet(),
                topics = jpaTopicRepository.findAllById(createQuestionDTO.topics).toSet(),
                difficulty = createQuestionDTO.difficulty
        )

        return jpaQuestionRepository.save(question)
    }
}
