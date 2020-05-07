package fm.force.quiz.core.controller

import fm.force.quiz.common.dto.DifficultyScalePatchDTO
import fm.force.quiz.common.dto.DifficultyScaleRangePatchDTO
import fm.force.quiz.factory.TestDataFactory

class DifficultyScaleControllerTest(testDataFactory: TestDataFactory) : AbstractQuizControllerTest() {
    init {
        "CRUD controllers" should {
            "/difficultyScales" {
                smokeTestCRUD(
                    path = "/difficultyScales",
                    create = DifficultyScalePatchDTO(
                        "sample", max = 20,
                        ranges = listOf(
                            DifficultyScaleRangePatchDTO(title = "sample title -222", min = 0, max = 2)
                        )
                    ),
                    patch = DifficultyScalePatchDTO("Patched")
                )
            }
            "/difficultyScaleRanges" {
                val difficultyScaleIds = (1..5).map {
                    testDataFactory.getDifficultyScale(owner = user, createNRandomRanges = 0)
                }.map { it.id }.toMutableSet()
                smokeTestCRUD(
                    path = "/difficultyScaleRanges",
                    create = DifficultyScaleRangePatchDTO(
                        difficultyScale = difficultyScaleIds.random(),
                        title = "sample", min = 0, max = 1
                    ),
                    patch = DifficultyScaleRangePatchDTO(title = "Patched")
                )
            }
        }
    }
}
