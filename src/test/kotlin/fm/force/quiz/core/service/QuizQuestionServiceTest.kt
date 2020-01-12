package io.kotlintest.provided.fm.force.quiz.core.service

import fm.force.quiz.core.dto.QuizQuestionPatchDTO
import fm.force.quiz.core.entity.QuizQuestion
import fm.force.quiz.core.exception.ValidationError
import fm.force.quiz.core.repository.JpaQuizQuestionRepository
import fm.force.quiz.core.service.AbstractCRUDServiceTest
import fm.force.quiz.core.service.QuizQuestionService
import io.kotlintest.data.forall
import io.kotlintest.shouldBe
import io.kotlintest.shouldThrow
import io.kotlintest.tables.row
import org.springframework.dao.DataIntegrityViolationException

open class QuizQuestionServiceTest(
        private val service: QuizQuestionService,
        private val jpaQuizQuestionRepository: JpaQuizQuestionRepository
) : AbstractCRUDServiceTest() {

    init {
        "should validate" {
            val quiz = testDataFactory.getQuiz(owner = user)
            val quizQuestion = quiz.quizQuestions.random()
            val usedQuestion = quizQuestion.question
            val freeQuestion = testDataFactory.getQuestion(owner = user)

            forall(
                    // insufficient data attributes
                    row(QuizQuestionPatchDTO(id = 1)),
                    row(QuizQuestionPatchDTO(id = null, quiz = quiz.id)),
                    row(QuizQuestionPatchDTO(question = quizQuestion.id)),

                    // negative sequence with everything else properly formed
                    row(QuizQuestionPatchDTO(id = quizQuestion.id, quiz = quiz.id, question = usedQuestion.id, seq = -2))
            ) {
                shouldThrow<ValidationError> { service.create(it) }
            }
            // duplicate relation
            val duplicateRelation = QuizQuestionPatchDTO(quiz = quiz.id, question = usedQuestion.id, seq = 2)
            shouldThrow<DataIntegrityViolationException> { service.create(duplicateRelation) }

            // all randomly created instances don't use Int.MAX_VALUE, so this one must always be the highest
            val withWrongSeq = QuizQuestionPatchDTO(quiz = quiz.id, question = freeQuestion.id, seq = Int.MAX_VALUE)
            shouldThrow<ValidationError> { service.create(withWrongSeq) }
        }

        "should create a QuizQuestion with respect to a specified sequence" {
            val questions = (0..4).map { testDataFactory.getQuestion(owner = user) }
            val quiz = testDataFactory.getQuiz(owner = user, questions = mutableSetOf())

            // -1 stands for the last one
            val qq1 = service.create(QuizQuestionPatchDTO(quiz = quiz.id, question = questions[0].id, seq = -1))
            // should take into account inserting elements with occupied seq
            val qq2 = service.create(QuizQuestionPatchDTO(quiz = quiz.id, question = questions[1].id, seq = 0))
            // at this moment there are 2 elements [0, 1], so it must be possible to manually set seq to 2
            val qq3 = service.create(QuizQuestionPatchDTO(quiz = quiz.id, question = questions[2].id, seq = 2))

            // should fail on seq >= 3
            shouldThrow<ValidationError> {
                service.create(QuizQuestionPatchDTO(quiz = quiz.id, question = questions[3].id, seq = 4))
            }
            // current order must be as follows: [qq2, qq1, qq3]
            // add fourth question to the top
            val qq4 = service.create(QuizQuestionPatchDTO(quiz = quiz.id, question = questions[3].id, seq = 1))
            // now: [qq2, qq4, qq1, qq3]

            val actualQuizQuestions = jpaQuizQuestionRepository.findAllByQuizIdOrderBySeq(quiz.id)
            listOf(qq2, qq4, qq1, qq3).zip(actualQuizQuestions).map { (expected, actual) ->
                expected.id shouldBe actual.id
            }
        }

        "should delete QuizQuestion's with respect to sequence" {
            val questions = (1..5).map { testDataFactory.getQuestion(owner = user) }.toMutableList()
            val questionIds = questions.map { it.id }.toMutableList()
            val quiz = testDataFactory.getQuiz(owner = user, questions = questions)
            lateinit var removing: QuizQuestion
            lateinit var quizQuestions: List<QuizQuestion>

            quizQuestions = jpaQuizQuestionRepository.findAllByQuizIdOrderBySeq(quiz.id)
            questionIds shouldBe quizQuestions.map { it.question.id }

            removing = quizQuestions[0]
            service.delete(removing.id)
            questionIds.removeAt(0)
            quizQuestions = jpaQuizQuestionRepository.findAllByQuizIdOrderBySeq(quiz.id)
            questionIds shouldBe quizQuestions.map { it.question.id }

            removing = quizQuestions[2]
            service.delete(removing.id)
            questionIds.removeAt(2)
            quizQuestions = jpaQuizQuestionRepository.findAllByQuizIdOrderBySeq(quiz.id)
            questionIds shouldBe quizQuestions.map { it.question.id }

            removing = quizQuestions.last()
            service.delete(removing.id)
            questionIds.remove(questionIds.last())
            quizQuestions = jpaQuizQuestionRepository.findAllByQuizIdOrderBySeq(quiz.id)
            questionIds shouldBe quizQuestions.map { it.question.id }
        }

        "should patch" {
            val questions = (0..4).map { testDataFactory.getQuestion(owner = user) }
            val quiz = testDataFactory.getQuiz(owner = user, questions = questions)

            // 1 -> 3
            // 0 1 2 3 4
            // 0 2 3 4
            // 0 2 3 1 4
            testPatch(quiz.id, 1, 3, 0, 2, 3, 1, 4)

            // 2 -> 3
            // 0 1 2 3 4
            // 0 1 3 2 4
            testPatch(quiz.id, 2, 3, 0, 1, 3, 2, 4)

            // 2 -> 1
            // 0 1 2 3 4
            // 0 2 1 3 4
            testPatch(quiz.id, 2, 1, 0, 2, 1, 3, 4)

            // 2 -> -1
            // 0 1 2 3 4
            // 0 1 3 4 2
            testPatch(quiz.id, 2, -1, 0, 1, 3, 4, 2)

            // 4 -> 0
            // 0 1 2 3 4
            // 4 0 1 2 3
            testPatch(quiz.id, 4, 0, 4, 0, 1, 2, 3)

            // 0 -> 4
            // 0 1 2 3 4
            // 1 2 3 4 0
            testPatch(quiz.id, 0, 4, 1, 2, 3, 4, 0)
        }
    }

    private fun testPatch(quizId: Long, from: Int, to: Int, vararg order: Int) {
        // get initial order of QuizQuestions
        var qqs = jpaQuizQuestionRepository.findAllByQuizIdOrderBySeq(quizId)
        // remember question ids as they were before
        val originalQuestionOrder = qqs.map { it.question.id }
        // remember *question* order because implementation may delete old records
        val expectedQuestionIds = order.map { qqs[it] }.map { it.question.id }

        // move the item
        service.patch(qqs[from].id, QuizQuestionPatchDTO(seq = to))

        // retrieve changes
        qqs = jpaQuizQuestionRepository.findAllByQuizIdOrderBySeq(quizId)

        // get question ids
        val actualQuestionIds = qqs.map { it.question.id }
        val actualOrder = actualQuestionIds.map { originalQuestionOrder.indexOf(it) }

        // changes must reflect the desired item movement
        println("Moving $from -> $to; expected order: ${order.joinToString()}; actual: ${actualOrder.joinToString()}")
        actualQuestionIds shouldBe expectedQuestionIds

        // range must remain continuous
        qqs.map { it.seq } shouldBe (order.indices).toList()
    }

}