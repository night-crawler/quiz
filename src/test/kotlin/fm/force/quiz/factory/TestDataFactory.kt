package fm.force.quiz.factory

import fm.force.quiz.common.getRandomString
import fm.force.quiz.core.entity.Answer
import fm.force.quiz.core.entity.Question
import fm.force.quiz.core.entity.Tag
import fm.force.quiz.core.entity.Topic
import fm.force.quiz.core.repository.JpaAnswerRepository
import fm.force.quiz.core.repository.JpaQuestionRepository
import fm.force.quiz.core.repository.JpaTagRepository
import fm.force.quiz.core.repository.JpaTopicRepository
import fm.force.quiz.security.entity.User
import fm.force.quiz.security.repository.JpaUserRepository
import org.springframework.stereotype.Service
import javax.transaction.Transactional
import kotlin.random.Random

@Service
class TestDataFactory(
        private val jpaUserRepository: JpaUserRepository,
        private val jpaAnswerRepository: JpaAnswerRepository,
        private val jpaTagRepository: JpaTagRepository,
        private val jpaTopicRepository: JpaTopicRepository,
        private val jpaQuestionRepository: JpaQuestionRepository
) {
    @Transactional
    fun getUser(
            username: String = getRandomString(16),
            email: String = "$username@example.com"
    ) = jpaUserRepository.save(User(username = username, email = email))

    @Transactional
    fun getAnswer(
            text: String = getRandomString(16),
            owner: User = getUser()
    ) = jpaAnswerRepository.save(Answer(text = text, owner = owner))

    @Transactional
    fun getTag(
            owner: User = getUser(),
            name: String = getRandomString(16),
            slug: String = name
    ) = jpaTagRepository.save(Tag(owner = owner, name = name, slug = slug))

    @Transactional
    fun getTopic(
            owner: User = getUser(),
            title: String = getRandomString(16)
    ) = jpaTopicRepository.save(Topic(owner = owner, title = title))

    @Transactional
    fun getQuestion(
            owner: User = getUser(),
            text: String = getRandomString(16),
            answers: Set<Answer> = (1..5).map { getAnswer(owner = owner) }.toSet(),
            topics: Set<Topic> = (1..5).map { getTopic(owner = owner) }.toSet(),
            tags: Set<Tag> = (1..5).map { getTag(owner = owner) }.toSet(),
            difficulty: Int = Random.nextInt(1000)
    ) = jpaQuestionRepository.save(Question(
            owner = owner,
            text = text,
            answers = answers,
            topics = topics,
            correctAnswers = setOf(answers.random()),
            tags = tags,
            difficulty = difficulty
    ))
}
