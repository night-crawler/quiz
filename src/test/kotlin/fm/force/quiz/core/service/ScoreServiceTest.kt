package fm.force.quiz.core.service

import fm.force.quiz.core.repository.QuestionRepository
import io.kotlintest.matchers.collections.shouldHaveSize
import io.kotlintest.matchers.numerics.shouldBeExactly


open class ScoreServiceTest(
    questionRepository: QuestionRepository,
    service: ScoreService
) : AbstractCRUDServiceTest() {

    init {
        "should count scores" {
            val session = testDataFactory.getQuizSession(owner = user, numQuestions = 20, isCompleted = true)
            val tags = (0..1).map { testDataFactory.getTag(owner = user) }.toMutableSet()
            val topics = (0..1).map { testDataFactory.getTopic(owner = user) }.toMutableSet()

            session.questions.forEachIndexed { index, quizSessionQuestion ->
                testDataFactory.getQuizSessionAnswer(
                    owner = user,
                    quiz = session.quiz!!,
                    quizSession = session,
                    isCorrect = index % 2 == 0,
                    quizSessionQuestion = quizSessionQuestion
                )
                quizSessionQuestion.originalQuestion?.tags = tags
                quizSessionQuestion.originalQuestion?.topics = topics
                quizSessionQuestion.originalQuestion?.let {
                    questionRepository.save(it)
                }
            }
            val report = service.prepareReport(session.id)
            // two correct answers and two wrong
            report.tagScores shouldHaveSize 4
            report.topicScores shouldHaveSize 4

            // there are 20 tags and 20 topics;
            // half of answers is correct, each isCorrect bucket must have 10 answers in it
            report.tagScores.forEach { it.count shouldBeExactly 10 }
            report.topicScores.forEach { it.count shouldBeExactly 10 }
            report.tagScores.filter { it.isCorrect } shouldHaveSize 2
            report.topicScores.filter { it.isCorrect } shouldHaveSize 2
        }

    }
}
