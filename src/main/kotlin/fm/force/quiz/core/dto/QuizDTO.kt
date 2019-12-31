package fm.force.quiz.core.dto

import fm.force.quiz.core.entity.Quiz

data class QuizDTO(
        val id: Long,
        val owner: Long,
        val tags: Collection<TagDTO>,
        val topics: Collection<TopicDTO>,
        val difficultyScale: DifficultyScaleDTO?
)

data class PatchQuizDTO(
        val title: String? = null,
        val questions: Set<Long>? = null,
        val tags: Set<Long>? = null,
        val topics: Set<Long>? = null,
        val difficultyScale: Long? = null
)


fun Quiz.toDTO() = QuizDTO(
        id = id,
        owner = owner.id,
        tags = tags.map { it.toDTO() },
        topics = topics.map { it.toDTO() },
        difficultyScale = difficultyScale?.toDTO()
)
