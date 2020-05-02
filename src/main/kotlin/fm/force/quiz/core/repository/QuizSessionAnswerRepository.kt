package fm.force.quiz.core.repository

import fm.force.quiz.core.entity.QuizSessionAnswer
import fm.force.quiz.core.entity.Tag
import fm.force.quiz.security.entity.User
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

// for some reason defining of an interface with val tagId(): Long didn't work
interface TagStatsProjection {
    fun getTagId(): Long
    fun getIsCorrect(): Boolean
    fun getCount(): Long
}

interface TopicStatsProjection {
    fun getTopicId(): Long
    fun getIsCorrect(): Boolean
    fun getCount(): Long
}

data class TagHQLStats(
    val tag: Tag,
    val isCorrect: Boolean,
    val count: Long
)

@Repository
interface QuizSessionAnswerRepository :
    CustomJpaRepository<QuizSessionAnswer, Long>,
    JpaSpecificationExecutor<QuizSessionAnswer>,
    CommonRepository<QuizSessionAnswer> {

    @Query("""
        select new fm.force.quiz.core.repository.TagHQLStats(tags, qsa.isCorrect, count(tags))
        from QuizSessionAnswer qsa
        join qsa.quizSessionQuestion qsq 
        join qsq.originalQuestion q
        join q.tags tags
        where qsa.quizSession.id = :sessionId
        group by tags.id, qsa.isCorrect
    """)
    fun aggregateTagStatsHQL(@Param("sessionId") sessionId: Long) : List<TagHQLStats>

    @Query("""
        SELECT
            t.id as tagId, qsa.is_correct as isCorrect, count(*) as count
        FROM 
            quiz_session_answers qsa
        LEFT JOIN 
            quiz_session_questions qsq ON qsa.quiz_session_question_id = qsq.id
        LEFT JOIN 
            questions q ON qsq.original_question_id = q.id
        LEFT JOIN 
            questions__tags qt ON q.id = qt.question_id
        LEFT JOIN 
            tags t ON qt.tag_id = t.id
    
        WHERE 
            qsa.quiz_session_id = :sessionId
            AND t.id IS NOT NULL
        GROUP BY t.id, qsa.is_correct
    """, nativeQuery = true)
    fun aggregateTagStats(@Param("sessionId") sessionId: Long) : List<TagStatsProjection>

    @Query("""
        SELECT 
            t.id as topicId, qsa.is_correct as isCorrect, count(*) as count
        FROM 
            quiz_session_answers qsa
        LEFT JOIN 
            quiz_session_questions qsq ON qsa.quiz_session_question_id = qsq.id
        LEFT JOIN 
            questions q ON qsq.original_question_id = q.id
        LEFT JOIN 
            questions__topics qt ON q.id = qt.question_id
        LEFT JOIN 
            topics t ON qt.topic_id = t.id
        
        WHERE 
            qsa.quiz_session_id = :sessionId
            AND t.id IS NOT NULL
        GROUP BY t.id, qsa.is_correct
    """, nativeQuery = true)
    fun aggregateTopicStats(@Param("sessionId") sessionId: Long) : List<TopicStatsProjection>
}
