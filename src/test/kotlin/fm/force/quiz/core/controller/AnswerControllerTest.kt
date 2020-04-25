package fm.force.quiz.core.controller

import fm.force.quiz.common.dto.AnswerPatchDTO

class AnswerControllerTest : AbstractControllerTest() {
    init {
        "CRUD controllers" should {
            "/answers" {
                smokeTestCRUD("/answers", AnswerPatchDTO("sample"), AnswerPatchDTO("Patched"))
            }
        }
    }
}
