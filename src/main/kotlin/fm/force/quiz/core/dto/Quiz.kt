package fm.force.quiz.core.dto

import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer
import kotlinx.serialization.ContextualSerialization
import kotlinx.serialization.Serializable
import java.time.Instant

@Serializable
data class QuizFullDTO(
    @ContextualSerialization
    val id: Long,

    val title: String,
    val tags: Collection<TagFullDTO>,
    val topics: Collection<TopicFullDTO>,
    val quizQuestions: Collection<QuizQuestionFullDTO>,
    val difficultyScale: DifficultyScaleFullDTO?,

    @ContextualSerialization
    val createdAt: Instant,

    @ContextualSerialization
    val updatedAt: Instant
) : DTOFullSerializationMarker

@Serializable
data class QuizPatchDTO(
    val title: String? = null,
    val questions: Collection<Long>? = null,
    val tags: Set<Long>? = null,
    val topics: Set<Long>? = null,
    val difficultyScale: Long? = null
) : DTOMarker

@Serializable
data class QuizRestrictedDTO(
    @ContextualSerialization
    val id: Long,

    @ContextualSerialization
    val owner: Long,

    val title: String,
    val tags: Collection<TagRestrictedDTO>,
    val topics: Collection<TopicRestrictedDTO>,
    val quizQuestions: Collection<QuizQuestionRestrictedDTO>,
    val difficultyScale: DifficultyScaleRestrictedDTO?
) : DTORestrictedSerializationMarker
