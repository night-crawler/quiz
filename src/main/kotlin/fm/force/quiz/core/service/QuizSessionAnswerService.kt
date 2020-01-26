package fm.force.quiz.core.service

import am.ik.yavi.builder.ValidatorBuilder
import fm.force.quiz.configuration.properties.QuestionValidationProperties
import fm.force.quiz.core.dto.PageDTO
import fm.force.quiz.core.dto.QuizSessionAnswerPatchDTO
import fm.force.quiz.core.dto.QuizSessionAnswerRestrictedDTO
import fm.force.quiz.core.dto.toDTO
import fm.force.quiz.core.dto.toRestrictedDTO
import fm.force.quiz.core.entity.QuizSessionAnswer
import fm.force.quiz.core.repository.QuizSessionAnswerRepository
import fm.force.quiz.core.repository.QuizSessionQuestionAnswerRepository
import fm.force.quiz.core.repository.QuizSessionQuestionRepository
import fm.force.quiz.core.repository.QuizSessionRepository
import fm.force.quiz.core.validator.ownedFkConstraint
import fm.force.quiz.core.validator.ownedFksConstraint
import org.springframework.data.domain.Page
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class QuizSessionAnswerService(
    private val quizSessionRepository: QuizSessionRepository,
    private val quizSessionQuestionRepository: QuizSessionQuestionRepository,
    private val quizSessionQuestionAnswerRepository: QuizSessionQuestionAnswerRepository,
    quizSessionAnswerRepository: QuizSessionAnswerRepository,
    questionValidationProperties: QuestionValidationProperties
) : QuizSessionAnswerServiceType(quizSessionAnswerRepository) {

    private val msgWrongQuizQuestion = "Must belong to the same quiz as of the session's"
    private val msgWrongAnswers = "Answers must belong to the question"
    private val msgCancelled = "In order to answer the question, quiz session must be neither completed nor cancelled"

    override var dtoValidator = ValidatorBuilder.of<QuizSessionAnswerPatchDTO>()
        .ownedFkConstraint(QuizSessionAnswerPatchDTO::session, quizSessionRepository, ::ownerId)
        .ownedFksConstraint(
            QuizSessionAnswerPatchDTO::answers,
            quizSessionQuestionAnswerRepository,
            1..questionValidationProperties.maxAnswers,
            ::ownerId
        )
        .build()

    override var entityValidator = ValidatorBuilder.of<QuizSessionAnswer>()
        .constraintOnTarget(
            { it.quizSessionQuestion.quizSession.id == it.quizSession.id },
            "quizQuestion", "", msgWrongQuizQuestion
        )
        .constraintOnTarget(
            // todo: make a query
            { it.answers.map { a -> a.quizSession.id }.toSet() == setOf(it.quizSession.id) },
            "answers", "", msgWrongAnswers
        )
        .constraintOnTarget({ !it.quizSession.isCancelled }, "quizSession", "", msgCancelled)
        .constraintOnTarget({ !it.quizSession.isCompleted }, "quizSession", "", msgCancelled)
        .build()

    @Transactional(readOnly = true)
    override fun serializePage(page: Page<QuizSessionAnswer>): PageDTO =
        page.toDTO { it.toRestrictedDTO() }

    @Transactional(readOnly = true)
    override fun serializeEntity(entity: QuizSessionAnswer): QuizSessionAnswerRestrictedDTO =
        repository.refresh(entity).toRestrictedDTO()

    @Transactional
    override fun create(createDTO: QuizSessionAnswerPatchDTO): QuizSessionAnswer {
        validateCreate(createDTO)
        val quizSession = quizSessionRepository.getEntity(createDTO.session)
        val quizSessionQuestion = quizSessionQuestionRepository.getEntity(createDTO.question)
        val answers = quizSessionQuestionAnswerRepository.findAllById(createDTO.answers)

        val entity = QuizSessionAnswer(
            owner = authenticationFacade.user,
            quizSession = quizSession,
            quizSessionQuestion = quizSessionQuestion,
            answers = answers.toSet()
        )
        validateEntity(entity)

        return repository.save(entity)
    }
}
