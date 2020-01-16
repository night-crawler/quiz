package fm.force.quiz.core.repository

import fm.force.quiz.core.entity.QuizSessionAnswer
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.stereotype.Repository

@Repository
interface QuizSessionAnswerRepository :
    CustomJpaRepository<QuizSessionAnswer, Long>,
    JpaSpecificationExecutor<QuizSessionAnswer>,
    CommonRepository<QuizSessionAnswer>
