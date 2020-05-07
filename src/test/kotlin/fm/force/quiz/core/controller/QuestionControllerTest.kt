package fm.force.quiz.core.controller

import fm.force.quiz.common.dto.QuestionPatchDTO
import fm.force.quiz.factory.TestDataFactory

class QuestionControllerTest(testDataFactory: TestDataFactory) : AbstractQuizControllerTest() {
    init {
        "CRUD controllers" should {
            "/questions" {
                val answerIds = (1..5)
                    .map { testDataFactory.getAnswer(owner = user) }
                    .map { it.id }.toMutableSet()
                smokeTestCRUD(
                    "/questions",
                    QuestionPatchDTO(
                        title = "sample",
                        text = "sample",
                        help = "",
                        answers = answerIds,
                        correctAnswers = answerIds
                    ),
                    QuestionPatchDTO("Patched")
                )
            }
        }
    }
}
