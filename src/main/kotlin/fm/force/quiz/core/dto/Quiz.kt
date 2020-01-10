package fm.force.quiz.core.dto

import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer
import fm.force.quiz.core.entity.Quiz
import java.time.Instant

data class QuizFullDTO(
        @JsonSerialize(using = ToStringSerializer::class) val id: Long,
        val title: String,
        val tags: Collection<TagFullDTO>,
        val topics: Collection<TopicFullDTO>,
        val quizQuestions: Collection<QuizQuestionFullDTO>,
        val difficultyScale: DifficultyScaleFullDTO?,
        val createdAt: Instant,
        val updatedAt: Instant
) : DTOFullSerializationMarker

data class PatchQuizDTO(
        val title: String? = null,
        val questions: Collection<Long>? = null,
        val tags: Set<Long>? = null,
        val topics: Set<Long>? = null,
        val difficultyScale: Long? = null
) : DTOMarker


data class QuizRestrictedDTO(
        @JsonSerialize(using = ToStringSerializer::class) val id: Long,
        @JsonSerialize(using = ToStringSerializer::class) val owner: Long,
        val title: String,
        val tags: Collection<TagRestrictedDTO>,
        val topics: Collection<TopicRestrictedDTO>,
        val quizQuestions: Collection<QuizQuestionRestrictedDTO>,
        val difficultyScale: DifficultyScaleRestrictedDTO?
) : DTORestrictedSerializationMarker

fun Quiz.toFullDTO() = QuizFullDTO(
        id = id,
        title = title,
        tags = tags.map { it.toFullDTO() },
        topics = topics.map { it.toFullDTO() },
        quizQuestions = quizQuestions.map { it.toFullDTO() },
        difficultyScale = difficultyScale?.toFullDTO(),
        createdAt = createdAt,
        updatedAt = updatedAt
)

fun Quiz.toRestrictedDTO() = QuizRestrictedDTO(
        id = id,
        owner = owner.id,
        title = title,
        tags = tags.map { it.toRestrictedDTO() },
        topics = topics.map { it.toRestrictedDTO() },
        quizQuestions = quizQuestions.map { it.toRestrictedDTO() },
        difficultyScale = difficultyScale?.toRestrictedDTO()
)