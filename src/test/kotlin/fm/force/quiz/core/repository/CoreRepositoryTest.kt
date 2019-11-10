package io.kotlintest.provided.fm.force.quiz.core.repository

import fm.force.quiz.core.entity.*
import fm.force.quiz.core.repository.*
import fm.force.quiz.security.entity.User
import fm.force.quiz.security.repository.JpaUserRepository
import io.kotlintest.provided.fm.force.quiz.TestConfiguration
import io.kotlintest.provided.fm.force.quiz.YamlPropertyLoaderFactory
import io.kotlintest.specs.StringSpec
import org.springframework.context.annotation.PropertySource
import org.springframework.test.context.ContextConfiguration

@PropertySource("classpath:application-test.yaml", factory = YamlPropertyLoaderFactory::class)
@ContextConfiguration(classes = [TestConfiguration::class])
open class CoreRepositoryTest(
        jpaQuestionRepository: JpaQuestionRepository,
        jpaAnswerRepository: JpaAnswerRepository,
        jpaUserRepository: JpaUserRepository,
        jpaQuizRepository: JpaQuizRepository,
        jpaQuizSessionAnswerRepository: JpaQuizSessionAnswerRepository,
        jpaQuizSessionRepository: JpaQuizSessionRepository,
        jpaTagRepository: JpaTagRepository,
        jpaTopicRepository: JpaTopicRepository,
        jpaDifficultyScaleRepository: JpaDifficultyScaleRepository,
        jpaDifficultyScaleRangeRepository: JpaDifficultyScaleRangeRepository
) : StringSpec() {

    init {
        "should create all needed instance" {
            val repeats = 0..4

            val user = jpaUserRepository.save(User("sample@example.com", email = "sample@example.com"))

            val tags = jpaTagRepository.saveAll(repeats.map {
                Tag(name = "tag sample #$it", slug = "tag-sample-$it", owner = user)
            })

            val topics = jpaTopicRepository.saveAll(repeats.map {
                Topic(owner = user, title = "Sample topic $it")
            })

            val difficultyScale = jpaDifficultyScaleRepository.save(DifficultyScale(owner = user, name = "Super Scale"))
            val difficultyScaleRanges = jpaDifficultyScaleRangeRepository.saveAll(repeats.map {
                DifficultyScaleRange(
                        owner = user,
                        difficultyScale = difficultyScale,
                        title = "Easy",
                        min = it * 1, max = it * 2)
            })

            val answersSet1 = jpaAnswerRepository.saveAll(repeats.map {
                Answer(owner = user, text = "Answer set 1: #$it")
            })

            val answersSet2 = jpaAnswerRepository.saveAll(repeats.map {
                Answer(owner = user, text = "Answer set 2: #$it")
            })

            val question1 = jpaQuestionRepository.save(Question(
                    owner = user, text = "Sample question 1", answers = answersSet1.toSet(),
                    correctAnswers = setOf(answersSet1[0]),
                    topics = setOf(topics[0], topics[1]),
                    difficulty = 2
            ))

            val question2 = jpaQuestionRepository.save(Question(
                    owner = user, text = "Sample question 1", answers = answersSet2.toSet(),
                    correctAnswers = setOf(answersSet2[0]),
                    topics = setOf(topics[2], topics[3]),
                    difficulty = 3
            ))

            val quiz = jpaQuizRepository.save(Quiz(
                    owner = user, title = "Test Quiz",
                    questions = setOf(question1, question2),
                    tags = setOf(tags[0]),
                    difficultyScale = difficultyScale,
                    topics = setOf(topics[2], topics[3])
            ))

            val quizSession = jpaQuizSessionRepository.save(QuizSession(
                    owner = user,
                    quiz = quiz,
                    difficultyScale = difficultyScale
            ))

            val quizSessionAnswer = jpaQuizSessionAnswerRepository.save(QuizSessionAnswer(
                    owner = user,
                    quiz = quiz,
                    quizSession = quizSession,
                    question = question1,
                    answer = answersSet1.last()
            ))
        }
    }
}
