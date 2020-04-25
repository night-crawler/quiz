package fm.force.quiz.core.controller

import fm.force.quiz.common.dto.DifficultyScalePatchDTO
import fm.force.quiz.common.dto.DifficultyScaleRangePatchDTO
import fm.force.quiz.factory.TestDataFactory

class DifficultyScaleControllerTest(testDataFactory: TestDataFactory) : AbstractControllerTest() {
    init {
        "CRUD controllers" should {
            "/difficultyScales" {
                smokeTestCRUD(
                    "/difficultyScales",
                    DifficultyScalePatchDTO("sample", max = 20),
                    DifficultyScalePatchDTO("Patched")
                )
            }
            "/difficultyScaleRanges" {
                val difficultyScaleIds = (1..5).map {
                    testDataFactory.getDifficultyScale(owner = user, createNRandomRanges = 0)
                }.map { it.id }.toMutableSet()
                smokeTestCRUD(
                    "/difficultyScaleRanges",
                    DifficultyScaleRangePatchDTO(
                        difficultyScale = difficultyScaleIds.random(),
                        title = "sample", min = 0, max = 1
                    ),
                    DifficultyScaleRangePatchDTO(title = "Patched")
                )
            }
        }
    }
}
