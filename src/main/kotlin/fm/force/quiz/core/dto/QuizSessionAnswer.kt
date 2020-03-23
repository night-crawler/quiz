package fm.force.quiz.core.dto

data class QuizSessionAnswerFullDTO(
    val id: Long
) : DTOFullSerializationMarker

data class QuizSessionAnswerRestrictedDTO(
    val id: Long,
    val session: Long,
    val question: QuizSessionQuestionRestrictedDTO,
    val answers: List<QuizSessionQuestionAnswerRestrictedDTO>
) : DTORestrictedSerializationMarker

data class QuizSessionAnswerPatchDTO(
    var session: Long = 0,
    val question: Long,
    val answers: Set<Long>
) : DTOSerializationMarker

data class QuizSessionAnswerSearchDTO(
    val id: Long? = null
) : DTOSearchMarker
