package fm.force.quiz.core.service

import fm.force.quiz.core.dto.*
import fm.force.quiz.core.entity.Answer
import fm.force.quiz.core.entity.DifficultyScale
import fm.force.quiz.core.entity.DifficultyScaleRange
import fm.force.quiz.core.entity.Question
import fm.force.quiz.core.entity.Quiz
import fm.force.quiz.core.entity.QuizQuestion
import fm.force.quiz.core.entity.QuizSession
import fm.force.quiz.core.entity.QuizSessionAnswer
import fm.force.quiz.core.entity.QuizSessionQuestion
import fm.force.quiz.core.entity.Tag
import fm.force.quiz.core.entity.Topic
import fm.force.quiz.core.repository.AnswerRepository
import fm.force.quiz.core.repository.DifficultyScaleRangeRepository
import fm.force.quiz.core.repository.DifficultyScaleRepository
import fm.force.quiz.core.repository.QuestionRepository
import fm.force.quiz.core.repository.QuizQuestionRepository
import fm.force.quiz.core.repository.QuizRepository
import fm.force.quiz.core.repository.QuizSessionAnswerRepository
import fm.force.quiz.core.repository.QuizSessionQuestionRepository
import fm.force.quiz.core.repository.QuizSessionRepository
import fm.force.quiz.core.repository.TagRepository
import fm.force.quiz.core.repository.TopicRepository

typealias QuizSessionAnswerServiceType = AbstractPaginatedCRUDService<
    QuizSessionAnswer,
    QuizSessionAnswerRepository,
    QuizSessionAnswerPatchDTO,
    QuizSessionAnswerRestrictedDTO,
    QuizSessionAnswerSearchDTO>

typealias TopicServiceType = AbstractPaginatedCRUDService<
    Topic, TopicRepository, TopicPatchDTO, TopicFullDTO, SearchQueryDTO>

typealias TagServiceType = AbstractPaginatedCRUDService<
    Tag, TagRepository, TagPatchDTO, TagFullDTO, SearchQueryDTO>

typealias QuizSessionServiceType = AbstractPaginatedCRUDService<
    QuizSession, QuizSessionRepository, QuizSessionPatchDTO, QuizSessionFullDTO, QuizSessionSearchDTO>

typealias QuizServiceType = AbstractPaginatedCRUDService<
    Quiz, QuizRepository, QuizPatchDTO, QuizFullDTO, SearchQueryDTO>

typealias QuizQuestionServiceType = AbstractPaginatedCRUDService<
    QuizQuestion, QuizQuestionRepository, QuizQuestionPatchDTO, QuizQuestionFullDTO, QuizQuestionSearchDTO>

typealias QuestionServiceType = AbstractPaginatedCRUDService<
    Question, QuestionRepository, QuestionPatchDTO, QuestionFullDTO, SearchQueryDTO>

typealias DifficultyScaleServiceType = AbstractPaginatedCRUDService<
    DifficultyScale, DifficultyScaleRepository, DifficultyScalePatchDTO, DifficultyScaleFullDTO, SearchQueryDTO>

typealias DifficultyScaleRangeServiceType = AbstractPaginatedCRUDService<
    DifficultyScaleRange,
    DifficultyScaleRangeRepository,
    DifficultyScaleRangePatchDTO,
    DifficultyScaleRangeFullDTO,
    DifficultyScaleRangeSearchDTO>

typealias AnswerServiceType = AbstractPaginatedCRUDService<
    Answer, AnswerRepository, AnswerPatchDTO, AnswerFullDTO, SearchQueryDTO>

typealias QuizSessionQuestionType = AbstractPaginatedCRUDService<
    QuizSessionQuestion,
    QuizSessionQuestionRepository,
    QuizSessionQuestionPatchDTO,
    QuizSessionQuestionRestrictedDTO,
    QuizSessionQuestionSearchDTO>
