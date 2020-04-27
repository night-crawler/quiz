package fm.force.quiz.core.repository

import fm.force.quiz.core.entity.Quiz
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface QuizRepository : CustomJpaRepository<Quiz, Long>, JpaSpecificationExecutor<Quiz>, CommonRepository<Quiz> {
    @Modifying
    @Query("""
        update Quiz q
        set q.difficultyScale.id = null 
        where q.difficultyScale.id = :difficultyScaleId
    """
    )
    fun unsetDifficultyScaleId(difficultyScaleId: Long)
}
