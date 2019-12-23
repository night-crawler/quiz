package fm.force.quiz.core.repository

import fm.force.quiz.core.entity.DifficultyScaleRange
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.stereotype.Repository

@Repository
interface JpaDifficultyScaleRangeRepository :
        JpaRepository<DifficultyScaleRange, Long>,
        JpaSpecificationExecutor<DifficultyScaleRange>,
        CommonRepository<DifficultyScaleRange>
