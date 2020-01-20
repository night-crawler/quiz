package fm.force.quiz.core.dto

import fm.force.quiz.core.entity.QuizSessionQuestionAnswer

data class QuizSessionQuestionAnswerFullDTO(
    val id: Long,
    val quizSession: Long,
    val quizSessionQuestion: Long,
    val originalAnswer: Long?,
    val text: String,
    val isCorrect: Boolean
) : DTOFullSerializationMarker

data class QuizSessionQuestionAnswerRestrictedDTO(
    val id: Long,
    val text: String
)

fun QuizSessionQuestionAnswer.toFullDTO() = QuizSessionQuestionAnswerFullDTO(
    id = id,
    quizSession = quizSession.id,
    quizSessionQuestion = quizSessionQuestion.id,
    originalAnswer = originalAnswer?.id,
    text = text,
    isCorrect = isCorrect
)

fun QuizSessionQuestionAnswer.toRestrictedDTO() = QuizSessionQuestionAnswerRestrictedDTO(
    id = id,
    text = text
)
