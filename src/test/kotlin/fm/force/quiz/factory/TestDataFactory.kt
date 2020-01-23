package fm.force.quiz.factory

import fm.force.quiz.common.getRandomString
import fm.force.quiz.core.entity.Answer
import fm.force.quiz.core.entity.DifficultyScale
import fm.force.quiz.core.entity.DifficultyScaleRange
import fm.force.quiz.core.entity.Question
import fm.force.quiz.core.entity.Quiz
import fm.force.quiz.core.entity.QuizQuestion
import fm.force.quiz.core.entity.QuizSession
import fm.force.quiz.core.entity.QuizSessionQuestion
import fm.force.quiz.core.entity.QuizSessionQuestionAnswer
import fm.force.quiz.core.entity.Tag
import fm.force.quiz.core.entity.Topic
import fm.force.quiz.core.repository.AnswerRepository
import fm.force.quiz.core.repository.DifficultyScaleRangeRepository
import fm.force.quiz.core.repository.DifficultyScaleRepository
import fm.force.quiz.core.repository.QuestionRepository
import fm.force.quiz.core.repository.QuizQuestionRepository
import fm.force.quiz.core.repository.QuizRepository
import fm.force.quiz.core.repository.QuizSessionQuestionAnswerRepository
import fm.force.quiz.core.repository.QuizSessionQuestionRepository
import fm.force.quiz.core.repository.QuizSessionRepository
import fm.force.quiz.core.repository.TagRepository
import fm.force.quiz.core.repository.TopicRepository
import fm.force.quiz.security.entity.User
import fm.force.quiz.security.repository.JpaUserRepository
import java.time.Duration
import java.time.Instant
import kotlin.random.Random
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class TestDataFactory(
    private val jpaUserRepository: JpaUserRepository,
    private val answerRepository: AnswerRepository,
    private val tagRepository: TagRepository,
    private val topicRepository: TopicRepository,
    private val questionRepository: QuestionRepository,
    private val difficultyScaleRepository: DifficultyScaleRepository,
    private val difficultyScaleRangeRepository: DifficultyScaleRangeRepository,
    private val quizRepository: QuizRepository,
    private val quizQuestionRepository: QuizQuestionRepository,
    private val quizSessionRepository: QuizSessionRepository,
    private val quizSessionQuestionRepository: QuizSessionQuestionRepository,
    private val quizSessionQuestionAnswerRepository: QuizSessionQuestionAnswerRepository
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
    ) = answerRepository.save(Answer(text = text, owner = owner))

    @Transactional
    fun getTag(
        owner: User = getUser(),
        name: String = getRandomString(16),
        slug: String = name
    ) = tagRepository.save(Tag(owner = owner, name = name, slug = slug))

    @Transactional
    fun getTopic(
        owner: User = getUser(),
        title: String = getRandomString(16)
    ) = topicRepository.save(Topic(owner = owner, title = title))

    @Transactional
    fun getQuestion(
        owner: User = getUser(),
        text: String = getRandomString(16),
        answers: MutableSet<Answer> = (1..5).map { getAnswer(owner = owner) }.toMutableSet(),
        topics: MutableSet<Topic> = (1..5).map { getTopic(owner = owner) }.toMutableSet(),
        tags: MutableSet<Tag> = (1..5).map { getTag(owner = owner) }.toMutableSet(),
        difficulty: Int = Random.nextInt(1000)
    ) = questionRepository.save(
        Question(
            owner = owner,
            text = text,
            answers = answers,
            topics = topics,
            correctAnswers = mutableSetOf(answers.random()),
            tags = tags,
            difficulty = difficulty
        )
    )

    @Transactional
    fun getDifficultyScale(
        owner: User = getUser(),
        name: String = getRandomString(16),
        max: Int = Random.nextInt(Int.MAX_VALUE - 1),
        createNRandomRanges: Int = 5
    ) = difficultyScaleRepository.save(
        DifficultyScale(
            owner = owner,
            name = name,
            max = max
        )
    ).also {
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
    ) = difficultyScaleRangeRepository.save(
        DifficultyScaleRange(
            owner = owner,
            title = title,
            difficultyScale = difficultyScale,
            min = min,
            max = max
        )
    )

    @Transactional
    fun getQuizQuestion(
        owner: User = getUser(),
        quiz: Quiz = getQuiz(owner = owner),
        question: Question = getQuestion(owner = owner),
        seq: Int = Random.nextInt(Int.MAX_VALUE - 1)
    ) = quizQuestionRepository.save(
        QuizQuestion(
            owner = owner,
            quiz = quiz,
            question = question,
            seq = seq
        )
    )

    @Transactional
    fun getQuiz(
        owner: User = getUser(),
        title: String = getRandomString(16),
        questions: Collection<Question> = (1..5).map { getQuestion(owner = owner) },
        topics: MutableSet<Topic> = (1..5).map { getTopic(owner = owner) }.toMutableSet(),
        tags: MutableSet<Tag> = (1..5).map { getTag(owner = owner) }.toMutableSet(),
        difficultyScale: DifficultyScale? = getDifficultyScale(owner = owner)
    ): Quiz {
        val quiz = quizRepository.save(
            Quiz(
                owner = owner,
                title = title,
                topics = topics,
                tags = tags,
                difficultyScale = difficultyScale
            )
        )
        quiz.quizQuestions = questions.mapIndexed { index, question ->
            getQuizQuestion(owner = owner, quiz = quiz, seq = index, question = question)
        }.toMutableList()
        return quizRepository.save(quiz)
    }

    @Transactional
    fun getQuizSession(
        owner: User = getUser(),
        quiz: Quiz = getQuiz(owner = owner),
        isCancelled: Boolean = false,
        isCompleted: Boolean = false,
        cancelledAt: Instant? = if (isCancelled) Instant.now() else null,
        completedAt: Instant? = if (isCompleted) Instant.now() else null,
        difficultyScale: DifficultyScale? = getDifficultyScale(owner = owner),
        validTill: Instant = Instant.now() + Duration.ofDays(1),
        doInstantiateQuizSessionQuestions: Boolean = true
    ): QuizSession {
        val quizSession = quizSessionRepository.save(
            QuizSession(
                owner = owner,
                quiz = quiz,
                validTill = validTill,
                isCancelled = isCancelled,
                isCompleted = isCompleted,
                cancelledAt = cancelledAt,
                completedAt = completedAt,
                difficultyScale = difficultyScale
            )
        )

        if (doInstantiateQuizSessionQuestions) {
            quiz.quizQuestions.forEachIndexed { index, it ->
                val quizQuestion = quizQuestionRepository.refresh(it)
                getQuizSessionQuestion(
                    owner = owner,
                    question = quizQuestion.question,
                    text = quizQuestion.question.text,
                    quiz = quiz,
                    quizSession = quizSession,
                    seq = index
                )
            }
        }
        return quizSession
    }

    @Transactional
    fun removeAllAnswers() = answerRepository.deleteAll()

    @Transactional
    fun getQuizSessionQuestion(
        owner: User = getUser(),

        text: String = getRandomString(),

        question: Question = getQuestion(owner = owner, text = text),
        quiz: Quiz = getQuiz(owner = owner, questions = listOf(question)),
        quizSession: QuizSession = getQuizSession(owner = owner, quiz = quiz),

        seq: Int = Random.nextInt(1000)
    ): QuizSessionQuestion {
        val q = quizSessionQuestionRepository.save(
            QuizSessionQuestion(
                owner = owner,
                quizSession = quizSession,
                originalQuestion = question,
                text = text, seq = seq
            )
        )

        val correctAnswers = question.correctAnswers
        val qsqaList = question.answers.map {
            QuizSessionQuestionAnswer(
                owner = owner,
                quizSession = quizSession,
                quizSessionQuestion = q,
                originalAnswer = it,
                text = it.text,
                isCorrect = it in correctAnswers
            )
        }
        quizSessionQuestionAnswerRepository.saveAll(qsqaList)
        return q
    }
}
