package fm.force.quiz.core.dto

import fm.force.quiz.core.entity.QuizSessionAnswer

data class QuizSessionAnswerFullDTO(
    val id: Long
) : DTOFullSerializationMarker

data class QuizSessionAnswerRestrictedDTO(
    val id: Long
)

data class QuizSessionAnswerPatchDTO(
    val quizSession: Long,
    val quizQuestion: Long,
    val answers: Set<Long>
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
