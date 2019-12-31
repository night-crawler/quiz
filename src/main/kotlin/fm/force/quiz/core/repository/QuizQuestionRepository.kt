package fm.force.quiz.core.repository

import fm.force.quiz.core.entity.QuizQuestion
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.stereotype.Repository


@Repository
interface JpaQuizQuestionRepository : CustomJpaRepository<QuizQuestion, Long>, JpaSpecificationExecutor<QuizQuestion>, CommonRepository<QuizQuestion>
