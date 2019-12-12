package fm.force.quiz.core.dto

data class CreateQuestionDTO(
        val text: String,
        val answers: Set<Long>,
        val correctAnswers: Set<Long>,
        var tags: Set<Long>,
        var topics: Set<Long>,
        var difficulty: Int = 0
)
