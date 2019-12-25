package fm.force.quiz.core.dto

import fm.force.quiz.core.entity.Question

data class CreateQuestionDTO(
        val text: String,
        val answers: Set<Long>,
        val correctAnswers: Set<Long>,
        var tags: Set<Long>,
        var topics: Set<Long>,
        var difficulty: Int = 0
)

data class QuestionDTO(
        val id: Long,
        val owner: Long
)


fun Question.toDTO() = QuestionDTO(
        id = id,
        owner = owner.id
)
