package fm.force.quiz.core.dto

import fm.force.quiz.core.entity.Question

data class PatchQuestionDTO(
        val text: String? = null,
        val answers: Set<Long>? = null,
        val correctAnswers: Set<Long>? = null,
        val tags: Set<Long>? = null,
        val topics: Set<Long>? = null,
        val difficulty: Int? = 0
)

data class QuestionDTO(
        val id: Long,
        val owner: Long,
        val answers: Collection<AnswerDTO>,
        val correctAnswers: Collection<AnswerDTO>,
        val tags: Collection<TagDTO>,
        val topics: Collection<TopicDTO>,
        val difficulty: Int
)


fun Question.toDTO() = QuestionDTO(
        id = id,
        owner = owner.id,
        answers = answers.map { it.toDTO() },
        correctAnswers = correctAnswers.map { it.toDTO() },
        tags = tags.map { it.toDTO() },
        topics = topics.map { it.toDTO() },
        difficulty = difficulty
)
