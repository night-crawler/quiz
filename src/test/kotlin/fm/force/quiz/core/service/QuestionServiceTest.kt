package fm.force.quiz.core.service

import fm.force.quiz.core.dto.PaginationQuery
import fm.force.quiz.core.dto.QuestionPatchDTO
import fm.force.quiz.core.dto.SortQuery
import fm.force.quiz.core.entity.Answer
import fm.force.quiz.core.entity.Tag
import fm.force.quiz.core.entity.Topic
import fm.force.quiz.core.exception.ValidationError
import io.kotlintest.TestCase
import io.kotlintest.data.forall
import io.kotlintest.matchers.collections.shouldHaveSize
import io.kotlintest.shouldBe
import io.kotlintest.shouldThrow
import io.kotlintest.tables.row

open class QuestionServiceTest(questionService: QuestionService) : GenericCRUDServiceTest() {
    private lateinit var answer: Answer
    private lateinit var tag: Tag
    private lateinit var topic: Topic

    override fun beforeTest(testCase: TestCase) {
        super.beforeTest(testCase)
        answer = testDataFactory.getAnswer(owner = user)
        tag = testDataFactory.getTag(owner = user)
        topic = testDataFactory.getTopic(owner = user)
    }

    init {
        "should validate" {
            forall(
                    row(QuestionPatchDTO("sample", setOf(1, 2), setOf(3, 4), setOf(5), setOf(6))),
                    row(QuestionPatchDTO("", setOf(1, 22), setOf(1, 33), setOf(44), setOf(66))),
                    row(QuestionPatchDTO("", emptySet(), emptySet(), emptySet(), emptySet()))
            ) {
                shouldThrow<ValidationError> { questionService.validatePatch(it) }
            }

            forall(
                    row(QuestionPatchDTO(
                            text = "valid sample",
                            answers = setOf(answer.id), correctAnswers = setOf(answer.id),
                            tags = emptySet(), topics = emptySet())),
                    row(QuestionPatchDTO(
                            text = "valid sample",
                            answers = setOf(answer.id), correctAnswers = setOf(answer.id),
                            tags = setOf(tag.id), topics = setOf(topic.id)))
            ) {
                questionService.validatePatch(it)
            }
        }

        "should paginate" {
            (1..5).map { testDataFactory.getQuestion(owner = user, text = "Surprise question $it") }
            (1..5).map { testDataFactory.getQuestion(owner = alien, text = "Surprise alien question $it") }

            val page = questionService.find(PaginationQuery.default(), SortQuery.byIdDesc(), "SurPRise")
            page.content shouldHaveSize 5
        }

        "should update" {
            val tag = testDataFactory.getTag(owner = user)
            val topic = testDataFactory.getTopic(owner = user)
            val answer = testDataFactory.getAnswer(owner = user)
            val question = testDataFactory.getQuestion(owner = user)

            val patchDTO = QuestionPatchDTO(
                    text = "new text",
                    answers = setOf(answer.id),
                    correctAnswers = setOf(answer.id),
                    topics = setOf(topic.id),
                    tags = setOf(tag.id),
                    difficulty = 100500
            )

            val updatedQuestion = questionService.patch(question.id, patchDTO)
            updatedQuestion.text shouldBe "new text"
            updatedQuestion.answers shouldBe mutableSetOf(answer)
            updatedQuestion.correctAnswers shouldBe mutableSetOf(answer)
            updatedQuestion.tags shouldBe mutableSetOf(tag)
            updatedQuestion.topics shouldBe mutableSetOf(topic)
            updatedQuestion.difficulty shouldBe 100500
        }
    }
}
