package fm.force.quiz.core.dto

import kotlinx.serialization.ContextualSerialization
import kotlinx.serialization.Serializable


@Serializable
data class QuizQuestionFullDTO(
    @ContextualSerialization
    val id: Long,
    val seq: Int,
    val question: QuestionFullDTO,
    @ContextualSerialization
    val createdAt: InstantAlias,
    @ContextualSerialization
    val updatedAt: InstantAlias
) : DTOFullSerializationMarker

@Serializable
data class QuizQuestionRestrictedDTO(
    @ContextualSerialization
    val id: Long,
    val seq: Int,
    val question: QuestionRestrictedDTO
) : DTORestrictedSerializationMarker

@Serializable
data class QuizQuestionPatchDTO(
    @ContextualSerialization
    var id: Long? = null,
    @ContextualSerialization
    var quiz: Long? = null,
    var question: Long? = null,
    var seq: Int? = null
) : DTOSerializationMarker

@Serializable
data class QuizQuestionSearchDTO(
    @ContextualSerialization
    val quiz: Long
) : DTOSearchMarker
