package fm.force.quiz.core.dto

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
