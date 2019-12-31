package fm.force.quiz.core.repository

import fm.force.quiz.core.entity.Quiz
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.stereotype.Repository

@Repository
interface JpaQuizRepository : CustomJpaRepository<Quiz, Long>, JpaSpecificationExecutor<Quiz>, CommonRepository<Quiz>
