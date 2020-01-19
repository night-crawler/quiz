package fm.force.quiz.core.dto

data class QuizSessionQuestionFullDTO(
    val id: Long
) : DTOFullSerializationMarker

data class QuizSessionQuestionRestrictedDTO(
    val id: Long
) : DTORestrictedSerializationMarker

data class QuizSessionQuestionPatchDTO(
    val id: Long?
) : DTOMarker

data class QuizSessionQuestionSearchDTO(
    val id: Long
) : DTOSearchMarker
