package fm.force.quiz.factory

import fm.force.quiz.common.ObjectId
import fm.force.quiz.common.getRandomString
import fm.force.quiz.core.entity.*
import fm.force.quiz.core.repository.*
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
        private val jpaQuestionRepository: JpaQuestionRepository,
        private val jpaDifficultyScaleRepository: JpaDifficultyScaleRepository,
        private val jpaDifficultyScaleRangeRepository: JpaDifficultyScaleRangeRepository,
        private val jpaQuizRepository: JpaQuizRepository,
        private val jpaQuizQuestionRepository: JpaQuizQuestionRepository
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
            answers: MutableSet<Answer> = (1..5).map { getAnswer(owner = owner) }.toMutableSet(),
            topics: MutableSet<Topic> = (1..5).map { getTopic(owner = owner) }.toMutableSet(),
            tags: MutableSet<Tag> = (1..5).map { getTag(owner = owner) }.toMutableSet(),
            difficulty: Int = Random.nextInt(1000)
    ) = jpaQuestionRepository.save(Question(
            owner = owner,
            text = text,
            answers = answers,
            topics = topics,
            correctAnswers = mutableSetOf(answers.random()),
            tags = tags,
            difficulty = difficulty
    ))

    @Transactional
    fun getDifficultyScale(
            owner: User = getUser(),
            name: String = getRandomString(16),
            max: Int = Random.nextInt(Int.MAX_VALUE - 1),
            createNRandomRanges: Int = 5
    ) = jpaDifficultyScaleRepository.save(DifficultyScale(
            owner = owner,
            name = name,
            max = max
    )).also {
        (1..(maxOf(createNRandomRanges, 0))).forEach { _ ->
            getDifficultyScaleRange(owner = owner, difficultyScale = it)
        }
    }

    @Transactional
    fun getDifficultyScaleRange(
            owner: User = getUser(),
            title: String = getRandomString(16),
            min: Int = 0,
            max: Int = Random.nextInt(Int.MAX_VALUE - 1),
            difficultyScale: DifficultyScale = getDifficultyScale(owner = owner)
    ) = jpaDifficultyScaleRangeRepository.save(DifficultyScaleRange(
            owner = owner,
            title = title,
            difficultyScale = difficultyScale,
            min = min,
            max = max
    ))

    @Transactional
    fun getQuizQuestion(
            owner: User = getUser(),
            quiz: Quiz = getQuiz(owner = owner),
            question: Question = getQuestion(owner = owner),
            seq: Int = Random.nextInt(Int.MAX_VALUE - 1)
    ) = jpaQuizQuestionRepository.save(QuizQuestion(
            owner = owner,
            quiz = quiz,
            question = question,
            seq = seq
    ))

    @Transactional
    fun getQuiz(
            owner: User = getUser(),
            title: String = getRandomString(16),
            questions: Collection<Question> = (1..5).map { getQuestion(owner = owner) },
            topics: MutableSet<Topic> = (1..5).map { getTopic(owner = owner) }.toMutableSet(),
            tags: MutableSet<Tag> = (1..5).map { getTag(owner = owner) }.toMutableSet(),
            difficultyScale: DifficultyScale? = getDifficultyScale(owner = owner)
    ): Quiz {
        val quiz = jpaQuizRepository.save(Quiz(
                owner = owner,
                title = title,
                topics = topics,
                tags = tags,
                difficultyScale = difficultyScale
        ))
        quiz.quizQuestions = questions.mapIndexed { index, question ->
            getQuizQuestion(owner = owner, quiz = quiz, seq = index, question = question)
        }.toMutableList()
        return jpaQuizRepository.save(quiz)
    }
}
