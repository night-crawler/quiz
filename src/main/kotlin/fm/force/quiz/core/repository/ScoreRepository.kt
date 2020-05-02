package fm.force.quiz.core.repository

import fm.force.quiz.core.entity.Score
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.stereotype.Repository

@Repository
interface ScoreRepository :
    CustomJpaRepository<Score, Long>,
    JpaSpecificationExecutor<Score>,
    CommonRepository<Score> {

    fun findAllByQuizSessionId(quizSessionId: Long) : List<Score>
}
