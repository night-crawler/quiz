package fm.force.quiz.core.service

import fm.force.quiz.core.repository.QuizRepository
import fm.force.quiz.core.repository.TagRepository
import fm.force.quiz.core.repository.TopicRepository
import io.kotlintest.matchers.collections.shouldHaveSize
import io.kotlintest.matchers.numerics.shouldBeExactly
import io.kotlintest.shouldNotBe

open class ConstraintsTest(
    difficultyScaleService: DifficultyScaleService,
    quizService: QuizService,
    questionService: QuestionService,
    quizSessionService: QuizSessionService,
    quizRepository: QuizRepository,
    tagService: TagService,
    tagRepository: TagRepository,
    topicService: TopicService,
    topicRepository: TopicRepository
) : AbstractCRUDServiceTest() {
    init {

        "should delete a quiz question despite any relations" {
            val session = testDataFactory.getQuizSession(owner = user, numQuestions = 2)
            val questionSize = transactionTemplate.execute {
                quizRepository.refresh(session.quiz!!).quizQuestions.size
            }!!
            questionService.delete(session.questions.first().originalQuestion!!.id)

            transactionTemplate.execute {
                val storedSession = quizSessionService.getEntity(session.id)
                // deletion of the question must not affect any existing sessions
                storedSession.questions.size shouldBeExactly 2

                // it must set one of the answer's relation to null
                storedSession.questions.mapNotNull { it.originalQuestion } shouldHaveSize 1

                // also it should not cascade too deep
                storedSession.quiz shouldNotBe null

                storedSession.quiz!!.quizQuestions.size shouldBeExactly questionSize - 1
            }
        }

        "should delete a quiz despite any relations" {
            val q = testDataFactory.getQuizSession(owner = user)
            quizService.delete(q.quiz!!.id)
        }

        "should delete a difficulty scale despite any relations" {
            val quizSession = testDataFactory.getQuizSession(owner = user)
            difficultyScaleService.delete(quizSession.difficultyScale!!.id)

            val quiz = testDataFactory.getQuiz(owner = user)
            difficultyScaleService.delete(quiz.difficultyScale!!.id)
        }

        "should delete tags" {
            val quiz = testDataFactory.getQuiz(owner = user)
            tagService.delete(quiz.tags.first().id)
            tagRepository.findAll().map {
                if (it.owner == user)
                    tagService.delete(it.id)
            }

            quizService.getEntity(quiz.id)
            questionService.getEntity(quiz.quizQuestions.first().question.id)
        }

        "should delete topics" {
            val quiz = testDataFactory.getQuiz(owner = user)
            topicService.delete(quiz.topics.first().id)
            topicRepository.findAll().map {
                if (it.owner == user)
                    topicService.delete(it.id)
            }

            quizService.getEntity(quiz.id)
            questionService.getEntity(quiz.quizQuestions.first().question.id)
        }
    }
}
