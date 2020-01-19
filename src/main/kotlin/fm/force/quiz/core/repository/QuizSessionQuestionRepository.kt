package fm.force.quiz.core.repository

import fm.force.quiz.core.entity.QuizSessionQuestion
import org.springframework.data.jpa.repository.JpaSpecificationExecutor

interface QuizSessionQuestionRepository :
    CustomJpaRepository<QuizSessionQuestion, Long>,
    JpaSpecificationExecutor<QuizSessionQuestion>,
    CommonRepository<QuizSessionQuestion>
