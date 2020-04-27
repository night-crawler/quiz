package fm.force.quiz.core.repository

import fm.force.quiz.core.entity.QuizSession
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface QuizSessionRepository :
    CustomJpaRepository<QuizSession, Long>,
    JpaSpecificationExecutor<QuizSession>,
    CommonRepository<QuizSession> {

    @Modifying
    @Query("""
        update QuizSession s 
        set s.quiz.id = null 
        where s.quiz.id = :quizId
    """
    )
    fun unsetQuizId(quizId: Long)

    @Modifying
    @Query("""
        update QuizSession s
        set s.difficultyScale.id = null 
        where s.difficultyScale.id = :difficultyScaleId
    """
    )
    fun unsetDifficultyScaleId(difficultyScaleId: Long)
}
