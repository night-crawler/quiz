package io.kotlintest.provided.fm.force.quiz.core.service

import fm.force.quiz.core.dto.QuizQuestionPatchDTO
import fm.force.quiz.core.exception.ValidationError
import fm.force.quiz.core.service.GenericCRUDServiceTest
import fm.force.quiz.core.service.QuizQuestionService
import io.kotlintest.data.forall
import io.kotlintest.shouldThrow
import io.kotlintest.tables.row

open class QuizQuestionServiceTest(
        service: QuizQuestionService
) : GenericCRUDServiceTest() {

    init {
        "should validate" {
            val quiz = testDataFactory.getQuiz(owner = user)
            val quizQuestion = quiz.quizQuestions.random()

            forall(
                    row(QuizQuestionPatchDTO(id = 1)),
                    row(QuizQuestionPatchDTO(id = quizQuestion.id, quiz = quiz.id, question = quizQuestion.id, seq = -2)),
                    row(QuizQuestionPatchDTO(id = null, quiz = quiz.id)),
                    row(QuizQuestionPatchDTO(question = quizQuestion.id))
            ) {
                shouldThrow<ValidationError> { service.create(it) }
            }
        }
    }

}