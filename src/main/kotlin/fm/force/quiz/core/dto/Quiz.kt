package fm.force.quiz.core.dto

import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer
import java.time.Instant

data class QuizFullDTO(
    @JsonSerialize(using = ToStringSerializer::class)
    val id: Long,
    val title: String,
    val tags: Collection<TagFullDTO>,
    val topics: Collection<TopicFullDTO>,
    val quizQuestions: Collection<QuizQuestionFullDTO>,
    val difficultyScale: DifficultyScaleFullDTO?,
    val createdAt: Instant,
    val updatedAt: Instant
) : DTOFullSerializationMarker

data class QuizPatchDTO(
    val title: String? = null,
    val questions: Collection<Long>? = null,
    val tags: Set<Long>? = null,
    val topics: Set<Long>? = null,
    val difficultyScale: Long? = null
) : DTOMarker

data class QuizRestrictedDTO(
    @JsonSerialize(using = ToStringSerializer::class)
    val id: Long,
    @JsonSerialize(using = ToStringSerializer::class)
    val owner: Long,
    val title: String,
    val tags: Collection<TagRestrictedDTO>,
    val topics: Collection<TopicRestrictedDTO>,
    val quizQuestions: Collection<QuizQuestionRestrictedDTO>,
    val difficultyScale: DifficultyScaleRestrictedDTO?
) : DTORestrictedSerializationMarker
