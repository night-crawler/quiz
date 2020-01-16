package fm.force.quiz.core.repository

import fm.force.quiz.core.entity.DifficultyScaleRange
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface DifficultyScaleRangeRepository :
    CustomJpaRepository<DifficultyScaleRange, Long>,
    JpaSpecificationExecutor<DifficultyScaleRange>,
    CommonRepository<DifficultyScaleRange> {

    @Query(
        """
        select t from DifficultyScaleRange t 
        where
            t.difficultyScale.id = :difficultyScaleId and
            t.min <= :max and
            :min <= t.max and t.id <> :exclude
    """
    )
    fun findIntersecting(difficultyScaleId: Long, min: Int, max: Int, exclude: Long): List<DifficultyScaleRange>
}
