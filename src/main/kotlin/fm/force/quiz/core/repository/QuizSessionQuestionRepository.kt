package fm.force.quiz.core.repository

import fm.force.quiz.core.entity.QuizSessionQuestion
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface QuizSessionQuestionRepository :
    CustomJpaRepository<QuizSessionQuestion, Long>,
    JpaSpecificationExecutor<QuizSessionQuestion>,
    CommonRepository<QuizSessionQuestion> {

    @Query("""
        SELECT qsq.id
        FROM quiz_session_questions qsq 
        WHERE 
            qsq.quiz_session_id = :sessionId AND
            qsq.id NOT IN (
                SELECT qsa.quiz_session_question_id 
                FROM quiz_session_answers qsa 
                WHERE qsa.quiz_session_id = :sessionId
            ) 
    """, nativeQuery = true)
    fun getRemainingQuestionIds(@Param("sessionId") sessionId: Long): List<Long>

    @Query("""
        SELECT count(qsq.id)
        FROM quiz_session_questions qsq 
        WHERE 
            qsq.quiz_session_id = :sessionId AND
            qsq.id NOT IN (
                SELECT qsa.quiz_session_question_id 
                FROM quiz_session_answers qsa 
                WHERE qsa.quiz_session_id = :sessionId
            ) 
    """, nativeQuery = true)
    fun getRemainingQuestionCount(@Param("sessionId") sessionId: Long): Long
}
