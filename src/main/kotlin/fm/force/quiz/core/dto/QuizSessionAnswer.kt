package fm.force.quiz.core.dto

import fm.force.quiz.core.entity.QuizSessionAnswer

data class QuizSessionAnswerFullDTO(
    val id: Long
) : DTOFullSerializationMarker

data class QuizSessionAnswerRestrictedDTO(
    val id: Long
)

data class QuizSessionAnswerPatchDTO(
    val id: Long? = null,
    val sessionId: Long? = null,
    val quizQuestion: Long? = null
) : DTOSerializationMarker

data class QuizSessionAnswerSearchDTO(
    val id: Long? = null
) : DTOSearchMarker

fun QuizSessionAnswer.toFullDTO() = QuizSessionAnswerFullDTO(
    id = id
)

fun QuizSessionAnswer.toRestrictedDTO() = QuizSessionAnswerRestrictedDTO(
    id = id
)
