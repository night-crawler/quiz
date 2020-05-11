package fm.force.quiz.core.controller

import fm.force.quiz.common.dto.*
import fm.force.quiz.core.entity.Answer
import fm.force.quiz.core.entity.DifficultyScale
import fm.force.quiz.core.entity.DifficultyScaleRange
import fm.force.quiz.core.entity.Question
import fm.force.quiz.core.entity.Quiz
import fm.force.quiz.core.entity.QuizSession
import fm.force.quiz.core.entity.Tag
import fm.force.quiz.core.entity.Topic
import fm.force.quiz.core.repository.AnswerRepository
import fm.force.quiz.core.repository.DifficultyScaleRangeRepository
import fm.force.quiz.core.repository.DifficultyScaleRepository
import fm.force.quiz.core.repository.QuestionRepository
import fm.force.quiz.core.repository.QuizRepository
import fm.force.quiz.core.repository.QuizSessionRepository
import fm.force.quiz.core.repository.TagRepository
import fm.force.quiz.core.repository.TopicRepository

typealias AnswerControllerType = AbstractCRUDController<
    Answer, AnswerRepository, AnswerPatchDTO, AnswerFullDTO, SearchQueryDTO>

typealias DifficultyScaleControllerType = AbstractCRUDController<
    DifficultyScale, DifficultyScaleRepository, DifficultyScalePatchDTO, DifficultyScaleFullDTO, SearchQueryDTO>

typealias DifficultyScaleRangeControllerType = AbstractCRUDController<
    DifficultyScaleRange,
    DifficultyScaleRangeRepository,
    DifficultyScaleRangePatchDTO,
    DifficultyScaleRangeFullDTO,
    DifficultyScaleRangeSearchDTO>

typealias QuestionControllerType = AbstractCRUDController<
    Question, QuestionRepository, QuestionPatchDTO, QuestionFullDTO, QuestionSearchQueryDTO>

typealias QuizControllerType = AbstractCRUDController<
    Quiz, QuizRepository, QuizPatchDTO, QuizFullDTO, SearchQueryDTO>

typealias QuizSessionControllerType = AbstractCRUDController<
    QuizSession, QuizSessionRepository, QuizSessionPatchDTO, QuizSessionFullDTO, QuizSessionSearchDTO>

typealias TagControllerType = AbstractCRUDController<
    Tag, TagRepository, TagPatchDTO, TagFullDTO, TagSearchQueryDTO>

typealias TopicControllerType = AbstractCRUDController<
    Topic, TopicRepository, TopicPatchDTO, TopicFullDTO, TopicSearchQueryDTO>
