package fm.force.quiz.core.service

import fm.force.quiz.configuration.properties.QuizValidationProperties

open class QuizServiceTest(
    validationProps: QuizValidationProperties,
    quizService: QuizService
) : AbstractCRUDServiceTest() {

    init {
//        "should validate" {
//            forall(
//                row(QuizPatchDTO()),
//                row(QuizPatchDTO(title = "")),
//                row(QuizPatchDTO(title = getRandomString(validationProps.minTitleLength), topics = setOf(1, 2))),
//                row(
//                    QuizPatchDTO(
//                        title = getRandomString(validationProps.minTitleLength),
//                        questions = setOf(1, 3),
//                        tags = setOf(1, 2),
//                        topics = setOf(1, 2),
//                        difficultyScale = 123
//                    )
//                )
//            ) {
//                shouldThrow<ValidationError> { quizService.create(it) }
//            }
//        }
//
//        "should create" {
//            val questions = (1..5).map { testDataFactory.getQuestion(owner = user) }
//            val tags = questions.map { it.tags }.flatten()
//            val topics = questions.map { it.topics }.flatten()
//            val scale = testDataFactory.getDifficultyScale(owner = user)
//
//            val dto = QuizPatchDTO(
//                title = getRandomString(validationProps.minTitleLength),
//                questions = questions.map { it.id }.toSet(),
//                tags = setOf(tags.random().id, tags.random().id),
//                topics = setOf(topics.random().id, topics.random().id),
//                difficultyScale = scale.id
//            )
//            quizService.create(dto)
//        }
//
//        "should paginate" {
//            (1..5).map { testDataFactory.getQuiz(owner = user, title = "This title is exciting $it") }
//            (1..5).map { testDataFactory.getQuiz(owner = alien, title = "This title is exciting $it") }
//            val page = quizService.find(
//                PaginationQuery.default(),
//                SortQuery.byIdDesc(),
//                SearchQueryDTO("exciTINg")
//            )
//            page.content shouldHaveSize 5
//        }
//
//        "should patch" {
//            val quiz = testDataFactory.getQuiz(owner = user)
//            val patch = QuizPatchDTO(
//                title = "sample!",
//                questions = setOf(),
//                tags = setOf(),
//                topics = setOf()
//                // Cannot set it to null
//                // difficultyScale = null
//            )
//            val patched = quizService.patch(quiz.id, patch)
//            patched.title shouldBe "sample!"
//            patched.updatedAt shouldNotBe quiz.updatedAt
//            patched.tags shouldHaveSize 0
//            patched.topics shouldHaveSize 0
//        }

        "should delete" {
            val q = testDataFactory.getQuizSession(owner = user)
            quizService.delete(q.quiz!!.id)
        }
    }
}
