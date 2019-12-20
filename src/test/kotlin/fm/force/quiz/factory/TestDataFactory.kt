package fm.force.quiz.factory

import fm.force.quiz.common.getRandomString
import fm.force.quiz.core.entity.Answer
import fm.force.quiz.core.entity.Tag
import fm.force.quiz.core.entity.Topic
import fm.force.quiz.core.repository.JpaAnswerRepository
import fm.force.quiz.core.repository.JpaTagRepository
import fm.force.quiz.core.repository.JpaTopicRepository
import fm.force.quiz.security.entity.User
import fm.force.quiz.security.repository.JpaUserRepository
import org.springframework.stereotype.Service
import javax.transaction.Transactional

@Service
class TestDataFactory(
        private val jpaUserRepository: JpaUserRepository,
        private val jpaAnswerRepository: JpaAnswerRepository,
        private val jpaTagRepository: JpaTagRepository,
        private val jpaTopicRepository: JpaTopicRepository
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
}
