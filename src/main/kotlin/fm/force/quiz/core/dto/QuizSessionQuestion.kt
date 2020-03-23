package fm.force.quiz.core.dto

data class QuizSessionQuestionFullDTO(
    val id: Long
) : DTOFullSerializationMarker

data class QuizSessionQuestionRestrictedDTO(
    val id: Long,
    val text: String,
    val answers: List<QuizSessionQuestionAnswerRestrictedDTO>,
    val seq: Int
) : DTORestrictedSerializationMarker

data class QuizSessionQuestionPatchDTO(
    val id: Long?
) : DTOMarker

data class QuizSessionQuestionSearchDTO(
    var quizSession: Long? = null,
    val originalQuestion: Long? = null,
    val text: String? = null
) : DTOSearchMarker
