package fm.force.quiz.common.mapper

import fm.force.quiz.common.dto.*
import fm.force.quiz.core.entity.Answer
import fm.force.quiz.core.entity.BaseQuizEntity
import fm.force.quiz.core.entity.DifficultyScale
import fm.force.quiz.core.entity.DifficultyScaleRange
import fm.force.quiz.core.entity.Question
import fm.force.quiz.core.entity.Quiz
import fm.force.quiz.core.entity.QuizQuestion
import fm.force.quiz.core.entity.QuizSession
import fm.force.quiz.core.entity.QuizSessionAnswer
import fm.force.quiz.core.entity.QuizSessionQuestion
import fm.force.quiz.core.entity.QuizSessionQuestionAnswer
import fm.force.quiz.core.entity.Tag
import fm.force.quiz.core.entity.Topic
import fm.force.quiz.security.entity.Role
import fm.force.quiz.security.entity.User
import org.springframework.data.domain.Page
import org.springframework.data.domain.Sort

fun Answer.toFullDTO() = AnswerFullDTO(
    id = id,
    text = text,
    createdAt = createdAt,
    updatedAt = updatedAt
)

fun Answer.toRestrictedDTO() = AnswerRestrictedDTO(id = id, text = text)

fun DifficultyScale.toFullDTO() = DifficultyScaleFullDTO(
    id = id,
    name = name,
    max = max,
    difficultyScaleRanges = difficultyScaleRanges.map { it.toFullDTO() },
    createdAt = createdAt,
    updatedAt = updatedAt
)

fun DifficultyScale.toRestrictedDTO() = DifficultyScaleRestrictedDTO(
    id = id,
    name = name,
    max = max,
    difficultyScaleRanges = difficultyScaleRanges.map { it.toRestrictedDTO() }
)

fun DifficultyScaleRange.toFullDTO() = DifficultyScaleRangeFullDTO(
    id = id,
    difficultyScale = difficultyScale.id,
    title = title,
    min = min,
    max = max,
    createdAt = createdAt,
    updatedAt = updatedAt
)

fun DifficultyScaleRange.toRestrictedDTO() = DifficultyScaleRangeRestrictedDTO(
    id = id,
    title = title,
    min = min,
    max = max
)

fun Sort.toDTO() = SortDTO(
    isSorted = isSorted,
    isUnsorted = isUnsorted,
    isEmpty = isEmpty
)

inline fun <T : BaseQuizEntity> Page<out T>.toDTO(serialize: (T) -> DTOMarker) = PageDTO(
    currentPage = number + 1,
    numberOfElements = numberOfElements,
    totalElements = totalElements,
    totalPages = totalPages,
    sort = sort.toDTO(),
    pageSize = size,
    isFirst = isFirst,
    isLast = isLast,
    content = content.map(serialize)
)

fun Question.toFullDTO() = QuestionFullDTO(
    id = id,
    title = title,
    text = text,
    help = help,
    answers = answers.map { it.toFullDTO() },
    correctAnswers = correctAnswers.map { it.toFullDTO() },
    tags = tags.map { it.toFullDTO() },
    topics = topics.map { it.toFullDTO() },
    difficulty = difficulty,
    createdAt = createdAt,
    updatedAt = updatedAt
)

fun Question.toRestrictedDTO() = QuestionRestrictedDTO(
    id = id,
    title = title,
    text = text,
    help = help,
    answers = answers.map { it.toRestrictedDTO() },
    difficulty = difficulty
)

fun Quiz.toFullDTO() = QuizFullDTO(
    id = id,
    title = title,
    tags = tags.map { it.toFullDTO() },
    topics = topics.map { it.toFullDTO() },
    quizQuestions = quizQuestions.map { it.toFullDTO() },
    difficultyScale = difficultyScale?.toFullDTO(),
    createdAt = createdAt,
    updatedAt = updatedAt
)

fun Quiz.toRestrictedDTO() = QuizRestrictedDTO(
    id = id,
    owner = owner.id,
    title = title,
    tags = tags.map { it.toRestrictedDTO() },
    topics = topics.map { it.toRestrictedDTO() },
    quizQuestions = quizQuestions.map { it.toRestrictedDTO() },
    difficultyScale = difficultyScale?.toRestrictedDTO()
)

fun QuizQuestion.toFullDTO() = QuizQuestionFullDTO(
    id = id,
    seq = seq,
    question = question.toFullDTO(),
    createdAt = createdAt,
    updatedAt = updatedAt
)

fun QuizQuestion.toRestrictedDTO() = QuizQuestionRestrictedDTO(
    id = id,
    seq = seq,
    question = question.toRestrictedDTO()
)

fun QuizSession.toFullDTO() = QuizSessionFullDTO(
    id = id,
    quiz = quiz.id,
    difficultyScale = difficultyScale?.id,
    validTill = validTill,
    isCompleted = isCompleted,
    isCancelled = isCancelled,
    completedAt = completedAt,
    cancelledAt = cancelledAt,
    createdAt = createdAt,
    updatedAt = updatedAt
)

fun QuizSessionAnswer.toFullDTO() = QuizSessionAnswerFullDTO(
    id = id
)

fun QuizSessionAnswer.toRestrictedDTO() = QuizSessionAnswerRestrictedDTO(
    id = id,
    session = quizSession.id,
    question = quizSessionQuestion.toRestrictedDTO(),
    answers = answers.map { it.toRestrictedDTO() }
)

fun QuizSessionQuestion.toFullDTO() = QuizSessionQuestionFullDTO(
    id = id
)

fun QuizSessionQuestion.toRestrictedDTO() = QuizSessionQuestionRestrictedDTO(
    id = id,
    title = title,
    text = text,
    help = help,
    answers = answers.map { it.toRestrictedDTO() },
    seq = seq
)

fun QuizSessionQuestionAnswer.toFullDTO() = QuizSessionQuestionAnswerFullDTO(
    id = id,
    quizSession = quizSession.id,
    quizSessionQuestion = quizSessionQuestion.id,
    originalAnswer = originalAnswer?.id,
    text = text,
    isCorrect = isCorrect
)

fun QuizSessionQuestionAnswer.toRestrictedDTO() = QuizSessionQuestionAnswerRestrictedDTO(
    id = id,
    text = text
)

fun Tag.toFullDTO() = TagFullDTO(
    id = id,
    name = name,
    slug = slug,
    createdAt = createdAt,
    updatedAt = updatedAt
)

fun Tag.toRestrictedDTO() = TagRestrictedDTO(
    id = id,
    name = name,
    slug = slug
)

fun Topic.toFullDTO() = TopicFullDTO(
    id = id,
    title = title,
    createdAt = createdAt,
    updatedAt = updatedAt
)

fun Topic.toRestrictedDTO() = TopicRestrictedDTO(id = id, title = title)

fun Role.toFullDTO() = RoleFullDTO(
    id = id,
    name = name
)

fun User.toFullDTO() = UserFullDTO(
    id = id,
    firstName = firstName,
    lastName = lastName,
    email = email,
    isActive = isActive,
    roles = roles.map { it.toFullDTO() },
    createdAt = createdAt,
    updatedAt = updatedAt
)
