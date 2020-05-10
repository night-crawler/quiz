package fm.force.quiz.core.service

import fm.force.quiz.common.dto.PaginationQuery
import fm.force.quiz.common.dto.QuestionPatchDTO
import fm.force.quiz.common.dto.QuestionSearchQueryDTO
import fm.force.quiz.common.dto.SortQuery
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

val Collection<Tag>.tagsSlugs get() = joinToString(",") { it.slug }
val Collection<Topic>.topicSlugs get() = joinToString(",") { it.slug }

open class QuestionServiceTest(questionService: QuestionService) : AbstractCRUDServiceTest() {
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
                row(
                    QuestionPatchDTO(
                        title = "sample",
                        text = "sample text",
                        answers = setOf(1, 2),
                        correctAnswers = setOf(3, 4),
                        tags = setOf(5),
                        topics = setOf(6)
                    )
                ),
                row(
                    QuestionPatchDTO(
                        title = "",
                        text = "sample",
                        answers = setOf(1, 22),
                        correctAnswers = setOf(1, 33),
                        tags = setOf(44),
                        topics = setOf(66)
                    )
                ),
                row(
                    QuestionPatchDTO(
                        title = "sample",
                        text = "",
                        answers = emptySet(),
                        correctAnswers = emptySet(),
                        tags = emptySet(),
                        topics = emptySet()
                    )
                )
            ) {
                shouldThrow<ValidationError> { questionService.validatePatch(it) }
            }

            forall(
                row(
                    QuestionPatchDTO(
                        text = "valid sample",
                        answers = setOf(answer.id), correctAnswers = setOf(answer.id),
                        tags = emptySet(), topics = emptySet()
                    )
                ),
                row(
                    QuestionPatchDTO(
                        text = "valid sample",
                        answers = setOf(answer.id), correctAnswers = setOf(answer.id),
                        tags = setOf(tag.id), topics = setOf(topic.id)
                    )
                )
            ) {
                questionService.validatePatch(it)
            }
        }

        "should paginate" {
            (1..5).map { testDataFactory.getQuestion(owner = user, text = "Surprise question $it") }
            (1..5).map { testDataFactory.getQuestion(owner = alien, text = "Surprise alien question $it") }

            val page = questionService.find(
                paginationQuery = PaginationQuery.default(),
                sortQuery = SortQuery.byIdDesc(),
                search = QuestionSearchQueryDTO("SurPRise")
            )
            page.content shouldHaveSize 5
        }

        "should update" {
            val tag = testDataFactory.getTag(owner = user)
            val topic = testDataFactory.getTopic(owner = user)
            val answer = testDataFactory.getAnswer(owner = user)
            val question = testDataFactory.getQuestion(owner = user)

            val patchDTO = QuestionPatchDTO(
                title = "new title",
                text = "new text",
                answers = setOf(answer.id),
                correctAnswers = setOf(answer.id),
                topics = setOf(topic.id),
                tags = setOf(tag.id),
                difficulty = 100500
            )

            val updatedQuestion = questionService.patch(question.id, patchDTO)
            updatedQuestion.title shouldBe "new title"
            updatedQuestion.text shouldBe "new text"
            updatedQuestion.answers shouldBe mutableSetOf(answer)
            updatedQuestion.correctAnswers shouldBe mutableSetOf(answer)
            updatedQuestion.tags shouldBe mutableSetOf(tag)
            updatedQuestion.topics shouldBe mutableSetOf(topic)
            updatedQuestion.difficulty shouldBe 100500
        }

        "should search by tags and topics" {
            val topics1 = mutableSetOf(
                testDataFactory.getTopic(owner = user),
                testDataFactory.getTopic(owner = user)
            )
            val tags1 = mutableSetOf(
                testDataFactory.getTag(owner = user),
                testDataFactory.getTag(owner = user)
            )
            val topics2 = mutableSetOf(testDataFactory.getTopic(owner = user))
            val tags2 = mutableSetOf(testDataFactory.getTag(owner = user))

            (1..5).map { testDataFactory.getQuestion(
                owner = user,
                topics = topics1,
                tags = tags1
            ) }
            (1..5).map { testDataFactory.getQuestion(
                owner = user,
                topics = topics2,
                tags = tags2
            ) }

            forall(
                // tags only
                row(QuestionSearchQueryDTO(query = null, tagSlugs = tags1.tagsSlugs)),
                row(QuestionSearchQueryDTO(query = null, tagSlugs = tags2.tagsSlugs)),

                // topics only
                row(QuestionSearchQueryDTO(query = null, topicSlugs = topics1.topicSlugs)),
                row(QuestionSearchQueryDTO(query = null, topicSlugs = topics2.topicSlugs)),

                // topics and slugs
                row(QuestionSearchQueryDTO(
                    query = null,
                    tagSlugs = tags1.tagsSlugs,
                    topicSlugs = topics1.topicSlugs
                )),
                row(QuestionSearchQueryDTO(
                    query = null,
                    tagSlugs = tags2.tagsSlugs,
                    topicSlugs = topics2.topicSlugs
                ))
            ) {
                val page = questionService.find(
                    paginationQuery = PaginationQuery.default(),
                    sortQuery = SortQuery.byIdDesc(),
                    search = it
                )
                page.content shouldHaveSize 5
            }
        }
    }
}
