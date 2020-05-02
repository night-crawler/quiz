package fm.force.quiz.core.service

import fm.force.quiz.common.dto.QuizSessionScoresDTO
import fm.force.quiz.common.mapper.toRestrictedDTO
import fm.force.quiz.core.entity.QuizSession
import fm.force.quiz.core.entity.Score
import fm.force.quiz.core.exception.NotFoundException
import fm.force.quiz.core.repository.QuizSessionAnswerRepository
import fm.force.quiz.core.repository.QuizSessionRepository
import fm.force.quiz.core.repository.ScoreRepository
import fm.force.quiz.core.repository.TagRepository
import fm.force.quiz.core.repository.TopicRepository
import fm.force.quiz.security.service.AuthenticationFacade
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ScoreService(
    private val quizSessionAnswerRepository: QuizSessionAnswerRepository,
    private val sessionRepository: QuizSessionRepository,
    private val scoreRepository: ScoreRepository,
    private val tagRepository: TagRepository,
    private val topicRepository: TopicRepository,
    private val authFacade: AuthenticationFacade
) {

    @Transactional
    fun prepareReport(sessionId: Long): QuizSessionScoresDTO {
        val session = getCompletedOwnedSession(sessionId)
        val tagScores = prepareTagsReport(sessionId, session)
        val topicScores = prepareTopicsReport(sessionId, session)

        return QuizSessionScoresDTO(
            topicScores = topicScores.toRestrictedDTO(),
            tagScores = tagScores.toRestrictedDTO()
        )
    }

    private fun getCompletedOwnedSession(sessionId: Long): QuizSession =
        sessionRepository.findByIdAndOwner(sessionId, authFacade.user).orElseThrow {
            NotFoundException(sessionId, QuizSession::class)
        }.also {
            if (!it.isCompleted) {
                throw IllegalStateException("You cannot prepare a report on an incomplete session")
            }
        }

    @Transactional
    fun getOrCreateReport(sessionId: Long): QuizSessionScoresDTO {
        getCompletedOwnedSession(sessionId)
        val scores = scoreRepository.findAllByQuizSessionId(sessionId)
        if (scores.isEmpty())
            return prepareReport(sessionId)

        val topicScores = scores.filter { it.topic != null }
        val tagScores = scores.filter { it.tag != null }

        return QuizSessionScoresDTO(
            topicScores = topicScores.toRestrictedDTO(),
            tagScores = tagScores.toRestrictedDTO()
        )
    }

    private fun prepareTopicsReport(sessionId: Long, session: QuizSession): MutableList<Score> {
        val topicStats = quizSessionAnswerRepository.aggregateTopicStats(sessionId)
        val topicMap = topicStats
                .map { it.getTopicId() }
                .let { topicRepository.findEntitiesById(it) }
                .map { it.id to it }
                .toMap()

        return scoreRepository.saveAll(topicStats.map {
            Score(
                owner = authFacade.user,
                quizSession = session,
                tag = null,
                topic = topicMap[it.getTopicId()],
                isCorrect = it.getIsCorrect(),
                count = it.getCount()
            )
        })
    }

    private fun prepareTagsReport(sessionId: Long, session: QuizSession): MutableList<Score> {
        val tagStats = quizSessionAnswerRepository.aggregateTagStats(sessionId)
        val tagMap = tagStats
                .map { it.getTagId() }
                .let { tagRepository.findEntitiesById(it) }
                .map { it.id to it }
                .toMap()

        return scoreRepository.saveAll(tagStats.map {
            Score(
                owner = authFacade.user,
                quizSession = session,
                tag = tagMap[it.getTagId()],
                topic = null,
                isCorrect = it.getIsCorrect(),
                count = it.getCount()
            )
        })
    }

}
