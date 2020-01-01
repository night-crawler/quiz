package fm.force.quiz.core.dto

import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer
import fm.force.quiz.core.entity.Quiz

data class QuizDTO(
        @JsonSerialize(using = ToStringSerializer::class) val id: Long,
        @JsonSerialize(using = ToStringSerializer::class) val owner: Long,
        val tags: Collection<TagDTO>,
        val topics: Collection<TopicDTO>,
        val difficultyScale: DifficultyScaleDTO?
) : DTOSerializeMarker

data class PatchQuizDTO(
        val title: String? = null,
        val questions: Set<Long>? = null,
        val tags: Set<Long>? = null,
        val topics: Set<Long>? = null,
        val difficultyScale: Long? = null
) : DTOMarker


fun Quiz.toDTO() = QuizDTO(
        id = id,
        owner = owner.id,
        tags = tags.map { it.toDTO() },
        topics = topics.map { it.toDTO() },
        difficultyScale = difficultyScale?.toDTO()
)
