package fm.force.quiz.core.service

import am.ik.yavi.builder.ValidatorBuilder
import fm.force.quiz.configuration.properties.QuestionValidationProperties
import fm.force.quiz.core.dto.PageDTO
import fm.force.quiz.core.dto.QuizSessionAnswerFullDTO
import fm.force.quiz.core.dto.QuizSessionAnswerPatchDTO
import fm.force.quiz.core.dto.toDTO
import fm.force.quiz.core.dto.toFullDTO
import fm.force.quiz.core.dto.toRestrictedDTO
import fm.force.quiz.core.entity.QuizSessionAnswer
import fm.force.quiz.core.repository.AnswerRepository
import fm.force.quiz.core.repository.QuizQuestionRepository
import fm.force.quiz.core.repository.QuizSessionAnswerRepository
import fm.force.quiz.core.repository.QuizSessionRepository
import fm.force.quiz.core.validator.fkConstraint
import fm.force.quiz.core.validator.fksConstraint
import fm.force.quiz.core.validator.ownedFkConstraint
import javax.transaction.Transactional
import org.springframework.data.domain.Page
import org.springframework.stereotype.Service

@Service
class QuizSessionAnswerService(
    private val quizSessionRepository: QuizSessionRepository,
    private val quizQuestionRepository: QuizQuestionRepository,
    private val answerRepository: AnswerRepository,
    quizSessionAnswerRepository: QuizSessionAnswerRepository,
    questionValidationProperties: QuestionValidationProperties
) : QuizSessionAnswerServiceType(quizSessionAnswerRepository) {

    private val msgWrongQuizQuestion = "Must belong to the same quiz as of the session's"
    private val msgWrongAnswers = "Answers must belong to the question"
    private val msgCancelled = "In order to answer the question, quiz session must be neither completed nor cancelled"

    override var dtoValidator = ValidatorBuilder.of<QuizSessionAnswerPatchDTO>()
        .ownedFkConstraint(QuizSessionAnswerPatchDTO::quizSession, quizSessionRepository, ::ownerId)
        .fkConstraint(QuizSessionAnswerPatchDTO::quizQuestion, quizQuestionRepository)
        .fksConstraint(QuizSessionAnswerPatchDTO::answers, answerRepository, 1..questionValidationProperties.maxAnswers)
        .build()

    override var entityValidator = ValidatorBuilder.of<QuizSessionAnswer>()
        .constraintOnTarget(
            { it.quizQuestion.quiz.id == it.quizSession.quiz.id },
            "quizQuestion", "", msgWrongQuizQuestion
        )
        .constraintOnTarget(
            { it.answers.map { a -> a.id }.toSet() == setOf(it.question.id) },
            "answers", "", msgWrongAnswers
        )
        .constraintOnTarget({ !it.quizSession.isCancelled }, "quizSession", "", msgCancelled)
        .constraintOnTarget({ !it.quizSession.isCompleted }, "quizSession", "", msgCancelled)
        .build()

    override fun serializePage(page: Page<QuizSessionAnswer>): PageDTO =
        page.toDTO { it.toRestrictedDTO() }

    override fun serializeEntity(entity: QuizSessionAnswer): QuizSessionAnswerFullDTO =
        entity.toFullDTO()

    @Transactional
    override fun create(createDTO: QuizSessionAnswerPatchDTO): QuizSessionAnswer {
        validateCreate(createDTO)
        val quizSession = quizSessionRepository.getEntity(createDTO.quizSession)
        val quizQuestion = quizQuestionRepository.getEntity(createDTO.quizQuestion)
        val answers = answerRepository.findAllById(createDTO.answers)

        val entity = QuizSessionAnswer(
            owner = authenticationFacade.user,
            quizSession = quizSession,
            quiz = quizSession.quiz,
            quizQuestion = quizQuestion,
            question = quizQuestion.question,
            answers = answers.toSet()
        )
        validateEntity(entity)

        return entity
    }
}
