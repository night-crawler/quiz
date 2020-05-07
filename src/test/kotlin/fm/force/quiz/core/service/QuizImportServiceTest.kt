package io.kotlintest.provided.fm.force.quiz.core.service

import fm.force.quiz.core.service.AbstractCRUDServiceTest
import fm.force.quiz.core.service.QuizImportService

open class QuizImportServiceTest(
    private val quizImportService: QuizImportService
) : AbstractCRUDServiceTest() {
    private val sample = javaClass.classLoader.getResource("sample-quiz.yaml")!!.readText()

    init {
        "import" {
            quizImportService.import(sample)
        }
    }
}

