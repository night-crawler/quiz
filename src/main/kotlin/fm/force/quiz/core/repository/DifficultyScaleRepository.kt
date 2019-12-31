package fm.force.quiz.core.repository

import fm.force.quiz.core.entity.DifficultyScale
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.stereotype.Repository

@Repository
interface JpaDifficultyScaleRepository : CustomJpaRepository<DifficultyScale, Long>, JpaSpecificationExecutor<DifficultyScale>, CommonRepository<DifficultyScale>
